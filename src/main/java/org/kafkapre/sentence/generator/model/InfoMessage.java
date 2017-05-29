package org.kafkapre.sentence.generator.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

public class InfoMessage {

    @JsonProperty
    @XmlElement
    private String message;

    public InfoMessage() {
    }

    public InfoMessage(String str, Object... objects) {
        this.message = String.format(str, objects);
    }

    public InfoMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
