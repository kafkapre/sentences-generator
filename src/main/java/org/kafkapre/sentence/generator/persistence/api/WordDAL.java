package org.kafkapre.sentence.generator.persistence.api;

import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.WordCategory;

import java.util.List;
import java.util.Optional;

public interface WordDAL {

    /**
     * This method inserts given word if it has not been already inserted,
     * otherwise updates particular word.
     * @param word
     * @exception PersistenceException On persistence error.
     */
    void putWord(Word word);

    /**
     * This method returns word with particular id.
     * @param id Word id.
     * @exception PersistenceException On persistence error.
     * @return Optional<Word> Returns Optional.empty() if not found or Optional.of({word}) otherwise.
     */
    Optional<Word> getWord(String id);


    /**
     * This method returns word with particular category randomly.
     * @param category Word category.
     * @exception PersistenceException On persistence error.
     * @return Optional<Word> Returns Optional.empty() if any word with particular category
     * does not exist or Optional.of({word}) otherwise.
     */
    Optional<Word> getRandomWord(WordCategory category);

    /**
     * This method returns list of all words in basic form.
     * @exception PersistenceException On persistence error.
     * @return List<Word> Returns list of all words in basic form.
     */
    List<Word> getAllWords();

}
