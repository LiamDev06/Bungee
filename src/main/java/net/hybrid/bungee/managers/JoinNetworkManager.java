package net.hybrid.bungee.managers;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.data.mysql.MySQL;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.hybrid.bungee.utility.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JoinNetworkManager implements Listener {

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Mongo mongo = BungeePlugin.getInstance().getMongo();

        Document document = mongo.loadDocument(
                "playerData", player.getUniqueId());

        if (document.containsKey("banned") && document.getBoolean("banned")) {
            Document punishmentDoc = mongo.loadDocument(
                    "punishments", "punishmentId", document.getString("banId")
            );

            String reason = punishmentDoc.getString("reason");

            if (punishmentDoc.containsKey("duration") && punishmentDoc.get("duration").equals("permanent")) {
                player.disconnect(new TextComponent(CC.translate(
                        "&cYou are permanently banned from this server!\n\n" +
                                "&7Reason: &f" + reason + "\n" +
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                )));
                return;
            }

            String expires = Utils.timeAsString(punishmentDoc.getLong("expires") - System.currentTimeMillis());

            player.disconnect(new TextComponent(CC.translate(
                    "&cYou are temporarily banned from this server!\n" +
                            "&cYour ban expires in: &f" + expires + "\n\n" +
                            "&7Reason: &f" + reason + "\n" +
                            "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
            )));
            return;
        }

        Document serverDocument = mongo.loadDocument("serverData", "serverDataType", "badNameList");
        if (serverDocument.containsValue(player.getName().toLowerCase())) {
            player.disconnect(new TextComponent(CC.translate(
                    "&c&lBLACKLISTED USERNAME!\n" +
                            "&cYour Minecraft username '&f" + player.getName() + "&c' is not allowed on Hybrid!\n\n" +
                            "&7To be able to play again, you must first change your username.\n" +
                            "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
            )));
            return;
        }

        Document playerDataList = mongo.loadDocument("serverData", "serverDataType", "playerDataList");
        int count = BungeePlugin.getInstance().getProxy().getOnlineCount();
        if (count > playerDataList.getInteger("mostConcurrentAtOnce")) {
            playerDataList.replace("mostConcurrentAtOnce", count);
            mongo.saveDocument("serverData", playerDataList, "serverDataType", "playerDataList");
        }

        if (document.containsKey("waitingOnSeeWarning") && document.getBoolean("waitingOnSeeWarning")) {
            ProxyServer.getInstance().getScheduler().schedule(BungeePlugin.getInstance(), () -> {
                Document warningDoc = mongo.loadDocument("punishments", "punishmentId",
                        document.getString("warningWaitingId"));

                String warningReason = warningDoc.getString("reason");
                warningDoc.replace("playerHasSeen", true);

                player.sendMessage(new TextComponent(CC.translate(
                        "&7&m-------------------------------------"
                )));

                player.sendMessage(new TextComponent(CC.translate(
                        "&c&lYOU HAVE BEEN WARNED BY STAFF!"
                )));

                player.sendMessage(new TextComponent(CC.translate(
                        "&cYou have been &cwarned with the &creason: &f" + warningReason
                )));

                player.sendMessage(new TextComponent("  "));

                player.sendMessage(new TextComponent(CC.translate(
                        "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                )));

                player.sendMessage(new TextComponent(CC.translate(
                        "&7&m-------------------------------------"
                )));

                mongo.saveDocument("punishments", warningDoc, "punishmentId",
                        document.getString("warningWaitingId"));

                document.replace("waitingOnSeeWarning", false);
                document.replace("warningWaitingId", "");
                mongo.saveDocument("playerData", document, player.getUniqueId());
            }, 2, TimeUnit.SECONDS);
        }

        if (document.containsKey("muted") && document.getBoolean("muted") && !document.getString("muteId").equalsIgnoreCase("")) {
            Document punishmentDoc = mongo.loadDocument("punishments", "punishmentId", document.getString("muteId"));

            if (punishmentDoc.getLong("expires") > System.currentTimeMillis() && !punishmentDoc.getBoolean("playerHasSeen")) {
                punishmentDoc.replace("playerHasSeen", true);
                mongo.saveDocument("punishments", punishmentDoc, "punishmentId", document.getString("muteId"));

                StringBuilder reason = new StringBuilder();
                for (String s : punishmentDoc.getString("reason").split(" ")) {
                    reason.append("§c").append(s).append(" ");
                }

                ProxyServer.getInstance().getScheduler().schedule(BungeePlugin.getInstance(), () -> {
                    player.sendMessage(new TextComponent(CC.translate(
                            "&7&m-----------------&7&m--------------------"
                    )));

                    player.sendMessage(new TextComponent(CC.translate(
                            "&c&lYOU HAVE BEEN &c&lMUTED BY STAFF!"
                    )));

                    player.sendMessage(new TextComponent(CC.translate(
                            "&cYou have been &cmuted for &c" + reason.toString().trim()
                    )));

                    player.sendMessage(new TextComponent("  "));

                    player.sendMessage(new TextComponent(CC.translate(
                            "&7Your mute &7expires in &f" + Utils.timeAsString(punishmentDoc.getLong("expires") - System.currentTimeMillis())
                    )));

                    player.sendMessage(new TextComponent(CC.translate(
                            "&7Punished falsely? Create a &7ticket at &b&nhttps://hybridplays.com/discord &7and explain &7the situation."
                    )));

                    player.sendMessage(new TextComponent(CC.translate(
                            "&7&m----------------&7&m---------------------"
                    )));
                }, 2, TimeUnit.SECONDS);
            }
        }

        if ((!document.getString("staffRank").equalsIgnoreCase("")
                || !document.getString("specialRank").equalsIgnoreCase("")) && !document.getBoolean("banned")) {
            for (UUID targetUuid : mongo.getStaffOnNotifyMode()) {
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

        if (!document.getString("staffRank").equalsIgnoreCase("")
            && document.getBoolean("staffNotifyMode")) {
            if (!mongo.getStaffOnNotifyMode().contains(player.getUniqueId())) {
                mongo.getStaffOnNotifyMode().add(player.getUniqueId());
            }
        }

        if (!document.getString("staffRank").equalsIgnoreCase("")) {
            mongo.getStaff().add(player.getUniqueId());
        }

        if (document.getString("staffRank").equalsIgnoreCase("owner")) {
            mongo.getAdmins().add(player.getUniqueId());
            mongo.getOwners().add(player.getUniqueId());

            document.replace("lastLogin", System.currentTimeMillis());
            mongo.saveDocument("playerData", document, player.getUniqueId());
            return;
        }

        if (document.getString("staffRank").equalsIgnoreCase("admin")) {
            mongo.getAdmins().add(player.getUniqueId());
        }

        document.replace("lastLogin", System.currentTimeMillis());
        mongo.saveDocument("playerData", document, player.getUniqueId());
    }
}
















