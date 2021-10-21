package net.hybrid.bungee.managers;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

import java.util.UUID;

public class JoinNetworkManager implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Document document = BungeePlugin.getInstance().getMongo().loadDocument(
                "playerData", player.getUniqueId());

        if (!document.getString("staffRank").equalsIgnoreCase("")
                || !document.getString("specialRank").equalsIgnoreCase("")) {
            for (UUID targetUuid : BungeePlugin.getInstance().getMongo().getStaff()) {
                ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(targetUuid);

                target.sendMessage(new TextComponent(CC.translate(
                        "&b[STAFF] " +
                                new RankManager(player.getUniqueId()).getRank().getPrefixSpace()
                                + player.getName() + " &econnected."
                )));
            }
        }

        if (RankManager.getRankCache().containsKey(player.getUniqueId())) {
            RankManager.getRankCache().replace(player.getUniqueId(), new RankManager(player.getUniqueId()).getRank());
        } else {
            RankManager.getRankCache().put(player.getUniqueId(), new RankManager(player.getUniqueId()).getRank());
        }

        if (!document.getString("staffRank").equalsIgnoreCase("")) {
            BungeePlugin.getInstance().getMongo().getStaff().add(player.getUniqueId());
        }

        if (document.getString("staffRank").equalsIgnoreCase("owner")) {
            BungeePlugin.getInstance().getMongo().getAdmins().add(player.getUniqueId());
            BungeePlugin.getInstance().getMongo().getOwners().add(player.getUniqueId());
            return;
        }

        if (document.getString("staffRank").equalsIgnoreCase("admin")) {
            BungeePlugin.getInstance().getMongo().getAdmins().add(player.getUniqueId());
        }

        document.replace("lastLogin", System.currentTimeMillis());
        BungeePlugin.getInstance().getMongo().saveDocument("playerData", document, player.getUniqueId());
    }
}
















