package net.hybrid.bungee.events;

import net.hybrid.core.CorePlugin;
import net.hybrid.core.mongo.Mongo;
import net.hybrid.core.utility.enums.PlayerRank;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

public class JoinNetworkEvent implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Mongo mongo = CorePlugin.getInstance().getMongo();
        Document document = mongo.loadDocument("playerData", player.getUniqueId());

        document.append("rank", PlayerRank.MEMBER.name());

        mongo.saveDocument("playerData", document, player.getUniqueId());
    }
}
















