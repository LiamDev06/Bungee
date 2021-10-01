package net.hybrid.bungee.events;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

public class LeaveNetworkEvent implements Listener {

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Document document = BungeePlugin.getInstance().getMongo().loadDocument(
                "playerData", player.getUniqueId());

        if (!document.getString("staffRank").equalsIgnoreCase("")) {
            JoinNetworkEvent.getStaff().remove(player);
        }

        if (!document.getString("staffRank").equalsIgnoreCase("")
                || !document.getString("specialRank").equalsIgnoreCase("")) {
            for (ProxiedPlayer target : JoinNetworkEvent.getStaff()) {
                target.sendMessage(new TextComponent(CC.translate(
                        "&b[STAFF] " +
                                new RankManager(player.getUniqueId()).getRank().getPrefixSpace()
                                + player.getName() + " &edisconnected."
                )));
            }
        }
    }
}