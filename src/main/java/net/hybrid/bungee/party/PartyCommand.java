package net.hybrid.bungee.party;

import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyCommand extends Command {

    public PartyCommand() {
        super("party", "", "p", "serverparty");
    }

    private ProxiedPlayer player;

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        this.player = player;

        RankManager rank = new RankManager(player.getUniqueId());

        if (args.length == 0) {
            PartyUtils.sendHelpMenu(player);
            return;
        }

        if (args[0].equalsIgnoreCase("invite")) {
            return;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            return;
        }

        if (args[0].equalsIgnoreCase("deny")) {
            return;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            return;
        }

        if (args[0].equalsIgnoreCase("warp")) {
            return;
        }

        if (args[0].equalsIgnoreCase("delete")) {
            return;
        }

        if (args[0].equalsIgnoreCase("transfer")) {
            return;
        }

        if (args[0].equalsIgnoreCase("kick")) {
            return;
        }

        if (args[0].equalsIgnoreCase("chat")) {
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            return;
        }

        if (args[0].equalsIgnoreCase("mute")) {
            return;
        }

        if (args[0].equalsIgnoreCase("promote")) {
            return;
        }

        if (args[0].equalsIgnoreCase("demote")) {
            return;
        }

        // -----------------------------------------------------
        // Admin Commands

        if (args[0].equalsIgnoreCase("hijack")) {
            if (!rank.isAdmin()){
                message("&cNope pal, do not even think about it...");
                return;
            }
            return;
        }

        if (args[0].equalsIgnoreCase("yoink")) {
            if (!rank.isAdmin()){
                message("&cNope pal, do not even think about it...");
                return;
            }
            return;
        }

        if (args[0].equalsIgnoreCase("forcejoin")) {
            if (!rank.isAdmin()){
                message("&cNope pal, do not even think about it...");
                return;
            }
            return;
        }

        // Do /p <player>

    }

    private void message(String message) {
        player.sendMessage(new TextComponent(CC.translate(message)));
    }

}












