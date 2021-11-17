package net.hybrid.bungee.commands;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class FindPlayerCommand extends Command {

    public FindPlayerCommand() {
        super("findplayer");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        RankManager manager = new RankManager(player.getUniqueId());
        if (!manager.isStaff()) {
            player.sendMessage(new TextComponent(CC.translate("&cYou &care &cnot &callowed &cto &cperform &cthis &ccommand!")));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(new TextComponent(CC.translate("&c&lMISSING ARGUMENTS! &cPlease specify &ca player &cwith &6/findplayer <player>&c!")));
            return;
        }

        ProxyServer proxy = BungeePlugin.getInstance().getProxy();
        ProxiedPlayer target = proxy.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(new TextComponent(CC.translate("&cThis player &cis not &conline!")));
            return;
        }

        player.sendMessage(new TextComponent(CC.translate(
                "&6" + target.getName() + " &ais currently &aplaying on &athe server &b" + target.getServer().getInfo().getName() + "&a!"
        )));

    }
}












