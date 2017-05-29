package org.kafkapre.sentence.generator.exceptionMappers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kafkapre.sentence.generator.model.InfoMessage;
import org.kafkapre.sentence.generator.persistence.api.PersistenceException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class PersistenceExceptionMapper implements ExceptionMapper<PersistenceException> {

    private static final Logger logger = LogManager.getLogger(PersistenceExceptionMapper.class);

    @Override
    public Response toResponse(PersistenceException exception) {
        logger.error(exception);
        return Response.serverError().entity(new InfoMessage("Something is wrong with persistence.")).build();
    }

}
