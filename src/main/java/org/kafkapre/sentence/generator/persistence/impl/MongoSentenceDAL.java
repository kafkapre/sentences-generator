package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.model.Indexes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.kafkapre.sentence.generator.model.BaseSentence;
import org.kafkapre.sentence.generator.model.Sentence;
import org.kafkapre.sentence.generator.model.Words;
import org.kafkapre.sentence.generator.persistence.api.PersistenceException;
import org.kafkapre.sentence.generator.persistence.api.SentenceDAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Updates.inc;

public class MongoSentenceDAL extends AbstractMongoDAL implements SentenceDAL {

    private static final Logger logger = LogManager.getLogger(MongoSentenceDAL.class);

    static final String collectionName = "sentences";

    private static final String idKey = "_id";
    static final String hashKey = "hash";
    static final String nounKey = "noun";
    static final String verbKey = "verb";
    static final String adjectiveKey = "adjective";
    static final String showDisplayCountKey = "count";
    static final String sameGeneratedCountKey = "sameGeneratedCount";

    public MongoSentenceDAL(MongoClient mongoClient) {
        super(mongoClient);
        logger.info("MongoSentenceDAL initialized successfully");
    }

    @Override
    protected String getCollectionName() {
        return collectionName;
    }

    protected void initIndexes() {
        Iterable<Document> indexes = collection.listIndexes();
        if (!hasIndex(indexes, hashKey)) {
            collection.createIndex(Indexes.hashed(hashKey));
        }
    }

    @Override
    public Sentence createAndStoreSentence(Words words) {
        logger.debug("Method createAndStoreSentence called.");
        try {
            return createAndStoreSentenceInMongo(words);
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    private Sentence createAndStoreSentenceInMongo(Words words) {
        Document document = new Document(hashKey, words.hashCode())
                .append(nounKey, words.getNoun())
                .append(verbKey, words.getVerb())
                .append(adjectiveKey, words.getAdjective())
                .append(showDisplayCountKey, 0L)
                .append(sameGeneratedCountKey, 1L);

        collection.insertOne(document);
        return buildSentence(document);
    }

    @Override
    public boolean incrementSentenceShowDisplayCount(ObjectId id) {
        logger.debug("Method incrementSentenceShowDisplayCount called.");
        try {
            long modifiedCount = collection.updateOne(eq(idKey, id), inc(showDisplayCountKey, 1))
                    .getModifiedCount();
            return (modifiedCount != 0);
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public boolean incrementSentenceSameGeneratedCount(ObjectId id) {
        logger.debug("Method incrementSentenceSameGeneratedCount called.");
        try {
            long modifiedCount = collection.updateOne(eq(idKey, id), inc(sameGeneratedCountKey, 1))
                    .getModifiedCount();
            return (modifiedCount != 0);
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public Optional<Sentence> getSentence(ObjectId id) {
        logger.debug("Method getSentence called.");
        try {
            Document d = collection.find(eq(idKey, id)).first();
            return Optional.ofNullable(buildSentence(d));
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public List<Sentence> getSentences(int hash) {
        logger.debug("Method getSentences by hash called.");
        try {
            return getSentencesFromMongo(hash);
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    private List<Sentence> getSentencesFromMongo(int hash) {
        List<Sentence> list = new ArrayList<>();
        collection.find(eq(hashKey, hash))
                .forEach((Block<Document>) document -> {
                    list.add(buildSentence(document));
                });
        return list;
    }

    @Override
    public List<Sentence> getSentences(Words words) {
        logger.debug("Method getSentences by words called.");
        try {
            return getSentencesFromMongo(words);
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    private List<Sentence> getSentencesFromMongo(Words words) {
        List<Sentence> list = new ArrayList<>();
        collection.find(and(eq(hashKey, words.hashCode()),
                eq(nounKey, words.getNoun()),
                eq(verbKey, words.getVerb()),
                eq(adjectiveKey, words.getAdjective()
                ))).forEach((Block<Document>) document -> {
            list.add(buildSentence(document));
        });
        return list;
    }

    @Override
    public List<BaseSentence> getAllBaseSentences() {
        logger.debug("Method getAllBaseSentences called.");
        try {
            return getAllBaseSentencesFromMongo();
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    private List<BaseSentence> getAllBaseSentencesFromMongo() {
        List<BaseSentence> list = new ArrayList<>();
        collection.find().projection(fields(include(idKey, nounKey, verbKey, adjectiveKey)))
                .forEach((Block<Document>) document -> {
                    list.add(buildBasicSentence(document));
                });
        return list;
    }

    private Sentence buildSentence(Document doc) {
        if (doc == null) {
            return null;
        }
        ObjectId id = doc.getObjectId(idKey);
        String noun = doc.getString(nounKey);
        String verb = doc.getString(verbKey);
        String adjective = doc.getString(adjectiveKey);
        long showDisplayCount = doc.getLong(showDisplayCountKey);
        long sameGeneratedCount = doc.getLong(sameGeneratedCountKey);
        Words words = new Words(noun, verb, adjective);
        return new Sentence(id, words, showDisplayCount, sameGeneratedCount);
    }

    private BaseSentence buildBasicSentence(Document doc) {
        if (doc == null) {
            return null;
        }
        ObjectId id = doc.getObjectId(idKey);
        String noun = doc.getString(nounKey);
        String verb = doc.getString(verbKey);
        String adjective = doc.getString(adjectiveKey);
        Words words = new Words(noun, verb, adjective);
        return new BaseSentence(id, words);
    }

}
