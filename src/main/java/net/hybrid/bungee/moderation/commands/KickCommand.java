package net.hybrid.bungee.moderation.commands;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.moderation.ModerationLogger;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class KickCommand extends Command {

    public KickCommand() {
        super("kick");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) commandSender;
            RankManager rank = new RankManager(player.getUniqueId());

            if (!rank.isStaff()) {
                player.sendMessage(new TextComponent(CC.translate(
                        "&cYou are not allowed to perform this!"
                )));
                return;
            }

            if (args.length == 0) {
                player.sendMessage(new TextComponent(CC.translate(
                        "&cMissing arguments! Valid usage: /kick <player> <reason...>"
                )));
                return;
            }

            if (args.length == 1) {
                ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(new TextComponent(CC.translate(
                            "&cThis player is not online!"
                    )));
                    return;
                }

                RankManager targetRank = new RankManager(target.getUniqueId());
                if (target.getUniqueId() == player.getUniqueId()) {
                    player.sendMessage(new TextComponent(CC.translate(
                            "&cYou cannot kick yourself!"
                    )));
                    return;
                }

                if (rank.getRank().getOrdering() < targetRank.getRank().getOrdering()) {
                    player.sendMessage(new TextComponent(CC.translate(
                            "&cYou cannot kick this player!"
                    )));
                    return;
                }

                if (!target.isConnected()) {
                    player.sendMessage(new TextComponent(CC.translate(
                            "&cThis player is not online on the network!"
                    )));
                    return;
                }

                target.disconnect(new TextComponent(CC.translate(
                        "&cYou have been kicked from the server!\n\n" +
                                "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
                )));

                ModerationLogger.logKick(target.getUniqueId(), player.getUniqueId(), "", System.currentTimeMillis());

                player.sendMessage(new TextComponent(CC.translate(
                        "&aYou successfully kicked " + targetRank
                        .getRank().getPrefixSpace() + target.getName() + " &afrom the network with: &fNo Reason&a. &aThis has been logged."
                )));
                return;
            }

            StringBuilder reason = new StringBuilder();
            int count = 0;

            for (String s : args) {
                if (count >= 1) {
                    reason.append(s).append(" ");
                }

                count++;
            }

            ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(new TextComponent(CC.translate(
                        "&cThis player is not online!"
                )));
                return;
            }

            RankManager targetRank = new RankManager(target.getUniqueId());
            if (target.getUniqueId() == player.getUniqueId()) {
                player.sendMessage(new TextComponent(CC.translate(
                        "&cYou cannot kick yourself!"
                )));
                return;
            }

            if (rank.getRank().getOrdering() < targetRank.getRank().getOrdering()) {
                player.sendMessage(new TextComponent(CC.translate(
                        "&cYou cannot kick this player!"
                )));
                return;
            }

            if (!target.isConnected()) {
                player.sendMessage(new TextComponent(CC.translate(
                        "&cThis player is not online on the network!"
                )));
                return;
            }

            target.disconnect(new TextComponent(CC.translate(
                    "&cYou have been kicked from the server!\n\n" +
                            "&7Reason: &f" + reason.toString().trim() + "\n" +
                            "&7Punished falsely? Create a ticket at &b&nhttps://hybridplays.com/discord&7 and explain the situation."
            )));

            ModerationLogger.logKick(target.getUniqueId(), player.getUniqueId(), reason.toString().trim(), System.currentTimeMillis());

            player.sendMessage(new TextComponent(CC.translate(
                    "&aYou successfully kicked " + targetRank
                            .getRank().getPrefixSpace() + target.getName() + " &afrom the network with the &areason: &f" + reason.toString().trim() + "&a. This has been logged."
            )));

        }
    }
}














