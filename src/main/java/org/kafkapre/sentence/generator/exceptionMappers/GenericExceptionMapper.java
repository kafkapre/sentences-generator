package org.kafkapre.sentence.generator.exceptionMappers;

import org.kafkapre.sentence.generator.model.InfoMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {

        // TODO log error exception.getMessage()

        return Response.serverError().entity(new InfoMessage("Something wrong happened on server")).build();
    }

}
