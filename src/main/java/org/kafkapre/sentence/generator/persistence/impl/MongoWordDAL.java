package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.WordCategory;
import org.kafkapre.sentence.generator.persistence.api.PersistenceException;
import org.kafkapre.sentence.generator.persistence.api.WordDAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class MongoWordDAL extends AbstractMongoDAL implements WordDAL {

    private static final Logger logger = LogManager.getLogger(MongoWordDAL.class);

    static final String collectionName = "words";
    private static final FindOneAndUpdateOptions findAndUpdateOptions = new FindOneAndUpdateOptions();

    static {
        findAndUpdateOptions.returnDocument(ReturnDocument.AFTER);
        findAndUpdateOptions.upsert(true);
    }

    static final String textKey = "text";
    static final String categoryKey = "category";


    public MongoWordDAL(MongoClient mongoClient) {
        super(mongoClient);
        logger.info("MongoWordDAL initialized successfully");
    }

    @Override
    protected String getCollectionName() {
        return collectionName;
    }

    protected void initIndexes() {
        Iterable<Document> indexes = collection.listIndexes();
        if (!hasIndex(indexes, categoryKey)) {
            collection.createIndex(Indexes.hashed(categoryKey));
        }
        if (!hasIndex(indexes, textKey)) {
            collection.createIndex(Indexes.text(textKey));
        }
    }

    @Override
    public void putWord(Word word) {
        logger.debug("Method putWord called.");
        try {
            putWordInMongo(word);
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    private void putWordInMongo(Word word) {
        Document doc = new Document();
        doc.put(textKey, word.getText());
        doc.put(categoryKey, word.getCategory().toString());

        collection.findOneAndUpdate(eq(textKey, word.getText()),
                new Document("$set", doc), findAndUpdateOptions);
    }

    @Override
    public Optional<Word> getWord(String id) {
        logger.debug("Method getWord called.");
        try {
            Document d = collection.find(eq(textKey, id)).first();
            return Optional.ofNullable(buildWord(d));
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    @Override
    public Optional<Word> getRandomWord(WordCategory category) {
        logger.debug("Method getRandomWord called.");
        try {
            return getRandomWordFromMongo(category);
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    private Optional<Word> getRandomWordFromMongo(WordCategory category) {
        Document d = collection.aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.eq(categoryKey, category.toString())),
                        Aggregates.sample(1)
                )).first();
        return Optional.ofNullable(buildWord(d));
    }

    @Override
    public List<Word> getAllWords() {
        logger.debug("Method getAllWords called.");
        try {
            return getAllWordsFromMongo();
        } catch (MongoException ex) {
            throw new PersistenceException(ex);
        }
    }

    private List<Word> getAllWordsFromMongo() {
        List<Word> list = new ArrayList<>();
        collection.find().projection(fields(include(textKey))).forEach((Block<Document>) document -> {
            list.add(buildSimpleWord(document));
        });
        return list;
    }

    private Word buildWord(Document doc) {
        if (doc == null) {
            return null;
        }
        String text = (String) doc.get(textKey);
        WordCategory category = WordCategory.valueOf((String) doc.get(categoryKey));
        return new Word(text, category);
    }

    private Word buildSimpleWord(Document doc) {
        if (doc == null) {
            return null;
        }
        String text = (String) doc.get(textKey);
        return new Word(text);
    }

}
