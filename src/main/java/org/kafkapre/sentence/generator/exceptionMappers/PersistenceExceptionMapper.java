package org.kafkapre.sentence.generator.exceptionMappers;

import org.kafkapre.sentence.generator.model.InfoMessage;
import org.kafkapre.sentence.generator.persistence.api.PersistenceException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

    @Override
    public Response toResponse(PersistenceException exception) {

        // TODO log error

        return Response.serverError().entity(new InfoMessage("Something is wrong with persistence.")).build();
    }

}
