package org.kafkapre.sentence.generator.persistence.impl;


import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kafkapre.sentence.generator.AppConfiguration;

public class MongoConnectorFactory {

    private static final Logger logger = LogManager.getLogger(MongoWordDAL.class);

    private final AppConfiguration conf;

    public MongoConnectorFactory(AppConfiguration conf) {
        this.conf = conf;
    }

    public MongoClient create() throws InterruptedException {
        MongoClient mongoClient;
        try {
            mongoClient = new MongoClient(conf.getMongoHost(), conf.getMongoPort());
            logger.info("Connected to MongoDB OK. [host:{}, port:{}]", conf.getMongoHost(), conf.getMongoPort());
        } catch (MongoException ex) {
            String msg = String.format("Cannot connect to MongoDB [host:%s, port:%d]",
                    conf.getMongoHost(), conf.getMongoPort());
            throw new RuntimeException(msg, ex);
        }
        return mongoClient;
    }

}
