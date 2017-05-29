package org.kafkapre.sentence.generator.persistence.api;

import org.bson.types.ObjectId;
import org.kafkapre.sentence.generator.model.BaseSentence;
import org.kafkapre.sentence.generator.model.Sentence;
import org.kafkapre.sentence.generator.model.Words;

import java.util.List;
import java.util.Optional;


public interface SentenceDAL {

    /**
     * This method generates and stores sentence from given words
     * @param words Sentence will be generated from given words.
     * @exception PersistenceException On persistence error.
     * @return Sentence Returns generated sentence.
     */
    Sentence createAndStoreSentence(Words words);

    /**
     * This method increments "showDisplayCount" property of
     * sentence determined by id.
     * @param id Sentence id.
     * @exception PersistenceException On persistence error.
     * @return boolean Returns if operation was successful.
     */
    boolean incrementSentenceShowDisplayCount(ObjectId id);

    /**
     * This method increments "sameGeneratedCount" property of
     * sentence determined by id.
     * @param id Sentence id.
     * @exception PersistenceException On persistence error.
     * @return boolean Returns if operation was successful.
     */
    boolean incrementSentenceSameGeneratedCount(ObjectId id);

    /**
     * This method returns sentence with particular id.
     * @param id Sentence id.
     * @exception PersistenceException On persistence error.
     * @return Optional<Sentence> Returns Optional.empty() if not found or Optional.of({sentence}) otherwise.
     */
    Optional<Sentence> getSentence(ObjectId id);

    /**
     * This method returns list of sentences with particular hash
     * @param hash Sentence's hash of words.
     * @exception PersistenceException On persistence error.
     * @return List<Sentence> Returns list of sentences with particular hash
     */
    List<Sentence> getSentences(int hash);

    /**
     * This method returns list of sentences with particular words.
     * @param words Sentence's words.
     * @exception PersistenceException On persistence error.
     * @return List<Sentence> Returns list of sentences with particular words
     */
    List<Sentence> getSentences(Words words);

    /**
     * This method returns list of all sentences in basic form.
     * @exception PersistenceException On persistence error.
     * @return List<BaseSentence> Returns list of all sentences in basic form.
     */
    List<BaseSentence> getAllBaseSentences();

}
