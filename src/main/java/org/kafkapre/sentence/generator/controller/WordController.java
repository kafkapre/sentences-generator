package org.kafkapre.sentence.generator.controller;

import org.kafkapre.sentence.generator.AppConfiguration;
import org.kafkapre.sentence.generator.model.InfoMessage;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.WordCategory;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;

@Component
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path(RestPaths.wordPath)
public class WordController {

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

    @GET
    public List<String> getAllWords() {
        List<Word> words = wordDAL.getAllWords();  // TODO better
        return words.stream().map(w -> w.getText()).collect(Collectors.toList());
    }

    @GET
    @Path("/rejected")
    public Set<String> getRejectedWords() {
        return configuration.getRejectedWords();
    }

    @GET
    @Path("/{id}")
    public Response getWord(@PathParam("id") String id) {
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
        System.out.println(word);
        System.out.println(configuration.getRejectedWords().contains(id));

        if (!configuration.getRejectedWords().contains(id)) {
            word.setText(id);
            wordDAL.putWord(word);
            return Response.status(OK).entity(word).build();
        } else {
            InfoMessage response = new InfoMessage("Word with id [%s] is prohibited.", id);
            return Response.status(METHOD_NOT_ALLOWED).entity(response).build();
        }
    }

//    @PUT
//    @Path("/{id}")
//    public Response putCsv(@PathParam("id") int id, String csvData) {
//        logger.debug("PUT: putCsv endpoint requested");
//
//        if (id <= 0) {
//            InfoMessage response = new InfoMessage(String.format("Id must be greater than 0. Your id is [%d]", id));
//            return Response.status(PRECONDITION_FAILED).entity(response).build();
//        }
//        return store(id, csvData);
//    }

}