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

import java.util.ArrayList;

public class JoinNetworkManager implements Listener {

    private final static ArrayList<ProxiedPlayer> staff = new ArrayList<>();

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Document document = BungeePlugin.getInstance().getMongo().loadDocument(
                "playerData", player.getUniqueId());

        if (!document.getString("staffRank").equalsIgnoreCase("")) {
            staff.add(player);
        }

        if (!document.getString("staffRank").equalsIgnoreCase("")
                || !document.getString("specialRank").equalsIgnoreCase("")) {
            for (ProxiedPlayer target : staff) {
                target.sendMessage(new TextComponent(CC.translate(
                        "&b[STAFF] " +
                                new RankManager(player.getUniqueId()).getRank().getPrefixSpace()
                                + player.getName() + " &econnected."
                )));
            }
        }
    }

    public static ArrayList<ProxiedPlayer> getStaff() {
        return staff;
    }

}
















