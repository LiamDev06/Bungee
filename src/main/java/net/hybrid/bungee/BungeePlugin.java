package net.hybrid.bungee;

import net.hybrid.bungee.commands.*;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.managers.ChatManager;
import net.hybrid.bungee.managers.JoinNetworkManager;
import net.hybrid.bungee.managers.LeaveNetworkManager;
import net.hybrid.bungee.managers.MessageListener;
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

        getProxy().getPluginManager().registerCommand(this, new MsgCommand());
        getProxy().getPluginManager().registerCommand(this, new ReplyCommand());
        getProxy().getPluginManager().registerCommand(this, new EnableMaintenanceCommand());
        getProxy().getPluginManager().registerCommand(this, new DisableMaintenanceCommand());

        getProxy().getPluginManager().registerListener(this, new MessageListener());
        getProxy().getPluginManager().registerListener(this, new ChatManager());
        getProxy().getPluginManager().registerListener(this, new LeaveNetworkManager());
        getProxy().getPluginManager().registerListener(this, new JoinNetworkManager());

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





