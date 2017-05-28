package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.kafkapre.sentence.generator.model.Sentence;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.Words;
import org.kafkapre.sentence.generator.persistence.api.SentenceDAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.inc;

public class MongoSentenceDAL extends AbstractMongoDAL implements SentenceDAL {

    static final String collectionName = "sentences";

    private static final String idKey = "_id";
    static final String hashKey = "hash";
    static final String nounKey = "noun";
    static final String verbKey = "verb";
    static final String adjectiveKey = "adjective";
    static final String showDisplayCountKey = "count";

    public MongoSentenceDAL(MongoClient mongoClient) {
        super(mongoClient);
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
        Document document = new Document(hashKey, words.hashCode())
                .append(nounKey, words.getNoun())
                .append(verbKey, words.getVerb())
                .append(adjectiveKey, words.getAdjective())
                .append(showDisplayCountKey, 0L);
        collection.insertOne(document);

        ObjectId id = document.getObjectId(idKey);
        return buildSentence(document);
    }

    @Override
    public boolean incrementSentenceShowDisplayCount(ObjectId id) {
        long modifiedCount = collection.updateOne(eq(idKey, id), inc(showDisplayCountKey, 1))
                .getModifiedCount();
        return (modifiedCount != 0);
    }

    @Override
    public Optional<Sentence> getSentence(ObjectId id) {
        Document d = collection.find(eq(idKey, id)).first();
        return Optional.ofNullable(buildSentence(d));
    }

    @Override
    public List<Sentence> getSentences(int hash) {
        List<Sentence> list = new ArrayList<>();
        collection.find(eq(hashKey, hash))
                .forEach((Block<Document>) document -> {
                    list.add(buildSentence(document));
                });
        return list;
    }

    @Override
    public Optional<Sentence> getSentence(int hash, Words words) {
        Document doc = collection.find(and(eq(hashKey, hash),
                eq(nounKey, words.getNoun()),
                eq(verbKey, words.getVerb()),
                eq(adjectiveKey, words.getAdjective()
                ))).first();
        return Optional.ofNullable(buildSentence(doc));
    }

    @Override
    public List<Sentence> getAllSentences() {
        List<Sentence> list = new ArrayList<>();
        collection.find().forEach((Block<Document>) document -> {
            list.add(buildSentence(document));
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
        int hash = doc.getInteger(hashKey);
        long count = doc.getLong(showDisplayCountKey);
        Words words = new Words(noun, verb, adjective);
        return new Sentence(id, words, hash, count);
    }

    public static int computeTextHash(String adjective, String nounWithVerb) {
        int result = adjective != null ? adjective.hashCode() : 0;
        result = 31 * result + (nounWithVerb != null ? nounWithVerb.hashCode() : 0);
        return result;
    }

}
