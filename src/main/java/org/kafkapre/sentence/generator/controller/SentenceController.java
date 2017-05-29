package org.kafkapre.sentence.generator.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.kafkapre.sentence.generator.AppConfiguration;
import org.kafkapre.sentence.generator.model.BaseSentence;
import org.kafkapre.sentence.generator.model.InfoMessage;
import org.kafkapre.sentence.generator.model.Sentence;
import org.kafkapre.sentence.generator.model.SentenceJSON;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.WordCategory;
import org.kafkapre.sentence.generator.model.Words;
import org.kafkapre.sentence.generator.persistence.api.SentenceDAL;
import org.kafkapre.sentence.generator.persistence.api.WordDAL;
import org.kafkapre.sentence.generator.persistence.impl.MongoSentenceDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;

@Component
@Produces({MediaType.APPLICATION_JSON})
@Path(RestPaths.sentencesPath)
public class SentenceController {

    private static final Logger logger = LogManager.getLogger(SentenceController.class);

    static final String resourceHrefHeaderName = "resourceHref";

    @Autowired
    private WordDAL wordDAL;

    @Autowired
    private SentenceDAL sentenceDAL;

    @GET
    public List<SentenceJSON> getSentences() {
        logger.debug("Method getSentences called.");
        List<BaseSentence> sentences = sentenceDAL.getAllBaseSentences();
        return sentences.stream().map(s -> s.generateBaseSentenceJSON()).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getSentence(@PathParam("id") String id) {
        logger.debug("Method getSentence called.");
        Function<Sentence, SentenceJSON> jsonFunction = sentence -> {
            return sentence.generateSentenceJSON();
        };
        return getSentence(id, jsonFunction);
    }

    @GET
    @Path("/{id}/yodaTalk")
    public Response getSentenceYodaTalk(@PathParam("id") String id) {
        logger.debug("Method getSentenceYodaTalk called.");
        Function<Sentence, SentenceJSON> jsonFunction = sentence -> {
            return sentence.generateYodaSentenceJSON();
        };
        return getSentence(id, jsonFunction);
    }

    private Response getSentence(String id, Function<Sentence, SentenceJSON> responseJsonFunction) {
        ObjectId objectId = createObjectId(id);
        if (objectId == null) {
            InfoMessage response = new InfoMessage("Bad format of id [%s].", id);
            return Response.status(BAD_REQUEST).entity(response).build();
        }

        if (!sentenceDAL.incrementSentenceShowDisplayCount(objectId)) {
            InfoMessage response = new InfoMessage("Was not able to increment showDisplayCount of sentence with id [%s]", id);
            return Response.status(INTERNAL_SERVER_ERROR).entity(response).build();
        }

        Optional<Sentence> res = sentenceDAL.getSentence(objectId);
        if (res.isPresent()) {
            return Response.status(OK).entity(responseJsonFunction.apply(res.get())).build();
        } else {
            InfoMessage response = new InfoMessage("Sentence with id [%s] not found.", id);
            return Response.status(NOT_FOUND).entity(response).build();
        }
    }

    @POST
    @Path("/generate")
    public Response postSentence() {
        logger.debug("Method postSentence called.");
        Words words = null;
        try {
            words = generateRandomSentenceWords();
        } catch (RuntimeException ex) {
            InfoMessage response = new InfoMessage("Sentence cannot be generated words" +
                    " some are missing. [%s]", ex.getMessage());
            return Response.status(BAD_REQUEST).entity(response).build();
        }

        return generateResponseForNewSentence(words);
    }

    private Words generateRandomSentenceWords() {
        Optional<Word> noun = wordDAL.getRandomWord(WordCategory.NOUN);
        Optional<Word> verb = wordDAL.getRandomWord(WordCategory.VERB);
        Optional<Word> adjective = wordDAL.getRandomWord(WordCategory.ADJECTIVE);

        Words words = new Words(noun, verb, adjective);
        words.validate();
        return words;
    }

    private Response generateResponseForNewSentence(Words words) {

        List<Sentence> sentences = sentenceDAL.getSentences(words);
        if (!sentences.isEmpty()) {
            if (sentences.size() > 1) {
                logger.error("There are more sentences with same words. [{}]", words);
            }
            Sentence sentence = sentences.get(0);
            boolean isOk = sentenceDAL.incrementSentenceSameGeneratedCount(sentence.getId());
            if (!isOk) {
                InfoMessage entity = new InfoMessage("Generation of new sentence failed.");
                return Response.status(INTERNAL_SERVER_ERROR).entity(entity).build();
            } else {
                SentenceJSON entity = sentenceDAL.getSentence(sentence.getId()).get().generateSentenceJSON();
                return Response.status(CONFLICT).entity(entity).header(resourceHrefHeaderName, entity.getHref()).build();
            }
        } else {
            SentenceJSON entity = sentenceDAL.createAndStoreSentence(words).generateSentenceJSON();
            return Response.status(CREATED).entity(entity).header(resourceHrefHeaderName, entity.getHref()).build();
        }
    }

    private ObjectId createObjectId(String id) {
        ObjectId objectId;
        try {
            objectId = new ObjectId(id);
        } catch (RuntimeException ex) {
            return null;
        }
        return objectId;
    }

}