package net.hybrid.bungee;

import net.hybrid.bungee.commands.*;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.data.mysql.MySQL;
import net.hybrid.bungee.managers.*;
import net.hybrid.bungee.party.PartyCommand;
import net.hybrid.bungee.party.PartyManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class BungeePlugin extends Plugin {

    private static BungeePlugin INSTANCE;
    private Mongo mongo;
    private PartyManager partyManager;
    private MySQL mySql;

    @Override
    public void onEnable(){
        long time = System.currentTimeMillis();
        INSTANCE = this;

        mongo = new Mongo(this);
        partyManager = new PartyManager();

        mySql = new MySQL();
        mySql.connect();

        if (mySql.isConnected()) {
            getProxy().getLogger().info("Bungee Database (network systems) was successfully connected!");
        }

        PluginManager manager = getProxy().getPluginManager();

        manager.registerCommand(this, new LobbyCommand());
        manager.registerCommand(this, new SendCommand());
        manager.registerCommand(this, new OwnerChatCommand());
        manager.registerCommand(this, new AdminChatCommand());
        manager.registerCommand(this, new StaffChatCommand());
        manager.registerCommand(this, new PurchaseRankCommand());

        manager.registerCommand(this, new FindPlayerCommand());
        manager.registerCommand(this, new PartyCommand());
        manager.registerCommand(this, new MsgCommand());
        manager.registerCommand(this, new ReplyCommand());

        manager.registerListener(this, new MessageListener());
        manager.registerListener(this, new ChatManager());
        manager.registerListener(this, new LeaveNetworkManager());
        manager.registerListener(this, new JoinNetworkManager());
        manager.registerListener(this, new ServerShutdownListener());

        getLogger().info("Hybrid Bungee system has been SUCCESSFULLY loaded in " + (System.currentTimeMillis() - time) + "ms!");
    }

    @Override
    public void onDisable(){
        INSTANCE = null;

        mySql.disconnect();
        if (!mySql.isConnected()) {
            getProxy().getLogger().info("Database was successfully disconnected!");
        }

        getLogger().info("Hybrid Bungee system has SUCCESSFULLY been disabled.");
    }

    public static BungeePlugin getInstance() {
        return INSTANCE;
    }

    public Mongo getMongo() {
        return mongo;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public MySQL getMySql() {
        return mySql;
    }
}





