package net.hybrid.bungee.commands;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.PlayerRank;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bson.Document;

import java.util.UUID;

public class PurchaseRankCommand extends Command {

    public PurchaseRankCommand() {
        super("purchaserank");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            Mongo mongo = BungeePlugin.getInstance().getMongo();
            ProxyServer proxy = BungeePlugin.getInstance().getProxy();

            if (args.length < 2) {
                commandSender.sendMessage(new TextComponent("§cMissing arguments! Valid usage: /purchaserank <uuid> <rank>"));
                return;
            }

            UUID uuid = UUID.fromString(args[0]);
            PlayerRank rank = PlayerRank.valueOf(args[1].toUpperCase());

            if (proxy.getPlayer(uuid) != null) {
                ProxiedPlayer player = proxy.getPlayer(uuid);
                player.sendMessage(new TextComponent(CC.translate("&e&lIMPORTANT! &e" +
                        "New items from our store has just been bought to your Minecraft account. &eLeave the server and re-join to &ereceive the items.")));
            }

            Document document = mongo.loadDocument("serverData", "serverDataType", "rankPurchases");
            if (document.containsKey(uuid.toString())) {
                PlayerRank existingRank = PlayerRank.valueOf(document.getString(uuid.toString()).toUpperCase());

                if (rank.getOrdering() > existingRank.getOrdering()) {
                    document.replace(uuid.toString(), rank.name());
                }

            } else {
                document.append(uuid.toString(), rank.name());
            }

            mongo.saveDocument("serverData", document, "serverDataType", "rankPurchases");
            commandSender.sendMessage(new TextComponent(CC.translate("&aCompleted without errors.")));
        }
    }
}







