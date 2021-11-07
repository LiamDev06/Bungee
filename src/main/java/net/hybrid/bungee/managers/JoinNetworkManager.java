package net.hybrid.bungee.managers;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class JoinNetworkManager implements Listener {

    //TODO ADD MUTED CHECK, so if you login and have not seen your mute it will appear on login

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Document document = BungeePlugin.getInstance().getMongo().loadDocument(
                "playerData", player.getUniqueId());

        if (document.getBoolean("banned")) {
            Document punishmentDoc = BungeePlugin.getInstance().getMongo().loadDocument(
                    "punishments", "punishmentId", document.getString("banId")
            );

            String reason = punishmentDoc.getString("reason");

            if (punishmentDoc.get("duration").equals("permanent")) {
                player.disconnect(new TextComponent(CC.translate(
                        "&cYou are permanently banned from this server!\n\n" +
                                "&7Reason: &f" + reason + "\n" +
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                )));
                return;
            }

            String expires = "";

            player.disconnect(new TextComponent(CC.translate(
                    "&cYou are temporarily banned from this server!\n" +
                            "&cYour ban expires in: &f" + expires + "\n\n" +
                            "&7Reason: &f" + reason + "\n" +
                            "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
            )));
            return;
        }

        Document serverDocument = BungeePlugin.getInstance().getMongo().loadDocument("serverData", "serverDataType", "badNameList");
        if (serverDocument.containsValue(player.getName().toLowerCase())) {
            player.disconnect(new TextComponent(CC.translate(
                    "&c&lBLACKLISTED USERNAME!\n" +
                            "&cYour Minecraft username '&f" + player.getName() + "&c' is not allowed on Hybrid!\n\n" +
                            "&7To be able to play again, you must first change your username.\n" +
                            "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
            )));
            return;
        }

        Document playerDataList = BungeePlugin.getInstance().getMongo().loadDocument("serverData", "serverDataType", "playerDataList");
        if (BungeePlugin.getInstance().getProxy().getPlayers().size() > playerDataList.getInteger("mostConcurrentAtOnce")) {
            playerDataList.replace("mostConcurrentAtOnce", BungeePlugin.getInstance().getProxy().getPlayers().size());
        }

        if (document.getBoolean("waitingOnSeeWarning")) {
            ProxyServer.getInstance().getScheduler().schedule(BungeePlugin.getInstance(), () -> {
                Document warningDoc = BungeePlugin.getInstance().getMongo().loadDocument("punishments", "punishmentId",
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
                        "&cYou have been warned with the reason: &f" + warningReason
                )));

                player.sendMessage(new TextComponent("  "));

                player.sendMessage(new TextComponent(CC.translate(
                        "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                )));

                player.sendMessage(new TextComponent(CC.translate(
                        "&7&m-------------------------------------"
                )));

                BungeePlugin.getInstance().getMongo().saveDocument("punishments", warningDoc, "punishmentId",
                        document.getString("warningWaitingId"));

                document.replace("waitingOnSeeWarning", false);
                document.replace("warningWaitingId", "");
                BungeePlugin.getInstance().getMongo().saveDocument("playerData", document, player.getUniqueId());
            }, 2, TimeUnit.SECONDS);
        }

        if (!document.getString("staffRank").equalsIgnoreCase("")
                || !document.getString("specialRank").equalsIgnoreCase("")) {
            for (UUID targetUuid : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
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
            BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(player.getUniqueId());
        }

        if (!document.getString("staffRank").equalsIgnoreCase("")) {
            BungeePlugin.getInstance().getMongo().getStaff().add(player.getUniqueId());
        }

        if (document.getString("staffRank").equalsIgnoreCase("owner")) {
            BungeePlugin.getInstance().getMongo().getAdmins().add(player.getUniqueId());
            BungeePlugin.getInstance().getMongo().getOwners().add(player.getUniqueId());

            document.replace("lastLogin", System.currentTimeMillis());
            BungeePlugin.getInstance().getMongo().saveDocument("playerData", document, player.getUniqueId());
            return;
        }

        if (document.getString("staffRank").equalsIgnoreCase("admin")) {
            BungeePlugin.getInstance().getMongo().getAdmins().add(player.getUniqueId());
        }

        document.replace("lastLogin", System.currentTimeMillis());
        BungeePlugin.getInstance().getMongo().saveDocument("playerData", document, player.getUniqueId());
    }
}
















