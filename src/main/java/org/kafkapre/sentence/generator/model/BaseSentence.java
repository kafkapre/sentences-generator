package org.kafkapre.sentence.generator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.Validate;
import org.bson.types.ObjectId;

import java.util.Objects;

import static org.kafkapre.sentence.generator.controller.RestPaths.rootPath;
import static org.kafkapre.sentence.generator.controller.RestPaths.sentencesPath;

public class BaseSentence {

    protected final ObjectId id;
    protected final Words words;

    public BaseSentence(ObjectId id, Words words) {
        Validate.notNull(id);
        Validate.notNull(words);

        this.id = id;
        this.words = words;
    }

    public ObjectId getId() {
        return id;
    }

    public Words getWords() {
        return words;
    }

    public SentenceJSON generateBaseSentenceJSON() {
        String text = words.getNoun() + " " + words.getVerb() + " " + words.getAdjective();
        return new SentenceJSON(id, text, null, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BaseSentence)) {
            return false;
        }
        BaseSentence that = (BaseSentence) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(words, that.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, words);
    }
}
