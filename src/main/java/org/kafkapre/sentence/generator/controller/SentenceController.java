package org.kafkapre.sentence.generator.controller;

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
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path(RestPaths.sentencesPath)
public class SentenceController {

    @Autowired
    private WordDAL wordDAL;

    @Autowired
    private SentenceDAL sentenceDAL;

    @GET
    public List<SentenceJSON> getSentences() {
        List<BaseSentence> sentences = sentenceDAL.getAllBaseSentences();
        return sentences.stream().map(s -> s.generateBaseSentenceJSON()).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getSentence(@PathParam("id") String id) {
        Function<Sentence, SentenceJSON> jsonFunction = sentence -> {
            return sentence.generateSentenceJSON();
        };
        return getSentence(id, jsonFunction);
    }

    @GET
    @Path("/{id}/yodaTalk")
    public Response getSentenceYodaTalk(@PathParam("id") String id) {
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
        Object entity = null;
        Response.Status status = null;

        List<Sentence> sentences = sentenceDAL.getSentences(words);
        if (!sentences.isEmpty()) {
            if (sentences.size() > 0) {
                // TODO log warning;
            }
            Sentence sentence = sentences.get(0);
            boolean isOk = sentenceDAL.incrementSentenceSameGeneratedCount(sentence.getId());
            if (!isOk) {
                entity = new InfoMessage("Generation of new sentence failed.");
                status = INTERNAL_SERVER_ERROR;
            } else {
                entity = sentence;
                status = CONFLICT;
            }
        } else {
            entity = sentenceDAL.createAndStoreSentence(words);
            status = CREATED;
        }
        return Response.status(status).entity(entity).build();
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