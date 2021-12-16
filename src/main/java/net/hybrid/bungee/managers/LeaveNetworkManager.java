package net.hybrid.bungee.managers;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

import java.util.UUID;

public class LeaveNetworkManager implements Listener {

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Mongo mongo = BungeePlugin.getInstance().getMongo();

        Document document = mongo.loadDocument(
                "playerData", player.getUniqueId());

        if (!document.getString("staffRank").equalsIgnoreCase("")
                || !document.getString("specialRank").equalsIgnoreCase("")) {
            for (UUID targetUuid : mongo.getStaffOnNotifyMode()) {
                ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(targetUuid);

                target.sendMessage(new TextComponent(CC.translate(
                        "&b[STAFF] " +
                                new RankManager(player.getUniqueId()).getRank().getPrefixSpace()
                                + player.getName() + " &edisconnected."
                )));
            }
        }

        mongo.getStaffOnNotifyMode().remove(player.getUniqueId());
        mongo.getStaff().remove(player.getUniqueId());
        mongo.getAdmins().remove(player.getUniqueId());
        mongo.getOwners().remove(player.getUniqueId());

        document.replace("lastLogout", System.currentTimeMillis());
        mongo.saveDocument("playerData", document, player.getUniqueId());
    }
}