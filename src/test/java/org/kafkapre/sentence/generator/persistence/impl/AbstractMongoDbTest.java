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

import static org.kafkapre.sentence.generator.persistence.impl.AbstractMongoDAL.databaseName;

public abstract class AbstractMongoDbTest {

    protected final static int mongoPort = 27017;
    protected final static String mongoHost = "localhost";

    /*
     @Note: Not used because it causes troubles, needs more investigation
     */
    static class  MongoInstance {

        private static final MongoInstance instance = new MongoInstance();

        private static final MongodStarter starter = MongodStarter.getDefaultInstance();

        private MongodExecutable _mongodExe;
        private MongodProcess _mongod;

        private MongoClient _mongo;

        public static MongoInstance getInstance(){
            return instance;
        }

        protected void startEmbeddedMongo(int port) throws Exception {

            _mongodExe = starter.prepare(new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net("localhost", port, Network.localhostIsIPv6()))
                    .build());
            _mongod = _mongodExe.start();


            _mongo = new MongoClient("localhost", port);
        }

        protected void stopEmbeddedMongo() throws Exception {

            _mongod.stop();
            _mongodExe.stop();
        }

        public Mongo getMongo() {
            return _mongo;
        }
    }

    protected MongoCollection<Document> createTestLocalClient(String collectionName, int port) {
        MongoClient mongoClient = new MongoClient(mongoHost, port);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection(collectionName);
    }


    protected void clearDatabase(int port) {
        MongoClient mongoClient = new MongoClient(mongoHost, port);
        MongoDatabase database = mongoClient.getDatabase(AbstractMongoDAL.databaseName);

        MongoCollection<Document> wordCollection = database.getCollection(MongoWordDAL.collectionName);
        wordCollection.drop();

        MongoCollection<Document> sentenceCollection = database.getCollection(MongoSentenceDAL.collectionName);
        sentenceCollection.drop();
    }

}