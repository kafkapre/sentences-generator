package org.kafkapre.sentence.generator.persistence.impl;


import com.mongodb.MongoClient;
import org.kafkapre.sentence.generator.AppConfiguration;

public class MongoConnectorFactory {

    private final AppConfiguration conf;

    public MongoConnectorFactory(AppConfiguration conf) {
        this.conf = conf;
    }

    public MongoClient create() {
        MongoClient mongoClient = new MongoClient(conf.getMongoHost(), conf.getMongoPort());

        // TODO check ping

        return mongoClient;
    }


}
