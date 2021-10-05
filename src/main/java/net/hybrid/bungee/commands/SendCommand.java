package net.hybrid.bungee.commands;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SendCommand extends Command {

    public SendCommand() {
        super("sendplayer");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        RankManager rankManager = new RankManager(player.getUniqueId());
        if (!rankManager.isAdmin()) {
            player.sendMessage(new ComponentBuilder("You must be an admin or above to perform this!").color(ChatColor.RED).create());
            return;
        }

        if (args.length == 0) {
            player.sendMessage(new ComponentBuilder
                    ("Missing arguments! Use /sendplayer <player> <server> to send!")
                    .color(ChatColor.RED).create());
            return;
        }

        try {
            if (args.length > 1) {
                ProxiedPlayer target = null;
                String server = args[1];

                int times = 0;
                int amount = BungeePlugin.getInstance().getProxy().getPlayers().size();
                for (ProxiedPlayer targetLoop : BungeePlugin.getInstance().getProxy().getPlayers()) {

                    if (targetLoop.getName().equalsIgnoreCase(args[0])) {
                        target = targetLoop;
                        break;
                    }

                    times++;
                    if (times == amount) {
                        // NOt found
                        player.sendMessage(new ComponentBuilder(
                                CC.translate("&cThis player is not online!")
                        ).create());
                        break;
                    }
                }

                assert target != null;
                target.connect(BungeePlugin.getInstance().getProxy().getServerInfo(server));
                target.sendMessage(new ComponentBuilder("Sending you to " +
                        BungeePlugin.getInstance().getProxy().getServerInfo(server).getName() + "...").color(ChatColor.GREEN).create());

                player.sendMessage(new ComponentBuilder(CC.translate(
                        "&aYou sent " + target.getName() + " to server " + BungeePlugin.getInstance().getProxy()
                                .getServerInfo(server).getName() + "!"
                )).create());
            } else {
                player.sendMessage(new ComponentBuilder
                        ("Missing arguments! Use /sendplayer <player> <server> to send!")
                        .color(ChatColor.RED).create());
            }
        } catch (Exception exception) {
            player.sendMessage(new ComponentBuilder
                    ("The player you entered was not online or you entered the wrong server! Try again!")
                    .color(ChatColor.RED).create());
        }
    }
}














