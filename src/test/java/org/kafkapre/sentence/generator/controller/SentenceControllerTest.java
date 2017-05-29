package org.kafkapre.sentence.generator.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kafkapre.sentence.generator.App;
import org.kafkapre.sentence.generator.AppConfiguration;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.persistence.impl.AbstractMongoDbTest;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kafkapre.sentence.generator.controller.RestPaths.rootPath;
import static org.kafkapre.sentence.generator.controller.RestPaths.sentencesPath;
import static org.kafkapre.sentence.generator.controller.RestPaths.wordsPath;
import static org.kafkapre.sentence.generator.controller.SentenceController.resourceHrefHeaderName;
import static org.kafkapre.sentence.generator.model.WordCategory.ADJECTIVE;
import static org.kafkapre.sentence.generator.model.WordCategory.NOUN;
import static org.kafkapre.sentence.generator.model.WordCategory.VERB;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SentenceControllerTest extends AbstractMongoDbTest {


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

    @Test
    public void postSentenceTest() {
        putWord(new Word("Car", NOUN));
        putWord(new Word("is", VERB));
        putWord(new Word("new", ADJECTIVE));

        ResponseEntity<String> resp = postNewSentence();
        assertThat(resp.getStatusCode()).isEqualTo(CREATED);
        assertThatJson(resp.getBody())
                .node("id").isPresent()
                .node("href").isPresent()
                .node("text").isEqualTo("Car is new")
                .node("showDisplayCount").isEqualTo(0)
                .node("generatedTimestampSec").isPresent()
                .node("sameGeneratedCount").isAbsent();


        resp = postNewSentence();
        assertThat(resp.getStatusCode()).isEqualTo(CONFLICT);
        assertThatJson(resp.getBody())
                .node("id").isPresent()
                .node("href").isPresent()
                .node("text").isEqualTo("Car is new")
                .node("showDisplayCount").isEqualTo(0)
                .node("generatedTimestampSec").isPresent()
                .node("sameGeneratedCount").isEqualTo(2);
    }

    @Test
    public void postSentenceCannotBeGeneratedTest() {
        putWord(new Word("car", NOUN));
        putWord(new Word("is", VERB));

        ResponseEntity<String> resp = postNewSentence(false);

        assertThat(resp.getHeaders().get(resourceHrefHeaderName)).isNull();
        assertThatJson(resp.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThatJson(resp.getBody()).isEqualTo("{\"message\":\"Sentence cannot be generated words some are missing. [Adjective is missing.]\"}");
    }

    @Test
    public void getSentenceTest() {
        putWord(new Word("car", NOUN));
        putWord(new Word("is", VERB));
        putWord(new Word("new", ADJECTIVE));

        ResponseEntity<String> resp = postNewSentence();
        String url = getResourceUrlFromHeaders(resp);


        resp = getSentence(url);
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThatJson(resp.getBody())
                .node("id").isPresent()
                .node("href").isPresent()
                .node("text").isEqualTo("car is new")
                .node("showDisplayCount").isEqualTo(1)
                .node("generatedTimestampSec").isPresent()
                .node("sameGeneratedCount").isAbsent();


        resp = getSentence(url);
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThatJson(resp.getBody())
                .node("id").isPresent()
                .node("href").isPresent()
                .node("text").isEqualTo("car is new")
                .node("showDisplayCount").isEqualTo(2)
                .node("generatedTimestampSec").isPresent()
                .node("sameGeneratedCount").isAbsent();
    }

    @Test
    public void getYodaSentenceTest() {
        putWord(new Word("car", NOUN));
        putWord(new Word("is", VERB));
        putWord(new Word("new", ADJECTIVE));

        ResponseEntity<String> resp = postNewSentence();
        String url = getResourceUrlFromHeaders(resp);

        resp = getSentence(url + "/yodaTalk");
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThatJson(resp.getBody())
                .node("id").isPresent()
                .node("href").isPresent()
                .node("text").isEqualTo("new car is")
                .node("showDisplayCount").isAbsent()
                .node("generatedTimestampSec").isAbsent()
                .node("sameGeneratedCount").isAbsent();
    }

    @Test
    public void getSentencesTest() {
        putWord(new Word("car", NOUN));
        putWord(new Word("is", VERB));
        putWord(new Word("new", ADJECTIVE));

        postNewSentence();

        ResponseEntity<String> resp = getSentences();
        assertThat(resp.getStatusCode()).isEqualTo(OK);


        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThatJson(resp.getBody()).isEqualTo("[{\"id\":\"${json-unit.ignore}\",\"href\":\"${json-unit.ignore}\",\"text\":\"car is new\"}]");
    }

    private String getResourceUrlFromHeaders(ResponseEntity<String> resp) {
        List<String> hrefList = resp.getHeaders().get(resourceHrefHeaderName);
        assertThat(hrefList).hasSize(1);
        return "http://localhost:" + port + "/" + hrefList.get(0);
    }

    private void putWord(Word word) {
        HttpEntity<Word> entity = new HttpEntity<>(word, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createWordURLWithPort(word.getText()),
                HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(OK);
    }

    private ResponseEntity<String> postNewSentence() {
        return postNewSentence(true);
    }

    private ResponseEntity<String> postNewSentence(boolean assertStatusCode) {
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                createSentenceURLWithPort("generate"),
                HttpMethod.POST, entity, String.class);

        if (assertStatusCode) {
            assertThat(response.getStatusCode()).isIn(CREATED, CONFLICT);
        }
        return response;
    }


    private ResponseEntity<String> getSentence(String url) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(
                url,
                HttpMethod.GET, entity, String.class);
    }

    private ResponseEntity<String> getSentences() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(
                createSentenceURLWithPort(),
                HttpMethod.GET, entity, String.class);
    }

    private String createSentenceURLWithPort() {
        return createSentenceURLWithPort("");
    }

    private String createSentenceURLWithPort(String uri) {
        if (uri.length() > 0) {
            uri = "/" + uri;
        }
        return "http://localhost:" + port + "/" + rootPath + sentencesPath + uri;
    }

    private String createWordURLWithPort(String id) {
        return "http://localhost:" + port + "/" + rootPath + wordsPath + "/" + id;
    }

}
