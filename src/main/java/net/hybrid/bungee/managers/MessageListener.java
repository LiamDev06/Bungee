package net.hybrid.bungee.managers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.ChatChannel;
import net.hybrid.bungee.utility.PlayerRank;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.chat.TextComponent;
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
            }

            if (playerRank == PlayerRank.ADMIN) {
                mongo.getAdmins().add(targetUuid);
            }

            if (playerRank == PlayerRank.OWNER) {
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
                against.disconnect(new TextComponent(CC.translate(
                        "&cYou have been permanently banned from this server!\n\n" +
                                "&7Reason: &f" + reason + "\n" +
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                )));
            }

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
                                " &apermanently banned " + againstManager.getRank().getPrefixSpace() + againstName + "&a for: &6" + reason
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
                                " &aunbanned " + againstManager.getRank().getPrefixSpace() + againstName + "&a with reason: &6" + reason
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
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                )));
            } else {
                BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(issuerUUID))
                        .sendMessage(new TextComponent(CC.translate(
                                "&cThis player is not online!"
                        )));
            }

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
                                " &akicked " + againstManager.getRank().getPrefixSpace() + againstName + "&a for: &6" + reason
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
                                    "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                    )));

                } else {
                    against.sendMessage(new TextComponent(CC.translate(
                            "&7&m-------------------------------------"
                    )));

                    against.sendMessage(new TextComponent(CC.translate(
                            "&c&lYOU HAVE BEEN WARNED BY STAFF!"
                    )));

                    against.sendMessage(new TextComponent(CC.translate(
                            "&cYou have been warned with the reason: &f" + reason
                    )));

                    against.sendMessage(new TextComponent("  "));

                    against.sendMessage(new TextComponent(CC.translate(
                            "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                    )));

                    against.sendMessage(new TextComponent(CC.translate(
                            "&7&m-------------------------------------"
                    )));
                }
            }

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
                                " &awarned " + againstManager.getRank().getPrefixSpace() + againstName + "&a for: &6" + reason
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
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                )));
            }

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
                                " &a'bad-named' " + againstManager.getRank().getPrefixSpace() + againstName + "&a with the name '&6" + actualBadName + "&a'"
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

            RankManager rankManager = new RankManager(UUID.fromString(issuerUUID));

            boolean value = false;

            if (BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().contains(UUID.fromString(issuerUUID))) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().remove(UUID.fromString(issuerUUID));
                value = true;
            }

            for (UUID targetStaff : BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(targetStaff);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getRank().getPrefixSpace() + rankManager.getColoredName() + CC.translate(
                                " &aremoved the bad-name &6" + actualBadName + "&a from the bad-names list, with the reason: &6" + reasonForRemoval
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

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
                    !updateValue.equalsIgnoreCase("MuteIssued")) return;

            ProxiedPlayer against = BungeePlugin.getInstance().getProxy().getPlayer(UUID.fromString(againstUUID));
            if (against != null) {
                against.sendMessage(new TextComponent(CC.translate(
                        "&7&m-------------------------------------"
                )));

                against.sendMessage(new TextComponent(CC.translate(
                        "&c&lYOU HAVE BEEN MUTED BY STAFF!"
                )));

                against.sendMessage(new TextComponent(CC.translate(
                        "&cYou have been muted for " + reason
                )));

                against.sendMessage(new TextComponent("  "));

                against.sendMessage(new TextComponent(CC.translate(
                        "&7Your mute expires in &f" + expiresNormal
                )));

                against.sendMessage(new TextComponent(CC.translate(
                        "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                )));

                against.sendMessage(new TextComponent(CC.translate(
                        "&7&m-------------------------------------"
                )));
            }

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
                                " &amuted " + againstManager.getRank().getPrefixSpace() + againstName + "&a for &b" + expiresNormal + "&a, with the reason: &6" + reason
                        )
                ));
            }

            if (value) {
                BungeePlugin.getInstance().getMongo().getStaffOnNotifyMode().add(UUID.fromString(issuerUUID));
            }
        }
    }
}
