package org.kafkapre.sentence.generator.controller;

import org.bson.types.ObjectId;
import org.kafkapre.sentence.generator.AppConfiguration;
import org.kafkapre.sentence.generator.model.InfoMessage;
import org.kafkapre.sentence.generator.model.Sentence;
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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.kafkapre.sentence.generator.persistence.impl.MongoSentenceDAL.computeTextHash;

@Component
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path(RestPaths.sentencesPath)
public class SentenceController {

    private AppConfiguration configuration;

    @Autowired
    public void setConfiguration(AppConfiguration configuration) {
        this.configuration = configuration;
    }

    private WordDAL wordDAL;

    @Autowired
    public void setWordDAL(WordDAL wordDAL) {
        this.wordDAL = wordDAL;
    }

    @Autowired
    private SentenceDAL sentenceDAL;

    @GET
    public List<String> getSentences() {
        List<Sentence> sentences = sentenceDAL.getAllSentences();  // TODO better
        return sentences.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
    }

    @GET
    @Path("/{id}")
    public Response getSentence(@PathParam("id") String id) {
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
            return Response.status(OK).entity(res.get()).build();
        } else {
            InfoMessage response = new InfoMessage("Sentence with id [%s] not found.", id);
            return Response.status(NOT_FOUND).entity(response).build();
        }
    }

    @GET
    @Path("/{id}/yodaTalk")
    public Response getSentenceYodaTalk(@PathParam("id") String id) {
        ObjectId objectId = createObjectId(id);
        if (objectId == null) {
            InfoMessage response = new InfoMessage("Bad format of id [%s].", id);
            return Response.status(BAD_REQUEST).entity(response).build();
        }

        Optional<Sentence> res = sentenceDAL.getSentence(objectId);
        if (res.isPresent()) {
            return Response.status(OK).entity(res.get()).build();
        } else {
            InfoMessage response = new InfoMessage("Sentence with id [%s] not found.", id);
            return Response.status(NOT_FOUND).entity(response).build();
        }
    }

    @POST
    @Path("/generate")
    public Response postSentence() {
        Sentence sentence = null;
        try {
            sentence = generateSentence();
        } catch (RuntimeException ex) {
            InfoMessage response = new InfoMessage("Sentence cannot be generated words" +
                    " some are missing. [%s]", ex.getMessage());
            return Response.status(BAD_REQUEST).entity(response).build();
        }
        return Response.status(CREATED).entity(sentence).build();
    }

    private Sentence generateSentence() {
        Optional<Word> noun = wordDAL.getRandomWord(WordCategory.NOUN);
        Optional<Word> verb = wordDAL.getRandomWord(WordCategory.VERB);
        Optional<Word> adjective = wordDAL.getRandomWord(WordCategory.ADJECTIVE);

        Words words = new Words(noun, verb, adjective);
        words.validate();

        List<Sentence> sentences = sentenceDAL.getSentences(words.hashCode(), words);
        if (!sentences.isEmpty()) {
            // TODO increment number;
            if(sentences.size() > 0){
                // TODO log warning;
            }
            return sentences.get(0);
        }

        return sentenceDAL.createAndStoreSentence(words);
    }

    private Sentence sameSentence(Words words) {
        int hash = words.hashCode();
        List<Sentence> sentences = sentenceDAL.getSentences(hash);
        for (Sentence s : sentences) {
            if (s.getWords().equals(words)){
                return s;
            }
        }
        return null;
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