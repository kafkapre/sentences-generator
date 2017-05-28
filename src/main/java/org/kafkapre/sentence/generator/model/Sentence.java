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
    private final int textHash;
    private final Words words;
    private final long showDisplayCount;

    public Sentence(ObjectId id, Words words, int textHash, long showDisplayCount){
        Validate.notNull(id);
        Validate.notNull(words);

        this.id = id;
        this.words = words;
        this.textHash = textHash;
        this.showDisplayCount = showDisplayCount;
    }

    public ObjectId getId() {
        return id;
    }

    public int getTextHash() {
        return textHash;
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


}
