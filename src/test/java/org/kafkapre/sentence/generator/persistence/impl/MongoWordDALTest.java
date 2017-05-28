package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.WordCategory;
import org.kafkapre.sentence.generator.persistence.api.WordDAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kafkapre.sentence.generator.model.WordCategory.ADJECTIVE;
import static org.kafkapre.sentence.generator.model.WordCategory.NOUN;
import static org.kafkapre.sentence.generator.model.WordCategory.VERB;
import static org.kafkapre.sentence.generator.persistence.impl.AbstractMongoDAL.databaseName;
import static org.kafkapre.sentence.generator.persistence.impl.MongoSentenceDAL.hashKey;
import static org.kafkapre.sentence.generator.persistence.impl.MongoWordDAL.categoryKey;
import static org.kafkapre.sentence.generator.persistence.impl.MongoWordDAL.textKey;


public class MongoWordDALTest {

    private WordDAL client;

    @Before
    public void setup() {
        clearDatabase();

        MongoClient mongoClient = new MongoClient("localhost", 27017);
        client = new MongoWordDAL(mongoClient);
    }

    private void clearDatabase() {
        MongoCollection<Document> collection = createTestLocalClient();
        collection.drop();
    }

    private MongoCollection<Document> createTestLocalClient(){
        MongoClient mongoClient = new MongoClient("localhost", 27017);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        return database.getCollection(MongoWordDAL.collectionName);
    }

    @Test
    public void indexesTest() throws InterruptedException {
        MongoCollection<Document> collection = createTestLocalClient();

        boolean textIndexFound = false;
        boolean categoryIndexFound = false;
        for (Document d : collection.listIndexes()) {
            if (d.get("name").equals("category_hashed")) {
                categoryIndexFound = true;
            }

            if (d.get("name").equals("text_text")) {
                textIndexFound = true;
            }
        }

        assertThat(categoryIndexFound).isTrue();
        assertThat(textIndexFound).isTrue();
    }

    @Test
    public void getNonExistingWordTest() throws Exception {
        Optional<Word> actual = client.getWord("some-word");
        assertThat(actual).isNotPresent();
    }

    @Test
    public void storeSameWords() throws Exception {
        for (int i = 0; i < 20; i++) {
            WordCategory c = (i % 2 == 0) ? ADJECTIVE : VERB;
            client.putWord(new Word("w", c));
        }

        assertThat(client.getAllWords()).hasSize(1);
        assertThat(client.getWord("w").get().getText()).isEqualTo("w");
    }

    @Test
    public void storeAndGetWordTest() throws Exception {
        Word w = new Word("some-word", ADJECTIVE);

        client.putWord(w);
        Optional<Word> actual = client.getWord(w.getText());

        assertThat(actual).isPresent();
        assertThat(actual.get()).isNotSameAs(w);
        assertThat(actual.get()).isEqualTo(w);
    }

    @Test
    public void storeAndGeTwotWordTest() throws Exception {
//        Word w = new Word("some-word", ADJECTIVE);

        {
            Word w = new Word("some-word", ADJECTIVE);
            client.putWord(w);
            Optional<Word> actual = client.getWord(w.getText());

            assertThat(actual).isPresent();
            assertThat(actual.get()).isNotSameAs(w);
            assertThat(actual.get()).isEqualTo(w);
        }



        {
            Word w = new Word("some-word", NOUN);
            client.putWord(w);
            Optional<Word> actual = client.getWord(w.getText());


            assertThat(actual).isPresent();
            assertThat(actual.get()).isNotSameAs(w);
            assertThat(actual.get()).isEqualTo(w);
        }
    }

    @Test
    public void getRandomWordTest() throws Exception {
        List<Word> words = storeWords(20);
        words.add(new Word("w", NOUN));

        Optional<Word> actual = client.getRandomWord(ADJECTIVE);
        assertThat(actual).isPresent();
        assertThat(actual.get().getCategory()).isEqualTo(ADJECTIVE);
    }

    @Test
    public void getRandomNonExistingWordTest() throws Exception {
        storeWords(20);

        Optional<Word> actual = client.getRandomWord(NOUN);
        assertThat(actual).isNotPresent();
    }

    @Test
    public void getRandomNonExistingWordFromEmptyCollectionTest() throws Exception {
        assertThat(client.getAllWords()).isEmpty();
        Optional<Word> actual = client.getRandomWord(NOUN);
        assertThat(actual).isNotPresent();
    }

    @Test
    public void getAllWords() throws Exception {
        List<Word> stored = storeWords(20);

        List<Word> actual = client.getAllWords();
        assertThat(actual).containsAll(stored);
    }

    private List<Word> storeWords(int count) {
        List<Word> words = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            WordCategory c = (i % 2 == 0) ? ADJECTIVE : VERB;
            words.add(new Word("w" + i, c));
        }
        words.forEach(w -> client.putWord(w));
        return words;
    }

}