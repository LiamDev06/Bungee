package net.hybrid.bungee.commands;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.MessageReply;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.UUID;

public class MsgCommand extends Command {

    public MsgCommand() {
        super("msg", "", "whisper", "tell", "message");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (args.length == 0) {
            player.sendMessage(new TextComponent(CC.translate("&cMissing arguments! " +
                    "Insert a player to message with /msg <player> <message>")));
        }

        if (args.length > 1) {
            ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(args[0]);
            StringBuilder message = new StringBuilder();
            int count = 0;

            if (target == null) {
                player.sendMessage(new TextComponent(CC.translate("&cThis player is not online!")));
                return;
            }

            if (target.getUniqueId() == player.getUniqueId()) {
                player.sendMessage(new TextComponent(CC.translate("&cYou cannot message yourself!")));
                return;
            }

            if (!canSendMessageTo(target.getUniqueId()) && !new RankManager(player.getUniqueId()).isStaff()) {
                player.sendMessage(new TextComponent(CC.translate("&cYou cannot send a private message to this player due to their settings!")));
            }

            if (!target.isConnected()) {
                player.sendMessage(new TextComponent(CC.translate("&cThis player is not online!")));
                return;
            }

            for (String s : args) {
                if (count >= 1) {
                    message.append(s).append(" ");
                }

                count++;
            }

            RankManager playerRank = new RankManager(player.getUniqueId());
            RankManager targetRank = new RankManager(target.getUniqueId());

            player.sendMessage(new TextComponent(CC.translate(
                    "&7[" + playerRank.getRank().getPrefixSpace() + player.getName() + " &7&l>> " + targetRank.getRank().getPrefixSpace() + target.getName() + "&7] &f" + message.toString()
            )));

            target.sendMessage(new TextComponent(CC.translate(
                    "&7[" + playerRank.getRank().getPrefixSpace() + player.getName() + " &7&l>> " + targetRank.getRank().getPrefixSpace() + target.getName() + "&7] &f" + message.toString()
            )));

            MessageReply.setMessageReply(target.getUniqueId(), player.getUniqueId());

        } else {
            player.sendMessage(new TextComponent(CC.translate("&cMissing arguments! " +
                    "Insert a player to message with /msg <player> <message>")));
        }
    }

    public static boolean canSendMessageTo(UUID uuid) {
        return true;
    }
}










