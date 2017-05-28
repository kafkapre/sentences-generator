package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.kafkapre.sentence.generator.model.Word;
import org.kafkapre.sentence.generator.model.WordCategory;
import org.kafkapre.sentence.generator.persistence.api.WordDAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.mongodb.client.model.Filters.eq;

public class MongoWordDAL extends AbstractMongoDAL implements WordDAL {

    static final String collectionName = "words";
    private static final FindOneAndUpdateOptions findAndUpdateOptions = new FindOneAndUpdateOptions();

    static {
        findAndUpdateOptions.returnDocument(ReturnDocument.AFTER);
        findAndUpdateOptions.upsert(true);
    }

    static final String textKey = "text";
    static final String categoryKey = "category";

//    MongoClient mongoClient = new MongoClient("localhost", 27017);
//    MongoDatabase database = mongoClient.getDatabase("generator");
//    MongoCollection<Document> collection = database.getCollection("words");

    public MongoWordDAL(MongoClient mongoClient) {
        super(mongoClient);
    }

    @Override
    protected String getCollectionName() {
        return collectionName;
    }

    protected void initIndexes(){
        Iterable<Document> indexes = collection.listIndexes();
        if(!hasIndex(indexes, categoryKey)){
            collection.createIndex(Indexes.hashed(categoryKey));
        }
        if(!hasIndex(indexes, textKey)){
            collection.createIndex(Indexes.text(textKey));
        }
    }

    @Override
    public void putWord(Word word) {
        Document doc = new Document();
        doc.put(textKey, word.getText());
        doc.put(categoryKey, word.getCategory().toString());

        collection.findOneAndUpdate(eq(textKey, word.getText()),
                new Document("$set", doc), findAndUpdateOptions);
    }

    @Override
    public Optional<Word> getWord(String id) {
        Document d = collection.find(eq(textKey, id)).first();
        return Optional.ofNullable(buildWord(d));
    }

    @Override
    public Optional<Word> getRandomWord(WordCategory category) {
        Document d = collection.aggregate(
                Arrays.asList(
                        Aggregates.match(Filters.eq(categoryKey, category.toString())),
                        Aggregates.sample(1)
                )).first();
        return Optional.ofNullable(buildWord(d));
    }

    @Override
    public List<Word> getAllWords() {
        List<Word> list = new ArrayList<>();
        collection.find().forEach((Block<Document>) document -> {
            list.add(buildWord(document));
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

}
