package org.kafkapre.sentence.generator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.Validate;
import org.bson.types.ObjectId;

import static org.kafkapre.sentence.generator.controller.RestPaths.rootPath;
import static org.kafkapre.sentence.generator.controller.RestPaths.sentencesPath;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SentenceJSON {

    private static final String path = rootPath + sentencesPath;

    private final ObjectId id;
    private final String href;
    private final String text;
    private final Long showDisplayCount;
    private final Long sameGeneratedCount;
    private final Integer generatedTimestampSec;

    SentenceJSON(ObjectId id, String text, Long showDisplayCount,
                 Long sameGeneratedCount, Integer generatedTimestampSec) {
        Validate.notNull(id);
        this.id = id;
        this.href = path + id.toString() + "/";
        this.text = text;
        this.showDisplayCount = showDisplayCount;
        this.sameGeneratedCount = sameGeneratedCount;
        this.generatedTimestampSec = generatedTimestampSec;
    }

}
