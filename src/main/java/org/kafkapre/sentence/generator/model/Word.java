package org.kafkapre.sentence.generator.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.kafkapre.sentence.generator.controller.RestPaths.rootPath;
import static org.kafkapre.sentence.generator.controller.RestPaths.wordPath;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Word {

    private static final String path = rootPath + wordPath + "/";

    @JsonProperty("word")
    private String text;

    @JsonProperty
    private WordCategory category;

    //    @JsonProperty
//    @JsonProperty (access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnore // (access = JsonProperty.Access.WRITE_ONLY) - bug: does not work, so there is workaround @JsonIgnore on property which is not
    private String href;

    // TODO add href

    Word() {
    }

    public Word(String text) {
        this.text = text;
        this.category = null;
    }

    public Word(String text, WordCategory category) {
        this.text = text;
        this.category = category;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    public WordCategory getCategory() {
        return category;
    }


    public void setCategory(WordCategory category) {
        this.category = category;
    }


    @JsonProperty // (access = JsonProperty.Access.WRITE_ONLY) - bug: does not work, so there is workaround @JsonIgnore on property which is not
    public String getHref() {
        return path + text;
    }

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    void setHref(String href) { // package private
        // do nothing
    }

    @Override
    public String toString() {
        return "Word{" +
                "text='" + text + '\'' +
                ", category=" + category +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Word)) {
            return false;
        }

        Word word = (Word) o;

        if (text != null ? !text.equals(word.text) : word.text != null) {
            return false;
        }
        return category == word.category;

    }

    @Override
    public int hashCode() {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}
