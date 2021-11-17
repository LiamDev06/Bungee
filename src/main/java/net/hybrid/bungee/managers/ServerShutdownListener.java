package net.hybrid.bungee.managers;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ServerShutdownListener implements Listener {

    @EventHandler
    public void onServerKick(ServerKickEvent event) {
        ProxyServer proxy = BungeePlugin.getInstance().getProxy();
        ServerInfo mainLobby = proxy.getServerInfo("mainlobby1");
        ProxiedPlayer player = event.getPlayer();

        proxy.getLogger().info(event.getKickReason());

        if (mainLobby != null && !event.getKickedFrom().getName().equalsIgnoreCase("mainlobby1")) {
            player.sendMessage(new TextComponent(CC.translate(
                    "&c&lDISCONNECTED! &cYou got &cdisconnected from the &cserver you &cwere on &7(" + event.getKickedFrom().getName() + ") " +
                            "&cand therefore &csent back &cto the main lobby!"
            )));

            event.setCancelServer(mainLobby);
            event.setCancelled(true);
        }
    }
}












