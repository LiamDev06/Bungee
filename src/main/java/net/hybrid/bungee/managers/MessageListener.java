package net.hybrid.bungee.managers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.ChatChannel;
import net.hybrid.bungee.utility.PlayerRank;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

import java.util.UUID;

public class MessageListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord")) return;

        Mongo mongo = BungeePlugin.getInstance().getMongo();

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();

        //TODO Fix rank thingy

        if (subChannel.equalsIgnoreCase("SendToLobbyIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("SendToLobbyIssued")) return;

            ProxiedPlayer player = BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(issuerUUID));
            if (player != null) {
                if (player.getServer().getInfo().getName().equalsIgnoreCase("mainlobby1")) {
                    player.sendMessage(new ComponentBuilder("You are already at the main lobby!").color(ChatColor.RED).create());
                    return;
                }

                player.sendMessage(new ComponentBuilder("Sending you to mainlobby1...").color(ChatColor.GREEN).create());
                ServerInfo server = BungeePlugin.getInstance().getProxy().getServerInfo("mainlobby1");
                player.connect(server);
            }
        }

        if (subChannel.equalsIgnoreCase("Forward")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String rankName = in.readUTF();
            final String uuidValue = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
            !updateValue.equalsIgnoreCase("RankUpdate")) return;

            UUID targetUuid = UUID.fromString(uuidValue);
            PlayerRank playerRank = PlayerRank.valueOf(rankName.toUpperCase());

            mongo.getStaff().remove(targetUuid);
            mongo.getAdmins().remove(targetUuid);
            mongo.getOwners().remove(targetUuid);
            RankManager.getRankCache().remove(targetUuid);

            if (playerRank.isStaffRank()) {
                mongo.getStaff().add(targetUuid);
                mongo.getStaffOnNotifyMode().add(targetUuid);
            }

            if (playerRank == PlayerRank.ADMIN) {
                mongo.getAdmins().add(targetUuid);

            } else if (playerRank == PlayerRank.OWNER) {
                mongo.getAdmins().add(targetUuid);
                mongo.getOwners().add(targetUuid);
            }
        }

        if (subChannel.equalsIgnoreCase("PermanentBanIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String againstUUID = in.readUTF();
            final String reason = in.readUTF();
            final String againstName = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("PermanentBanIssued")) return;

            ProxiedPlayer against = BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(againstUUID));
            if (against != null) {
                TextComponent punish = getPunishedMessage(reason);
                if (punish != null) {
                    for (ProxiedPlayer target : against.getServer().getInfo().getPlayers()) {
                        target.sendMessage(punish);
                    }
                }

                against.disconnect(new TextComponent(CC.translate(
                        "&cYou have been permanently banned from this server!\n\n" +
                                "&7Reason: &f" + reason + "\n" +
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord &7and explain &7the situation."
                )));
            }

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));
            RankManager againstManager = new RankManager(UUID.fromString(againstUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            StringBuilder finalReason = new StringBuilder();
            for (String s : reason.split(" ")) {
                finalReason.append("§6").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &apermanently banned " + againstManager.getRank().getPrefixSpace() + againstName + " &afor: &6" + finalReason.toString().trim()
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }

        if (subChannel.equalsIgnoreCase("UnbanIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String againstUUID = in.readUTF();
            final String reason = in.readUTF();
            final String againstName = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("UnbanIssued")) return;

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));
            RankManager againstManager = new RankManager(UUID.fromString(againstUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            StringBuilder finalReason = new StringBuilder();
            for (String s : reason.split(" ")) {
                finalReason.append("§6").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &aunbanned " + againstManager.getRank().getPrefixSpace() + againstName + " &awith reason: &6" + finalReason.toString().trim()
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }

        if (subChannel.equalsIgnoreCase("UnmuteIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String againstUUID = in.readUTF();
            final String reason = in.readUTF();
            final String againstName = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("UnmuteIssued")) return;

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));
            RankManager againstManager = new RankManager(UUID.fromString(againstUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            StringBuilder finalReason = new StringBuilder();
            for (String s : reason.split(" ")) {
                finalReason.append("§6").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &aunmuted " + againstManager.getRank().getPrefixSpace() + againstName + " &awith reason: &6" + finalReason.toString().trim()
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }

        if (subChannel.equalsIgnoreCase("NotifyModeChanged")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String performedUUIDString = in.readUTF();
            final String value = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("NotifyModeChanged")) return;

            if (value.equalsIgnoreCase("on")) {
                if (!BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(performedUUIDString))) {
                    BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(performedUUIDString));
                }

            } else {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(performedUUIDString));
            }
        }

        if (subChannel.equalsIgnoreCase("KickIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String againstUUID = in.readUTF();
            final String reason = in.readUTF();
            final String againstName = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("KickIssued")) return;

            ProxiedPlayer against = BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(againstUUID));
            if (against != null) {
                against.disconnect(new TextComponent(CC.translate(
                        "&cYou have been kicked from the server!\n\n" +
                                "&7Reason: &f" + reason + "\n" +
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord &7and explain &7the situation."
                )));
            } else {
                BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(issuerUUID))
                        .sendMessage(new TextComponent(CC.translate(
                                "&cThis player is not online!"
                        )));
            }

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));
            RankManager againstManager = new RankManager(UUID.fromString(againstUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            StringBuilder finalReason = new StringBuilder();
            for (String s : reason.split(" ")) {
                finalReason.append("§6").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &akicked " + againstManager.getRank().getPrefixSpace() + againstName + " &afor: &6" + finalReason.toString().trim()
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }

        if (subChannel.equalsIgnoreCase("WarningIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String againstUUID = in.readUTF();
            final String reason = in.readUTF();
            final boolean warnOnKick = Boolean.parseBoolean(in.readUTF());
            final String punishmentId = in.readUTF();
            final String againstName = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("WarningIssued")) return;

            ProxiedPlayer against = BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(againstUUID));
            if (against != null) {
                Document document = BungeePlugin.getInstance().getMongo().loadDocument("punishments", "punishmentId", punishmentId);
                document.replace("playerHasSeen", true);
                BungeePlugin.getInstance().getMongo().saveDocument("punishments", document, "punishmentId", punishmentId);

                Document playerDoc = BungeePlugin.getInstance().getMongo().loadDocument("playerData", UUID.fromString(againstUUID));
                playerDoc.replace("waitingOnSeeWarning", false);
                playerDoc.replace("warningWaitingId", "");
                BungeePlugin.getInstance().getMongo().saveDocument("playerData", playerDoc, UUID.fromString(againstUUID));

                if (warnOnKick) {
                    against.disconnect(new TextComponent(CC.translate(
                            "&c&lYOU HAVE BEEN WARNED BY STAFF!\n" +
                            "&7Reason: &f" + reason + "\n\n" +
                                    "&7Punished falsely? Create a &7ticket at &b&nhttps://hybridplays.com/discord&7 &7and &7explain &7the &7situation."
                    )));

                } else {
                    against.sendMessage(new TextComponent(CC.translate(
                            "&7&m-------------------------------------"
                    )));

                    against.sendMessage(new TextComponent(CC.translate(
                            "&c&lYOU HAVE BEEN &c&lWARNED BY STAFF!"
                    )));

                    against.sendMessage(new TextComponent(CC.translate(
                            "&cYou have been warned &cwith the reason: &f" + reason
                    )));

                    against.sendMessage(new TextComponent("  "));

                    against.sendMessage(new TextComponent(CC.translate(
                            "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord &7and explain &7the situation."
                    )));

                    against.sendMessage(new TextComponent(CC.translate(
                            "&7&m-------------------------------------"
                    )));
                }
            }

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));
            RankManager againstManager = new RankManager(UUID.fromString(againstUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            StringBuilder finalReason = new StringBuilder();
            for (String s : reason.split(" ")) {
                finalReason.append("&6").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &awarned " + againstManager.getRank().getPrefixSpace() + againstName + " &afor: &6" + finalReason.toString().trim()
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }

        if (subChannel.equalsIgnoreCase("BadNameIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String againstUUID = in.readUTF();
            final String actualBadName = in.readUTF();
            final String againstName = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("BadNameIssued")) return;

            ProxiedPlayer against = BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(againstUUID));
            if (against != null) {
                against.disconnect(new TextComponent(CC.translate(
                        "&c&lBLACKLISTED USERNAME!\n" +
                                "&cYour Minecraft username '&f" + actualBadName + "&c' is not allowed on Hybrid!\n\n" +
                                "&7To be able to play again, you must first change your username.\n" +
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord &7and explain &7the situation."
                )));
            }

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));
            RankManager againstManager = new RankManager(UUID.fromString(againstUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &a'bad-named' " + againstManager.getRank().getPrefixSpace() + againstName + " &awith the name '&6" + actualBadName + "&a'"
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }

        if (subChannel.equalsIgnoreCase("BadNameRemoved")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String actualBadName = in.readUTF();
            final String reasonForRemoval = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("BadNameRemoved")) return;

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;
            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            StringBuilder finalReason = new StringBuilder();
            for (String s : reasonForRemoval.split(" ")) {
                finalReason.append("§6").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &aremoved the bad-name &6" + actualBadName + " &afrom &athe &abad-names list, &awith the reason: &6" + finalReason.toString().trim()
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }

        if (subChannel.equalsIgnoreCase("MuteIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String againstUUID = in.readUTF();
            final String reason = in.readUTF();
            final String expiresNormal = in.readUTF();
            final String againstName = in.readUTF();
            final String punishmentId = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("MuteIssued")) return;

            ProxiedPlayer against = BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(againstUUID));
            if (against != null) {
                Document document = mongo.loadDocument("punishments", "punishmentId", punishmentId);
                document.replace("playerHasSeen", true);
                mongo.saveDocument("punishments", document, "punishmentId", punishmentId);

                StringBuilder finalReason = new StringBuilder();
                for (String s : reason.split(" ")) {
                    finalReason.append("§c").append(s).append(" ");
                }

                against.sendMessage(new TextComponent(CC.translate(
                        "&7&m-------------------------------------"
                )));

                against.sendMessage(new TextComponent(CC.translate(
                        "&c&lYOU HAVE BEEN MUTED BY STAFF!"
                )));

                against.sendMessage(new TextComponent(CC.translate(
                        "&cYou have &cbeen muted for " + finalReason.toString().trim()
                )));

                against.sendMessage(new TextComponent("  "));

                against.sendMessage(new TextComponent(CC.translate(
                        "&7Your mute expires in &f" + expiresNormal
                )));

                against.sendMessage(new TextComponent(CC.translate(
                        "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord &7and explain &7the situation."
                )));

                against.sendMessage(new TextComponent(CC.translate(
                        "&7&m-------------------------------------"
                )));
            }

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));
            RankManager againstManager = new RankManager(UUID.fromString(againstUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            StringBuilder finalReason = new StringBuilder();
            for (String s : reason.split(" ")) {
                finalReason.append("§6").append(s).append(" ");
            }

            StringBuilder finalExpiresNormal = new StringBuilder();
            for (String s : expiresNormal.split(" ")) {
                finalExpiresNormal.append("§b").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &amuted " + againstManager.getRank().getPrefixSpace() + againstName + " &afor &b" + finalExpiresNormal.toString().trim() + "&a, with the &areason: &6" + finalReason.toString().trim()
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }

        if (subChannel.equalsIgnoreCase("TempBanIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String againstUUID = in.readUTF();
            final String reason = in.readUTF();
            final String expiresNormal = in.readUTF();
            final String againstName = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("TempBanIssued")) return;

            ProxiedPlayer against = BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(againstUUID));
            if (against != null) {
                TextComponent punish = getPunishedMessage(reason);
                if (punish != null) {
                    for (ProxiedPlayer target : against.getServer().getInfo().getPlayers()) {
                        target.sendMessage(punish);
                    }
                }

                against.disconnect(new TextComponent(CC.translate(
                        "&cYou have been temporarily banned for &f" + expiresNormal + " &cfrom this server!\n\n" +
                                "&7Reason: &f" + reason + "\n" +
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord &7and explain &7the situation."
                )));
            }

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));
            RankManager againstManager = new RankManager(UUID.fromString(againstUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            StringBuilder finalReason = new StringBuilder();
            for (String s : reason.split(" ")) {
                finalReason.append("§6").append(s).append(" ");
            }

            StringBuilder finalExpiresNormal = new StringBuilder();
            for (String s : expiresNormal.split(" ")) {
                finalExpiresNormal.append("§b").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &atemporarily banned " + againstManager.getRank().getPrefixSpace() + againstName + " &afor &b" + finalExpiresNormal.toString().trim() + "&a, with the &areason: &6" + finalReason.toString().trim()
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }

        if (subChannel.equalsIgnoreCase("ClearChatIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String reason = in.readUTF();
            final String punishmentId = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("ClearChatIssued")) return;

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            String server = BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(issuerUUID)).getServer().getInfo().getName();

            StringBuilder finalReason = new StringBuilder();
            for (String s : reason.split(" ")) {
                finalReason.append("§6").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &acleared the chat on server '§f" + server + "§a' &awith reason: &6" + finalReason.toString().trim()
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }

            Document document = mongo.loadDocument("punishments", "punishmentId", punishmentId);
            document.replace("serverIssuedOn", server);
            mongo.saveDocument("punishments", document, "punishmentId", punishmentId);
        }

        if (subChannel.equalsIgnoreCase("PlayerReportIssued")) {
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String issuerUUID = in.readUTF();
            final String againstUUID = in.readUTF();
            final String reason = in.readUTF();
            final String againstName = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("PlayerReportIssued")) return;

            if (issuerUUID.equalsIgnoreCase("CONSOLE")) return;

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));
            RankManager rankAgainst = new RankManager(UUID.fromString(againstUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            StringBuilder finalReason = new StringBuilder();
            for (String s : reason.split(" ")) {
                finalReason.append("§6").append(s).append(" ");
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(CC.translate(
                        "&6[&6R&6E&6P&6O&6R&6T&6]" + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + " &aiss&aued &aa &anew &are&aport &afor " +
                                rankAgainst.getRank().getPrefixSpace() + againstName + "&a, &awith &athe &area&ason&a: " + finalReason.toString().trim()
                )));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }
    }

    public TextComponent getPunishedMessage(String reason) {
        TextComponent component = null;

        if (reason.equalsIgnoreCase("Cheating through the use of modifications that results in an unfair advantage")) {
            component = new TextComponent(CC.translate(
                    "&7&m----------------------------------\n" +
                       "&c&lSomeone was banned and removed from your server for &6&lCHEATING&c&l! GG :D\n" +
                       "&7&m----------------------------------"
            ));
        }

        if (reason.equalsIgnoreCase("Alt-account punishment evading")) {
            component = new TextComponent(CC.translate(
                    "&7&m----------------------------------\n" +
                            "&c&lSomeone was banned and removed from your server for &6&lPUNISHMENT EVADING&c&l! GG :D\n" +
                            "&7&m----------------------------------"
            ));
        }

        if (reason.equalsIgnoreCase("Abusing glitches, bugs and/or exploits intentionally")) {
            component = new TextComponent(CC.translate(
                    "&7&m----------------------------------\n" +
                            "&c&lSomeone was banned and removed from your server for &6&lEXPLOITING&c&l! GG :D\n" +
                            "&7&m----------------------------------"
            ));
        }

        if (reason.equalsIgnoreCase("Intentionally sabotaging the game for other players")) {
            component = new TextComponent(CC.translate(
                    "&7&m----------------------------------\n" +
                            "&c&lSomeone was banned and removed from your server for &6&lGAME SABOTAGING&c&l! GG :D\n" +
                            "&7&m----------------------------------"
            ));
        }

        if (reason.equalsIgnoreCase("Game sabotaging in the form of cross-teaming")) {
            component = new TextComponent(CC.translate(
                    "&7&m----------------------------------\n" +
                            "&c&lSomeone was banned and removed from your server for &6&lCROSS-TEAMING&c&l! GG :D\n" +
                            "&7&m----------------------------------"
            ));
        }

        return component;
    }



}








