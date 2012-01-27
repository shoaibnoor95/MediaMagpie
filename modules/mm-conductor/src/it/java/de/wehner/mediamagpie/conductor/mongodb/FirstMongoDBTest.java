package de.wehner.mediamagpie.conductor.mongodb;

import java.net.UnknownHostException;

import org.junit.Test;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

/**
 * see also: http://blog.ralscha.ch/?p=1096
 * @author ralfwehner
 *
 */
public class FirstMongoDBTest {

    @Test
    public void test() {
        try {
            // connect to mongoDB, ip and port number
            Mongo mongo = new Mongo("localhost", 27017);

            // get database from MongoDB,
            // if database doesn't exists, mongoDB will create it automatically
            DB db = mongo.getDB("yourdb");

            // Get collection from MongoDB, database named "yourDB"
            // if collection doesn't exists, mongoDB will create it automatically
            DBCollection collection = db.getCollection("yourCollection");

            // create a document to store key and value
            BasicDBObject document = new BasicDBObject();
            document.put("id", 1001);
            document.put("msg", "hello world mongoDB in Java");
            document.put("blobData", new byte[] { 1, 2, -127, -20 });

            // save it into collection named "yourCollection"
            WriteResult wr = collection.insert(document);
            // check insert was successful
            CommandResult lastError = wr.getLastError();
            System.out.println(lastError);

            // search query
            BasicDBObject searchQuery = new BasicDBObject();
            searchQuery.put("id", 1001);

            // query it
            DBCursor cursor = collection.find(searchQuery);

            // loop over the cursor and display the retrieved result
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }

            System.out.println("Done");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
}
