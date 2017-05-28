package org.kafkapre.sentence.generator.persistence.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;


public abstract class AbstractMongoDAL  {

    static final String databaseName ="generator";

    protected final MongoDatabase database;
    protected final MongoCollection<Document> collection;

    public AbstractMongoDAL(MongoClient mongoClient) {
        this.database = mongoClient.getDatabase(databaseName);
        this.collection = database.getCollection(getCollectionName());

        initIndexes();
    }

    abstract protected String getCollectionName();
    abstract protected void initIndexes();

    protected boolean hasIndex(Iterable<Document> indexes, String indexName){
        for(Document d : indexes){
            if(isIndexWithKey(d, indexName)){
                return true;
            }
        }
        return false;
    }

    protected boolean isIndexWithKey(Document doc, String key){
        return ((Document) doc.get("key")).containsKey(key);
    }
}
