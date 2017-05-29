package org.kafkapre.sentence.generator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.Validate;
import org.bson.types.ObjectId;

import static org.kafkapre.sentence.generator.controller.RestPaths.rootPath;
import static org.kafkapre.sentence.generator.controller.RestPaths.sentencesPath;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SentenceJSON {

    private static final String path = rootPath + sentencesPath + "/";

    @JsonProperty
    private final String id;
    @JsonProperty
    private final String href;
    @JsonProperty
    private final String text;
    @JsonProperty
    private final Long showDisplayCount;
    @JsonProperty
    private final Long sameGeneratedCount;
    @JsonProperty
    private final Integer generatedTimestampSec;

    SentenceJSON(ObjectId id, String text, Long showDisplayCount,
                 Long sameGeneratedCount, Integer generatedTimestampSec) {
        Validate.notNull(id);
        this.id = id.toString();
        this.href = path + this.id;
        this.text = text;
        this.showDisplayCount = showDisplayCount;
        this.sameGeneratedCount = sameGeneratedCount;
        this.generatedTimestampSec = generatedTimestampSec;
    }

    public String getHref() {
        return href;
    }
}
