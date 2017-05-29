package org.kafkapre.sentence.generator.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kafkapre.sentence.generator.App;
import org.kafkapre.sentence.generator.AppConfiguration;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.WordCategory;
import org.kafkapre.sentence.generator.persistence.impl.AbstractMongoDbTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kafkapre.sentence.generator.controller.RestPaths.rootPath;
import static org.kafkapre.sentence.generator.controller.RestPaths.wordsPath;
import static org.kafkapre.sentence.generator.model.WordCategory.ADJECTIVE;
import static org.kafkapre.sentence.generator.model.WordCategory.NOUN;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WordControllerTest extends AbstractMongoDbTest {


    @Autowired
    private AppConfiguration configuration;

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;
    private HttpHeaders headers;

    @Before
    public void startup() throws Exception {
        clearDatabase(configuration.getMongoPort());

        restTemplate = new TestRestTemplate();
        headers = new HttpHeaders();

    }

    @After
    public void shutdown() throws Exception {
//        super.tearDown();
    }

    @Test
    public void getWordTest() {
        Word word = new Word("some", ADJECTIVE);
        {
            putWord(word);
            ResponseEntity<String> response = getWord(word.getText());

            String expected = "{\"word\":\"some\",\"href\":\"api/words/some\",\"category\":\"ADJECTIVE\"}";
            assertThat(response.getStatusCode()).isEqualTo(OK);
            assertThatJson(response.getBody()).isEqualTo(expected);
        }

        word = new Word("some", NOUN);
        {
            putWord(word);
            ResponseEntity<String> response = getWord(word.getText());

            String expected = "{\"word\":\"some\",\"category\":\"NOUN\",\"href\":\"api/words/some\"}";
            assertThat(response.getStatusCode()).isEqualTo(OK);
            assertThatJson(response.getBody()).isEqualTo(expected);
        }
    }

    @Test
    public void getNonExistingWordTest() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("-1"),
                HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    public void putWordTest() throws Exception {
        Word w = new Word("home", WordCategory.NOUN);
        ResponseEntity<String> response = putWord(w);

        String expected = "{\"word\":\"home\",\"category\":\"NOUN\",\"href\":\"api/words/home\"}";
        assertThatJson(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void putRejectedWordTest() throws Exception {
        assertThat(configuration.getRejectedWords()).isNotEmpty();
        for(String text : configuration.getRejectedWords()){
            ResponseEntity<String> resp = putWord(new Word(text, NOUN), false);
            assertThatJson(resp.getStatusCodeValue()).isEqualTo(METHOD_NOT_ALLOWED.value());
        }
    }

    @Test
    public void getWordsTest() throws Exception {
        putWord(new Word("home", WordCategory.NOUN));
        putWord(new Word("and", WordCategory.NOUN));
        putWord(new Word("to", WordCategory.NOUN));

        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(),
                HttpMethod.GET, entity, String.class);

        String expected = "[{\"href\":\"api/words/home\",\"word\":\"home\"}," +
                "{\"href\":\"api/words/and\",\"word\":\"and\"},{\"href\":\"api/words/to\",\"word\":\"to\"}]";
        assertThatJson(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void getRejectedWordsTest() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort("rejected"),
                HttpMethod.GET, entity, String.class);

        assertThatJson(response.getBody()).isEqualTo(configuration.getRejectedWords());
    }

    private ResponseEntity<String> getWord(String id) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(
                createURLWithPort(id),
                HttpMethod.GET, entity, String.class);
    }

    private ResponseEntity<String> putWord(Word word) {
        return putWord(word, true);
    }

    private ResponseEntity<String> putWord(Word word, boolean assertStatusCode) {
        HttpEntity<Word> entity = new HttpEntity<>(word, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createURLWithPort(word.getText()),
                HttpMethod.PUT, entity, String.class);

        if(assertStatusCode) {
            assertThat(response.getStatusCode()).isEqualTo(OK);
        }
        return response;
    }

    private String createURLWithPort() {
        return createURLWithPort("");
    }

    private String createURLWithPort(String uri) {
        if(uri.length() > 0){
            uri = "/" + uri;
        }
        return "http://localhost:" + port + "/" + rootPath + wordsPath + uri ;
    }

}
