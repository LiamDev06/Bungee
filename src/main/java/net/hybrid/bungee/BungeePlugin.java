package net.hybrid.bungee;

import net.hybrid.bungee.mongo.Mongo;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

    //Testing comment to see if github logs work
    private Mongo mongo;

    @Override
    public void onEnable(){
        long time = System.currentTimeMillis();

        mongo = new Mongo(this);

        getLogger().info("Hybrid Bungee system has been SUCCESSFULLY loaded in " + (System.currentTimeMillis() - time) + "ms!");
    }

    @Override
    public void onDisable(){
        getLogger().info("Hybrid Bungee system has SUCCESSFULLY been disabled.");
    }

    public Mongo getMongo(){
        return mongo;
    }

}
