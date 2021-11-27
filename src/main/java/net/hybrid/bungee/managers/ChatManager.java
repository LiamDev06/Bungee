package net.hybrid.bungee.managers;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.ChatChannel;
import net.hybrid.bungee.utility.ChatChannelManager;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class ChatManager implements Listener {

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.isProxyCommand()) return;
        if (event.isCommand()) return;
        if (event.getMessage().startsWith("/")) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        RankManager rankManager = new RankManager(player.getUniqueId());
        ChatChannelManager chatChannelManager = new ChatChannelManager(player.getUniqueId());

        if (chatChannelManager.getChatChannel() == ChatChannel.OWNER) {
            event.setCancelled(true);

            for (UUID target : BungeePlugin.getInstance().getMongo().getOwners()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.OWNER.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(replaceWithEmote(event.getMessage()))
                ));
            }
            return;
        }

        if (chatChannelManager.getChatChannel() == ChatChannel.ADMIN) {
            event.setCancelled(true);

            for (UUID target : BungeePlugin.getInstance().getMongo().getAdmins()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.ADMIN.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(replaceWithEmote(event.getMessage()))
                ));
            }
            return;
        }

        if (chatChannelManager.getChatChannel() == ChatChannel.STAFF) {
            event.setCancelled(true);

            for (UUID target : BungeePlugin.getInstance().getMongo().getStaff()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(replaceWithEmote(event.getMessage()))
                ));
            }
        }

    }

    public static void sendOwnerChatMessage(String message, RankManager rankManager) {
        for (UUID target : BungeePlugin.getInstance().getMongo().getOwners()) {
            ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
            targetPlayer.sendMessage(new TextComponent(
                    ChatChannel.OWNER.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(replaceWithEmote(message))
            ));
        }
    }

    public static void sendAdminChatMessage(String message, RankManager rankManager) {
        for (UUID target : BungeePlugin.getInstance().getMongo().getAdmins()) {
            ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
            targetPlayer.sendMessage(new TextComponent(
                    ChatChannel.ADMIN.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(replaceWithEmote(message))
            ));
        }
    }

    public static void sendStaffChatMessage(String message, RankManager rankManager) {
        for (UUID target : BungeePlugin.getInstance().getMongo().getStaff()) {
            ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
            targetPlayer.sendMessage(new TextComponent(
                    ChatChannel.STAFF.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(replaceWithEmote(message))
            ));
        }
    }

    public static String replaceWithEmote(String input) {

        input = input.replace(":cool:", CC.translate("&a&lCool&r"));
        input = input.replace(":shrug:", CC.translate("&d¯\\_(ツ)_/¯&r"));
        input = input.replace(":wow:", CC.translate("&b&lW&4&lO&b&lW&r"));
        input = input.replace("o/", CC.translate("&5(o_o)/&r"));
        input = input.replace(":hybrid:", CC.translate("&2&lHYBRID&r"));
        input = input.replace(":L:", CC.translate("&c&lL&r"));
        input = input.replace(":sad:", CC.translate("&e◕︵◕&r"));
        input = input.replace(":happy:", CC.translate("&6&l◕◡◕&r"));
        input = input.replace(":embarrassed:", CC.translate("&b⊙﹏⊙&r"));
        input = input.replace(":eyes:", CC.translate("&aʘ.ʘ&r"));
        input = input.replace(":hehe:", CC.translate("&0hehe&r"));

        return input;
    }

}











