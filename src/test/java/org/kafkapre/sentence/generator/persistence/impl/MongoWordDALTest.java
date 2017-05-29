package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.Before;
import org.junit.Test;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.WordCategory;
import org.kafkapre.sentence.generator.persistence.api.WordDAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kafkapre.sentence.generator.model.WordCategory.ADJECTIVE;
import static org.kafkapre.sentence.generator.model.WordCategory.NOUN;
import static org.kafkapre.sentence.generator.model.WordCategory.VERB;


public class MongoWordDALTest extends AbstractMongoDbTest {

    private WordDAL client;

    @Before
    public void setup() {
        clearDatabase(mongoPort);

        MongoClient mongoClient = new MongoClient(mongoHost, mongoPort);
        client = new MongoWordDAL(mongoClient);
    }

    @Test
    public void indexesTest() throws InterruptedException {
        MongoCollection<Document> collection = createTestLocalClient(MongoWordDAL.collectionName, mongoPort);

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
    public void storeAndGeTwoWordTest() throws Exception {
        Word w = new Word("some-word", ADJECTIVE);
        client.putWord(w);
        Optional<Word> actual = client.getWord(w.getText());

        assertThat(actual).isPresent();
        assertThat(actual.get()).isNotSameAs(w);
        assertThat(actual.get()).isEqualTo(w);

        w = new Word("some-word", NOUN);
        client.putWord(w);
        actual = client.getWord(w.getText());


        assertThat(actual).isPresent();
        assertThat(actual.get()).isNotSameAs(w);
        assertThat(actual.get()).isEqualTo(w);
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
        List<Word> expected = stored.stream().map(w -> new Word(w.getText())).collect(Collectors.toList());

        List<Word> actual = client.getAllWords();
        assertThat(actual).containsAll(expected);
    }

    private List<Word> storeWords(int count) {
        return storeWords(count, false);
    }

    private List<Word> storeWords(int count, boolean withoutCategory) {
        List<Word> words = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            if (withoutCategory) {
                words.add(new Word("w" + i));
            } else {
                WordCategory c = (i % 2 == 0) ? ADJECTIVE : VERB;
                words.add(new Word("w" + i, c));
            }
        }
        words.forEach(w -> client.putWord(w));
        return words;
    }

}