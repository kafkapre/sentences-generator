package org.kafkapre.sentence.generator;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.kafkapre.sentence.generator.controller.RestPaths;
import org.kafkapre.sentence.generator.controller.RootController;
import org.kafkapre.sentence.generator.controller.SentenceController;
import org.kafkapre.sentence.generator.controller.WordController;
import org.kafkapre.sentence.generator.exceptionMappers.GenericExceptionMapper;
import org.kafkapre.sentence.generator.exceptionMappers.PersistenceExceptionMapper;
import org.springframework.context.annotation.Configuration;


@Configuration
@ApplicationPath(RestPaths.rootPath)
public class JerseyConfiguration extends ResourceConfig {

    public JerseyConfiguration() {

    }

    @PostConstruct
    public void setUp() {
        register(RootController.class);
        register(WordController.class);
        register(SentenceController.class);
        register(GenericExceptionMapper.class);
        register(PersistenceExceptionMapper.class);
    }
}
