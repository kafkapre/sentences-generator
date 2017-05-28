package org.kafkapre.sentence.generator;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.kafkapre.sentence.generator.controller.RestPaths;
import org.kafkapre.sentence.generator.controller.WordController;
import org.springframework.context.annotation.Configuration;


@Configuration
@ApplicationPath(RestPaths.rootPath)
public class JerseyConfiguration extends ResourceConfig {

    public JerseyConfiguration() {

    }

    @PostConstruct
    public void setUp() {
        register(WordController.class);
//        register(BookController.class);
        register(GenericExceptionMapper.class);
    }
}
