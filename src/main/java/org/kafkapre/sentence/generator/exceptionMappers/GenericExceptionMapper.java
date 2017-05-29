package org.kafkapre.sentence.generator.exceptionMappers;

import org.kafkapre.sentence.generator.model.InfoMessage;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kafkapre.sentence.generator.persistence.impl.MongoWordDAL;


@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger logger = LogManager.getLogger(GenericExceptionMapper.class);

    @Override
    public Response toResponse(Throwable exception) {
        logger.error(exception);
        return Response.serverError().entity(new InfoMessage("Something wrong happened on server")).build();
    }

}
