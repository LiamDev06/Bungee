package net.hybrid.bungee.commands;

import net.hybrid.bungee.managers.ChatManager;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.ChatChannel;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class AdminChatCommand extends Command {

    public AdminChatCommand() {
        super("adminchat", "", "ad", "adc", "adchat");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (args.length == 0) {
            player.chat("/chat admin");
            return;
        }

        RankManager rankManager = new RankManager(player.getUniqueId());
        if (!rankManager.hasRank(ChatChannel.ADMIN.getRequiredRank())) {
            player.sendMessage(new TextComponent(CC.translate("&cYou do not have permission to do this!")));
            return;
        }

        StringBuilder message = new StringBuilder();

        for (String s : args) {
            message.append(s).append(" ");
        }

        ChatManager.sendAdminChatMessage(message.toString().trim(), rankManager);
    }

}
