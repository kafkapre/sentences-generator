package org.kafkapre.sentence.generator.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kafkapre.sentence.generator.AppConfiguration;
import org.kafkapre.sentence.generator.model.InfoMessage;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.persistence.api.WordDAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;

@Component
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path(RestPaths.wordsPath)
public class WordController {

    private static final Logger logger = LogManager.getLogger(WordController.class);

    private HashSet rejectedWords;
    @Autowired
    public void setConfiguration(AppConfiguration configuration) {
        rejectedWords = new HashSet(configuration.getRejectedWords());
    }

    private WordDAL wordDAL;

    @Autowired
    public void setWordDAL(WordDAL wordDAL) {
        this.wordDAL = wordDAL;
    }

    @GET
    public List<Word> getAllWords() {
        logger.debug("Method createAndStoreSentence called.");
        List<Word> words = wordDAL.getAllWords();
        return words;
    }

    @GET
    @Path("/rejected")
    public Set<String> getRejectedWords() {
        logger.debug("Method createAndStoreSentence called.");
        return rejectedWords;
    }

    @GET
    @Path("/{id}")
    public Response getWord(@PathParam("id") String id) {
        logger.debug("Method createAndStoreSentence called.");
        Optional<Word> res = wordDAL.getWord(id);
        if (res.isPresent()) {
            return Response.status(OK).entity(res.get()).build();
        } else {
            InfoMessage response = new InfoMessage("Word with id [%s] not found.", id);
            return Response.status(NOT_FOUND).entity(response).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response putWord(@PathParam("id") String id, Word word) {
        logger.debug("Method createAndStoreSentence called.");
        // TODO check what happen when word cannot be parsed.

        if (!rejectedWords.contains(id)) {
            word.setText(id);
            wordDAL.putWord(word);
            return Response.status(OK).entity(word).build();
        } else {
            InfoMessage response = new InfoMessage("Word with id [%s] is prohibited.", id);
            return Response.status(METHOD_NOT_ALLOWED).entity(response).build();
        }
    }

}