package org.kafkapre.sentence.generator;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("app")
public class AppConfiguration {

    @NotEmpty
    private String mongoHost = "localhost";

    private int mongoPort = 27017;

    private List<String> rejectedWords = new ArrayList<>();
    {
        rejectedWords.add("aadf");
    }

    public String getMongoHost() {
        return mongoHost;
    }

    public int getMongoPort() {
        return mongoPort;
    }

    public void setMongoHost(String mongoHost) {
        this.mongoHost = mongoHost;
    }

    public void setMongoPort(int mongoPort) {
        this.mongoPort = mongoPort;
    }

    public List<String> getRejectedWords() {
        return rejectedWords;
    }

    @Override
    public String toString() {
        return "AppConfiguration{" +
                "mongoHost='" + mongoHost + '\'' +
                ", mongoPort=" + mongoPort +
                ", rejectedWords=" + rejectedWords +
                '}';
    }
}