package org.kafkapre.sentence.generator.persistence.api;

import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.WordCategory;

import java.util.List;
import java.util.Optional;

public interface WordDAL {

    void putWord(Word word);
    Optional<Word> getWord(String id);
    Optional<Word> getRandomWord(WordCategory category);
    List<Word> getAllWords();

}
