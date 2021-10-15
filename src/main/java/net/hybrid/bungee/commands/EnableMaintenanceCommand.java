package net.hybrid.bungee.commands;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.PlayerRank;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.concurrent.TimeUnit;

public class EnableMaintenanceCommand extends Command {

    // /enablemaintenance [all/<server>]

    public EnableMaintenanceCommand() {
        super("enablemaintenance", "", "enablemain", "emaintenance");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        RankManager rank = new RankManager(player.getUniqueId());
        if (!rank.isAdmin()) {
            player.sendMessage(new TextComponent(CC.translate("&cYou must be an admin or above to perform this command!")));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(new TextComponent(CC.translate("&cMissing arguments! Valid usage: /enablemaintenance [all/<server>]")));
            return;
        }

        String value = args[0];
        if (value.equalsIgnoreCase("all")) {

        }

        ServerInfo server = BungeePlugin.getInstance().getProxy().getServerInfo(value.toLowerCase());
        if (server == null) {
            player.sendMessage(new TextComponent(CC.translate("&cThere is no server available with that name. Did you spell it right?")));
            return;
        }


    }
}











