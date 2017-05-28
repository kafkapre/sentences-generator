package org.kafkapre.sentence.generator;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Component
//@PropertySource("classpath:app.yml")
//@ConfigurationProperties("app") // prefix app, find app.* values
@ConfigurationProperties(prefix="my")
public class AppConfiguration {


    private List<String> servers = new ArrayList<>();

//    @NotEmpty
    private String email;

    private String mongoHost = "localhost";

    private int mongoPort = 27017;

    private Set<String> rejectedWords = new HashSet<>();
    {
        rejectedWords.add("aadf");
        rejectedWords.add("aadfj");
    }

    public String getMongoHost() {
        return mongoHost;
    }

    public int getMongoPort() {
        return mongoPort;
    }

    public List<String> getServers() {
        return this.servers;
    }

    public Set<String> getRejectedWords() {
        return rejectedWords;
    }

    @Override
    public String toString() {
        return "AppProperties{" +
                "servers=" + servers +
                ", email='" + email + '\'' +
                '}';
    }
}