package net.hybrid.bungee.managers;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;

public class ChatManager implements Listener {

    public static ArrayList<ProxiedPlayer> owners = new ArrayList<>();

    @EventHandler
    public void onPlayerChat(ChatEvent event) {
        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        RankManager rankManager = new RankManager(player.getUniqueId());
        ChatChannelManager chatChannelManager = new ChatChannelManager(player.getUniqueId());

        String startFormatAllColor = rankManager.getRank().getColor() + player.getName();

        if (chatChannelManager.getChatChannel() == ChatChannel.OWNER) {
            if (!event.isCommand() && !event.isProxyCommand()) {
                event.setCancelled(true);

                for (ProxiedPlayer target : owners) {
                    target.sendMessage(new ComponentBuilder(CC.translate(
                            ChatChannel.OWNER.getPrefix() + startFormatAllColor +
                                    "&f: " + event.getMessage()
                    )).create());
                }
            }
        }
    }
}













