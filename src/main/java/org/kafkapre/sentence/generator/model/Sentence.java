package org.kafkapre.sentence.generator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.Validate;
import org.bson.types.ObjectId;


import static org.kafkapre.sentence.generator.controller.RestPaths.rootPath;
import static org.kafkapre.sentence.generator.controller.RestPaths.sentencesPath;

public class Sentence extends BaseSentence {



    private final Long showDisplayCount;
    private final Long sameGeneratedCount;


    public Sentence(ObjectId id, Words words, Long showDisplayCount,
                    Long sameGeneratedCount) {
        super(id, words);

        this.showDisplayCount = showDisplayCount;
        this.sameGeneratedCount = sameGeneratedCount;
    }

    public SentenceJSON generateSentenceJSON() {
        String text = words.getNoun() + " " + words.getVerb() + " " + words.getAdjective();
        Long _sameGeneratedCount = (sameGeneratedCount > 1L) ? sameGeneratedCount : null;
        return new SentenceJSON(id, text, showDisplayCount, _sameGeneratedCount, id.getTimestamp());
    }

    public SentenceJSON generateYodaSentenceJSON() {
        String text = words.getNoun() + " " + words.getVerb() + " " + words.getAdjective();
        return new SentenceJSON(id, text, null, null, null);
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
        if (sameGeneratedCount != sentence.sameGeneratedCount) {
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
        result = 31 * result + (int) (sameGeneratedCount ^ (sameGeneratedCount >>> 32));
        return result;
    }
}
