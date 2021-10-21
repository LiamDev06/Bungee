package net.hybrid.bungee.commands;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

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
            for (ProxiedPlayer target : BungeePlugin.getInstance().getProxy().getPlayers()) {
                RankManager rankManager = new RankManager(target.getUniqueId());
                if (!rankManager.isStaff()) {
                    target.disconnect(new TextComponent(CC.translate(
                            "&c&lMAINTENANCE MODE\n" +
                                    "&cHybrid is currently in maintenance mode!\n" +
                                    "&7Check out &6https://dsc.gg/hybridserver&7 for updates!"
                    )));
                }

                BungeePlugin.getInstance().getProxy().broadcast(new TextComponent(CC.translate(
                        "&6&lNETWORK WIDE MAINTENANCE MODE HAS BEEN &a&lENABLED&6&l!"
                )));
            }
        }

        ServerInfo server = BungeePlugin.getInstance().getProxy().getServerInfo(value.toLowerCase());
        if (server == null) {
            player.sendMessage(new TextComponent(CC.translate("&cThere is no server available with that name. Did you spell it right?")));
            return;
        }

        for (ProxiedPlayer target : server.getPlayers()) {
            RankManager rankManager = new RankManager(target.getUniqueId());
            if (!rankManager.isStaff()) {
                target.connect(BungeePlugin.getInstance().getProxy().getServerInfo("mainlobby1"));
            }

            target.sendMessage(new TextComponent(CC.translate(
                    "&aThe server you were on entered &cmaintenance mode&a! You were re-routed to the main lobby..."
            )));
        }

        for (ProxiedPlayer target : server.getPlayers()) {
            target.sendMessage(new TextComponent(CC.translate(
                    "&6&lMAINTENANCE MODE FOR SERVER &b&l" + server.getName() + " &6&lHAS BEEN &a&lENABLED&6&l!"
            )));
        }

    }
}











