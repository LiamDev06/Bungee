package net.hybrid.bungee.commands;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.MessageReply;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.bson.Document;

import java.util.UUID;

public class MsgCommand extends Command {

    public MsgCommand() {
        super("msg", "", "whisper", "tell", "message");
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) return;
        ProxiedPlayer player = (ProxiedPlayer) commandSender;

        if (isMuted(player.getUniqueId())) {
            player.sendMessage(new TextComponent(CC.translate(
                    "&c&lYou are &c&lcurrently MUTED &c&land can &c&ltherefore not &c&lmessage &c&lanyone!"
            )));
            return;
        }

        if (args.length == 0) {
            player.sendMessage(new TextComponent(CC.translate("&c&lMISSING ARGUMENTS! " +
                    "&cInsert &ca &cplayer &cto &cmessage &cwith &c/msg &c<player> &c<message>")));
            return;
        }

        if (args.length > 1) {
            ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(args[0]);
            StringBuilder message = new StringBuilder();
            int count = 0;

            if (target == null) {
                player.sendMessage(new TextComponent(CC.translate("&cThis player &cis not online!")));
                return;
            }

            if (target.getUniqueId() == player.getUniqueId()) {
                player.sendMessage(new TextComponent(CC.translate("&cYou cannot &cmessage yourself!")));
                return;
            }

            if (!canSendMessageTo(target.getUniqueId()) && !new RankManager(player.getUniqueId()).isStaff()) {
                player.sendMessage(new TextComponent(CC.translate("&cYou cannot &csend a private &cmessage to this player &cdue to their settings!")));
            }

            if (!target.isConnected()) {
                player.sendMessage(new TextComponent(CC.translate("&cThis player &cis not online!")));
                return;
            }

            for (String s : args) {
                if (count >= 1) {
                    message.append(s).append(" ");
                }

                count++;
            }

            RankManager playerRank = new RankManager(player.getUniqueId());
            RankManager targetRank = new RankManager(target.getUniqueId());

            player.sendMessage(new TextComponent(CC.translate(
                    "&7[" + playerRank.getRank().getPrefixSpace() + player.getName() + " &7&l>> " + targetRank.getRank().getPrefixSpace() + target.getName() + "&7] &f" + message.toString()
            )));

            target.sendMessage(new TextComponent(CC.translate(
                    "&7[" + playerRank.getRank().getPrefixSpace() + player.getName() + " &7&l>> " + targetRank.getRank().getPrefixSpace() + target.getName() + "&7] &f" + message.toString()
            )));

            MessageReply.setMessageReply(target.getUniqueId(), player.getUniqueId());

        } else {
            player.sendMessage(new TextComponent(CC.translate("&c&lMISSING ARGUMENTS! " +
                    "&cInsert &ca &cplayer &cto &cmessage &cwith &c/msg &c<player> &c<message>")));
        }
    }

    public static boolean canSendMessageTo(UUID uuid) {
        return true;
    }

    public static boolean isMuted(UUID uuid) {
        Mongo mongo = BungeePlugin.getInstance().getMongo();
        Document document = mongo.loadDocument("playerData", uuid);
        if (document.getString("muteId").equalsIgnoreCase("")) {
            return false;
        }

        Document muteDoc = mongo.loadDocument("punishments", "punishmentId",
                document.getString("muteId"));

        if (System.currentTimeMillis() > muteDoc.getLong("expires")) {
            document.replace("muted", false);
            document.replace("muteId", "");
            mongo.saveDocument("playerData", document, uuid);

            ProxiedPlayer player = BungeePlugin.getInstance().getProxy().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(new TextComponent(CC.translate("&7&m-------------&7&m--------------")));
                player.sendMessage(new TextComponent(CC.translate("&a&lMUTE &a&lEXPIRED!")));
                player.sendMessage(new TextComponent(CC.translate("&aYour mute &ahas now &aexpired meaning &ayou can send &amessages again.")));
                player.sendMessage(new TextComponent(" "));
                player.sendMessage(new TextComponent(CC.translate("&7Please get &7familiar with &7our rules to &7avoid more &7punishments in &7the future.")));
                player.sendMessage(new TextComponent(CC.translate("&7&m-------------&7&m--------------")));
            }

            return false;
        } else {
            return true;
        }
    }
}










