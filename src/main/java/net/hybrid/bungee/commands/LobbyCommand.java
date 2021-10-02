package net.hybrid.bungee.commands;

import net.hybrid.bungee.BungeePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LobbyCommand extends Command {

    public LobbyCommand() {
        super("lobby", "", "hub", "mainhub", "mainlobby", "l");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (player.getServer().getInfo().getName().equalsIgnoreCase("mainlobby1")) {
            player.sendMessage(new ComponentBuilder("You are already at the main lobby!").color(ChatColor.RED).create());
            return;
        }

        player.sendMessage(new ComponentBuilder("Sending you to mainlobby1...").color(ChatColor.GREEN).create());
        ServerInfo server = BungeePlugin.getInstance().getProxy().getServerInfo("mainlobby1");
        player.connect(server);
    }
}












