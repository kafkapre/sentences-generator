package org.kafkapre.sentence.generator.persistence.api;

import org.bson.types.ObjectId;
import org.kafkapre.sentence.generator.model.Sentence;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.Words;

import java.util.List;
import java.util.Optional;


public interface SentenceDAL {

    Sentence createAndStoreSentence(Words words);
    boolean incrementSentenceShowDisplayCount(ObjectId id);
    Optional<Sentence> getSentence(ObjectId id);
    List<Sentence> getSentences(int hash);
    Optional<Sentence> getSentence(int hash, Words words);
    List<Sentence> getAllSentences();

}
