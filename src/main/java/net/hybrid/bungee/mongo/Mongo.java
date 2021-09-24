package net.hybrid.bungee.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.hybrid.bungee.BungeePlugin;

public class Mongo {

    private BungeePlugin plugin;
    private MongoDatabase coreDatabase;

    public Mongo(BungeePlugin plugin){
        this.plugin = plugin;

        String uri = "mongodb+srv://LiamHBest:Li9313aM@cluster0.0bfk6.mongodb.net/test";
        MongoClient mongoClient = new MongoClient(new MongoClientURI(uri));

        this.coreDatabase = mongoClient.getDatabase("coredata");

        plugin.getLogger().info("Database has been CONNECTED.");
    }

}










