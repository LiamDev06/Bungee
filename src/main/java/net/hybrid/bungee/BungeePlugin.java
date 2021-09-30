package net.hybrid.bungee;

import net.hybrid.bungee.commands.RankCommand;
import net.hybrid.bungee.events.JoinNetworkEvent;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {

    private static BungeePlugin INSTANCE;

    @Override
    public void onEnable(){
        long time = System.currentTimeMillis();
        INSTANCE = this;

        getProxy().getPluginManager().registerCommand(this, new RankCommand());
        getProxy().getPluginManager().registerListener(this, new JoinNetworkEvent());

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
}





