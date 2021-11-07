package net.hybrid.bungee;

import net.hybrid.bungee.commands.*;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.managers.*;
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
        getProxy().getPluginManager().registerCommand(this, new SendCommand());
        getProxy().getPluginManager().registerCommand(this, new OwnerChatCommand());
        getProxy().getPluginManager().registerCommand(this, new AdminChatCommand());
        getProxy().getPluginManager().registerCommand(this, new StaffChatCommand());
        getProxy().getPluginManager().registerCommand(this, new PurchaseRankCommand());

        getProxy().getPluginManager().registerCommand(this, new MsgCommand());
        getProxy().getPluginManager().registerCommand(this, new ReplyCommand());

        getProxy().getPluginManager().registerListener(this, new MessageListener());
        getProxy().getPluginManager().registerListener(this, new ChatManager());
        getProxy().getPluginManager().registerListener(this, new LeaveNetworkManager());
        getProxy().getPluginManager().registerListener(this, new JoinNetworkManager());
        getProxy().getPluginManager().registerListener(this, new ServerShutdownListener());

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





