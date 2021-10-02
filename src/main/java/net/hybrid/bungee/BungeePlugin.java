package net.hybrid.bungee;

import net.hybrid.bungee.commands.LobbyCommand;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.managers.ChatManager;
import net.hybrid.bungee.managers.JoinNetworkManager;
import net.hybrid.bungee.managers.LeaveNetworkManager;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

    private static BungeePlugin INSTANCE;
    private Mongo mongo;

    @Override
    public void onEnable(){
        long time = System.currentTimeMillis();
        INSTANCE = this;

        mongo = new Mongo(this);

        getProxy().getPluginManager().registerCommand(this, new LobbyCommand());

        getProxy().getPluginManager().registerListener(this, new LeaveNetworkManager());
        getProxy().getPluginManager().registerListener(this, new JoinNetworkManager());
        getProxy().getPluginManager().registerListener(this, new ChatManager());

        getLogger().info("Hybrid Bungee system has been SUCCESSFULLY loaded in " + (System.currentTimeMillis() - time) + "ms!");
    }

    @Override
    public void onDisable(){
        INSTANCE = null;
        getLogger().info("Hybrid Bungee system has SUCCESSFULLY been disabled.");
    }

    public static BungeePlugin getInstance() {
        return INSTANCE;
    }

    public Mongo getMongo() {
        return mongo;
    }

}





