package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.kafkapre.sentence.generator.model.BaseSentence;
import org.kafkapre.sentence.generator.model.Sentence;
import org.kafkapre.sentence.generator.model.Words;
import org.kafkapre.sentence.generator.persistence.api.SentenceDAL;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class MongoSentenceDALTest extends AbstractMongoDbTest {

    private SentenceDAL client;

    @Before
    public void setup() {
        clearDatabase(mongoPort);

        MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);
        client = new MongoSentenceDAL(mongoClient);
    }

    @Test
    public void indexesTest() throws InterruptedException {
        MongoCollection<Document> collection = createTestLocalClient(MongoSentenceDAL.collectionName, mongoPort);

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
        Words words = new Words("some-noun", "some-verb", "some-adjective");
        Sentence actual = client.createAndStoreSentence(words);

        assertThat(actual).isNotNull();
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getWords()).isEqualTo(words);
        assertThat(actual.getShowDisplayCount()).isEqualTo(0);
    }

    @Test
    public void getSentenceTest() {
        Words words = new Words("some-noun", "some-verb", "some-adjective");
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
        Words words = new Words("some-noun", "some-verb", "some-adjective");
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
        BaseSentence[] expected = transformToBaseSentenceArr(stored);

        List<BaseSentence> actual = client.getAllBaseSentences();

        assertThat(actual).hasSize(5);
        assertThat(actual).contains(expected);
    }

    private BaseSentence[] transformToBaseSentenceArr(Sentence[] input){
        BaseSentence[] res = new BaseSentence[input.length];
        for (int i = 0; i < input.length; i++) {
            res[i] = (BaseSentence) input[i];
        }
        return res;
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