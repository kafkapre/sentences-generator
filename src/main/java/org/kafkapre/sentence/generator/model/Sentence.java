package org.kafkapre.sentence.generator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.Validate;
import org.bson.types.ObjectId;

public class Sentence {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    class SentenceJSON {

        private final String text;
        private final Long showDisplayCount;

        SentenceJSON(String text, Long showDisplayCount){
            this.text = text;
            this.showDisplayCount = showDisplayCount;
        }

    }

    private final ObjectId id;
    private final Words words;
    private final long showDisplayCount;

    public Sentence(ObjectId id, Words words, long showDisplayCount){
        Validate.notNull(id);
        Validate.notNull(words);

        this.id = id;
        this.words = words;
        this.showDisplayCount = showDisplayCount;
    }

    public ObjectId getId() {
        return id;
    }

    public Words getWords() {
        return words;
    }

    public SentenceJSON generateSentenceJSON() {
        String text = words.getNoun() + " " + words.getVerb() + " " + words.getAdjective();
        return new SentenceJSON(text, showDisplayCount);
    }

    public SentenceJSON generateYodaSentenceJSON() {
        String text = words.getNoun() + " " + words.getVerb() + " " + words.getAdjective();
        return new SentenceJSON(text, null);
    }

    public long getShowDisplayCount() {
        return showDisplayCount;
    }

//    public void setShowDisplayCount(long showDisplayCount) {
//        this.showDisplayCount = showDisplayCount;
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sentence)) {
            return false;
        }

        Sentence sentence = (Sentence) o;

        if (showDisplayCount != sentence.showDisplayCount) {
            return false;
        }
        if (id != null ? !id.equals(sentence.id) : sentence.id != null) {
            return false;
        }
        return words != null ? words.equals(sentence.words) : sentence.words == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (words != null ? words.hashCode() : 0);
        result = 31 * result + (int) (showDisplayCount ^ (showDisplayCount >>> 32));
        return result;
    }
}
