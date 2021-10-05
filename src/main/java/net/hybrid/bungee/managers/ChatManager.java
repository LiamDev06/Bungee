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
                        ChatChannel.OWNER.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(event.getMessage())
                ));
            }
            return;
        }

        if (chatChannelManager.getChatChannel() == ChatChannel.ADMIN) {
            event.setCancelled(true);

            for (UUID target : BungeePlugin.getInstance().getMongo().getAdmins()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.ADMIN.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(event.getMessage())
                ));
            }
            return;
        }

        if (chatChannelManager.getChatChannel() == ChatChannel.STAFF) {
            event.setCancelled(true);

            for (UUID target : BungeePlugin.getInstance().getMongo().getStaff()) {
                ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
                targetPlayer.sendMessage(new TextComponent(
                        ChatChannel.STAFF.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(event.getMessage())
                ));
            }
        }

    }

    public static void sendOwnerChatMessage(String message, RankManager rankManager) {
        for (UUID target : BungeePlugin.getInstance().getMongo().getOwners()) {
            ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
            targetPlayer.sendMessage(new TextComponent(
                    ChatChannel.OWNER.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(message)
            ));
        }
    }

    public static void sendAdminChatMessage(String message, RankManager rankManager) {
        for (UUID target : BungeePlugin.getInstance().getMongo().getAdmins()) {
            ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
            targetPlayer.sendMessage(new TextComponent(
                    ChatChannel.ADMIN.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(message)
            ));
        }
    }

    public static void sendStaffChatMessage(String message, RankManager rankManager) {
        for (UUID target : BungeePlugin.getInstance().getMongo().getStaff()) {
            ProxiedPlayer targetPlayer = BungeePlugin.getInstance().getProxy().getPlayer(target);
            targetPlayer.sendMessage(new TextComponent(
                    ChatChannel.STAFF.getPrefix() + " " + rankManager.getColoredName() + "§f: " + CC.translate(message)
            ));
        }
    }

}











