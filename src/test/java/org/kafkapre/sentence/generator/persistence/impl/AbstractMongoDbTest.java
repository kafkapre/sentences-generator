package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.bson.Document;
import org.kafkapre.sentence.generator.persistence.impl.AbstractMongoDAL;
import org.kafkapre.sentence.generator.persistence.impl.MongoSentenceDAL;

import static org.kafkapre.sentence.generator.persistence.impl.AbstractMongoDAL.databaseName;

public abstract class AbstractMongoDbTest {

    /**
     * please store Starter or RuntimeConfig in a static final field
     * if you want to use artifact store caching (or else disable caching)
     */
    private static final MongodStarter starter = MongodStarter.getDefaultInstance();

    private MongodExecutable _mongodExe;
    private MongodProcess _mongod;

    private MongoClient _mongo;


    protected void startEmbeddedMongo() throws Exception {

        _mongodExe = starter.prepare(new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net("localhost", 12345, Network.localhostIsIPv6()))
                .build());
        _mongod = _mongodExe.start();


        _mongo = new MongoClient("localhost", 12345);
    }

    protected void stopEmbeddedMongo() throws Exception {

        _mongod.stop();
        _mongodExe.stop();
    }

    public Mongo getMongo() {
        return _mongo;
    }

    protected MongoCollection<Document> createTestLocalClient(String collectionName) {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection(collectionName);
    }


    protected void clearDatabase() {
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase(AbstractMongoDAL.databaseName);

        MongoCollection<Document> wordCollection = database.getCollection(MongoWordDAL.collectionName);
        wordCollection.drop();

        MongoCollection<Document> sentenceCollection = database.getCollection(MongoSentenceDAL.collectionName);
        sentenceCollection.drop();
    }

}