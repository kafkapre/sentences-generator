package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kafkapre.sentence.generator.model.Sentence;
import org.kafkapre.sentence.generator.model.Words;
import org.kafkapre.sentence.generator.persistence.api.SentenceDAL;

import java.util.List;
import java.util.Optional;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kafkapre.sentence.generator.persistence.impl.AbstractMongoDAL.databaseName;
import static org.kafkapre.sentence.generator.persistence.impl.MongoSentenceDAL.hashKey;


public class MongoSentenceDALTest {

    private SentenceDAL client;

    @Before
    public void setup() {
        clearDatabase();

        MongoClient mongoClient = new MongoClient("localhost", 27017);
        client = new MongoSentenceDAL(mongoClient);
    }

    private void clearDatabase() {
        MongoCollection<Document> collection = createTestLocalClient();
        collection.drop();
    }

    private MongoCollection<Document> createTestLocalClient(){
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection(MongoSentenceDAL.collectionName);
    }

    @Test
    public void indexesTest() throws InterruptedException {
        MongoCollection<Document> collection = createTestLocalClient();

        boolean found = false;
        for (Document d : collection.listIndexes()) {
            if (d.get("name").equals("hash_hashed")) {
                found = true;
                break;
            }
        }

        assertThat(found).isTrue();
    }

    @Test
    public void storeSentenceTest() {
        Words words = new Words("some-noun","some-verb", "some-adjective");
        Sentence actual = client.createAndStoreSentence(words);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getWords()).isEqualTo(words);
        assertThat(actual.getShowDisplayCount()).isEqualTo(0);
    }

    @Test
    public void getSentenceTest() {
        Words words = new Words("some-noun","some-verb", "some-adjective");
        Sentence stored = client.createAndStoreSentence(words);

        assertThat(stored).isNotNull();
        assertThat(stored.getId()).isNotNull();

        Optional<Sentence> actual = client.getSentence(stored.getId());
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isNotNull();
        assertThat(actual.get().getWords()).isEqualTo(words);
        assertThat(actual.get().getShowDisplayCount()).isEqualTo(0);
    }

    @Test
    public void getNonExistingSentenceTest() {
        Optional<Sentence> actual = client.getSentence(new ObjectId());
        assertThat(actual).isNotPresent();
    }

    @Test
    public void getSentencesByHashTest() {
        Sentence[] stored = storeSentences("1", "2", "1", "3", "1");
        List<Sentence> actual = client.getSentences(stored[0].getWords().hashCode());

        assertThat(actual).hasSize(3);
        assertThat(actual).containsExactly(stored[0], stored[2], stored[4]);
    }

    @Test
    public void getSentencesByHashAndWordsTest() {
        Sentence[] stored = storeSentences("1", "2", "1", "3", "1");
        List<Sentence> actual = client.getSentences(stored[0].getWords());

        assertThat(actual).hasSize(3);
        assertThat(actual).containsExactly(stored[0], stored[2], stored[4]);
    }

    @Test
    public void incrementSentenceShowDisplayCountTest() {
        Words words = new Words("some-noun","some-verb", "some-adjective");
        Sentence stored = client.createAndStoreSentence(words);

        boolean res = client.incrementSentenceShowDisplayCount(stored.getId());
        assertThat(res).isTrue();
        assertThat(getShowDisplayCount(stored.getId())).isEqualTo(1);

        res = client.incrementSentenceShowDisplayCount(stored.getId());
        assertThat(res).isTrue();
        assertThat(getShowDisplayCount(stored.getId())).isEqualTo(2);

        res = client.incrementSentenceShowDisplayCount(stored.getId());
        assertThat(res).isTrue();
        assertThat(getShowDisplayCount(stored.getId())).isEqualTo(3);

        res = client.incrementSentenceShowDisplayCount(new ObjectId());
        assertThat(res).isFalse();

    }

    private long getShowDisplayCount(ObjectId id) {
        Optional<Sentence> s = client.getSentence(id);
        assertThat(s).isPresent();
        return s.get().getShowDisplayCount();
    }

    @Test
    public void getNonExistingSentencesTest() {
        storeSentences("1", "2", "1", "3", "1");
        int hash = -1;
        assertThat(client.getSentences(hash)).hasSize(0);
    }

    @Test
    public void getSentencesFromEmptyCollectionTest() {
        List<Sentence> actual = client.getSentences(1);
        assertThat(actual).hasSize(0);
    }

    @Test
    public void getAllSentencesTest() {
        Sentence[] stored = storeSentences("1", "2", "1", "3", "1");
        List<Sentence> actual = client.getAllSentences();

        assertThat(actual).hasSize(5);
        assertThat(actual).containsExactly(stored);
    }

    private Sentence[] storeSentences(String... ids) {
        Sentence[] resArr = new Sentence[ids.length];
        for (int i = 0; i < ids.length; i++) {
            String noun = "some-noun" + ids[i];
            String verb = "some-verb" + ids[i];
            String adj = "some-adjective" + ids[i];
            resArr[i] = client.createAndStoreSentence(new Words(noun, verb, adj));
        }
        return resArr;
    }


}