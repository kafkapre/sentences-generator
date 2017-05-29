package org.kafkapre.sentence.generator.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.OK;

@Path("")
@Produces({MediaType.APPLICATION_JSON})
public class RootController {


    private static final Logger logger = LogManager.getLogger(RootController.class);

    private static class RootApi {

        @JsonProperty
        private final String wordsPath = RestPaths.wordsPath;

        @JsonProperty
        private final String sentencesPath = RestPaths.sentencesPath;
    }

    private static final RootApi rootApi = new RootApi();

    @GET
    public Response getRootApi() {
        logger.debug("GET: root api endpoint requested");
        return Response.status(OK).entity(rootApi).build();
    }
}