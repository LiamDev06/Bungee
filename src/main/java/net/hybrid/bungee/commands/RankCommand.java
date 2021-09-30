package net.hybrid.bungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RankCommand extends Command {

    public RankCommand(){
        super("rank");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            // Console
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;


    }
}












