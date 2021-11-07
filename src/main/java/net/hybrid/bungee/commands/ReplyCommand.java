package net.hybrid.bungee.commands;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.MessageReply;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplyCommand extends Command {

    public ReplyCommand() {
        super("reply", "", "r");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (MsgCommand.isMuted(player.getUniqueId())) {
            player.sendMessage(new TextComponent(CC.translate(
                    "&c&lYou are currently MUTED and can therefore not reply to anyone!"
            )));
            return;
        }

        if (MessageReply.getMessageReply(player.getUniqueId()) == null) {
            player.sendMessage(new TextComponent(CC.translate("&cYou have no one to reply to!")));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(new TextComponent(CC.translate("&cMissing arguments! Reply to your last message with /reply <message>")));
            return;
        }

        StringBuilder message = new StringBuilder();
        ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(MessageReply.getMessageReply(player.getUniqueId()));

        if (target == null) {
            player.sendMessage(new TextComponent(CC.translate("&cThis player is not online!")));
            return;
        }

        if (target.getUniqueId() == player.getUniqueId()) {
            player.sendMessage(new TextComponent(CC.translate("&cYou cannot reply to yourself!")));
            return;
        }

        if (!MsgCommand.canSendMessageTo(target.getUniqueId()) && !new RankManager(player.getUniqueId()).isStaff()) {
            player.sendMessage(new TextComponent(CC.translate("&cYou cannot reply to this player due to their settings!")));
        }

        if (!target.isConnected()) {
            player.sendMessage(new TextComponent(CC.translate("&cThis player is not online!")));
            return;
        }

        for (String s : args) {
            message.append(s).append(" ");
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
    }
}
