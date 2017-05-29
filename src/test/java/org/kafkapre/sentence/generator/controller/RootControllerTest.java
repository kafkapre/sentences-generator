package org.kafkapre.sentence.generator.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kafkapre.sentence.generator.App;
import org.kafkapre.sentence.generator.persistence.impl.AbstractMongoDbTest;
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
import static org.springframework.http.HttpStatus.OK;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RootControllerTest extends AbstractMongoDbTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;
    private HttpHeaders headers;

    @Before
    public void startup() throws Exception {
        restTemplate = new TestRestTemplate();
        headers = new HttpHeaders();
    }

    @Test
    public void getWordTest() {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        ResponseEntity<String> resp =  restTemplate.exchange(
                createURLWithPort(),
                HttpMethod.GET, entity, String.class);

        String expected = "{\"wordsPath\":\"/words\",\"sentencesPath\":\"/sentences\"}";
        assertThat(resp.getStatusCode()).isEqualTo(OK);
        assertThatJson(resp.getBody()).isEqualTo(expected);
    }

    private String createURLWithPort() {
        return "http://localhost:" + port + "/" + rootPath;
    }

}
