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

public class DisableMaintenanceCommand extends Command {

    // /disablemaintenance [all/<server>]

    public DisableMaintenanceCommand() {
        super("disablemaintenance", "", "disablemain", "dmaintenance");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        RankManager rank = new RankManager(player.getUniqueId());
        if (!rank.hasRank(PlayerRank.ADMIN)) {
            player.sendMessage(new TextComponent(CC.translate("&cYou must be an admin or above to perform this command!")));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(new TextComponent(CC.translate("&cMissing arguments! Valid usage: /enablemaintenance [all/<server>]")));
            return;
        }

        String value = args[0];
        if (value.equalsIgnoreCase("all")) {
            BungeePlugin.getInstance().getProxy().broadcast(new TextComponent(CC.translate(
                    "&7&m-------------------------------"
            )));

            BungeePlugin.getInstance().getProxy().broadcast(new TextComponent(CC.translate(
                    "&c&lNETWORK MAINTENANCE"
            )));

            BungeePlugin.getInstance().getProxy().broadcast(new TextComponent(CC.translate(
                    "&bThe Hybrid Network will enter maintenance mode in 60 seconds... " +
                            "This will force-disconnect all players online."
            )));


            BungeePlugin.getInstance().getProxy().broadcast(new TextComponent(CC.translate(
                    "&7&m-------------------------------"
            )));

            try {
                TimeUnit.SECONDS.sleep(30);

                BungeePlugin.getInstance().getProxy().broadcast(new TextComponent(CC.translate(
                        "&c&lMAINTENANCE! &cMaintenance mode going live in &630 seconds&c..."
                )));

                TimeUnit.SECONDS.sleep(15);

                BungeePlugin.getInstance().getProxy().broadcast(new TextComponent(CC.translate(
                        "&c&lMAINTENANCE! &cMaintenance mode going live in &615 seconds&c..."
                )));

                TimeUnit.SECONDS.sleep(10);

                BungeePlugin.getInstance().getProxy().broadcast(new TextComponent(CC.translate(
                        "&c&lMAINTENANCE! &cMaintenance mode going live in &65 seconds&c..."
                )));
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            for (ProxiedPlayer target : BungeePlugin.getInstance().getProxy().getPlayers()) {

            }
            return;
        }

        ServerInfo server = BungeePlugin.getInstance().getProxy().getServerInfo(value.toLowerCase());
        if (server == null) {
            player.sendMessage(new TextComponent(CC.translate("&cThere is no server available with that name. Did you spell it right?")));
            return;
        }



    }
}












