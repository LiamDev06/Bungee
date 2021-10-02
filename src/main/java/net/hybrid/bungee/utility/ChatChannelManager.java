package net.hybrid.bungee.utility;

import net.hybrid.bungee.BungeePlugin;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;

import java.util.UUID;

public class ChatChannelManager {

    private final UUID uuid;
    public ChatChannelManager(UUID uuid) {
        this.uuid = uuid;
    }

    public ChatChannel getChatChannel() {
        Document document = BungeePlugin.getInstance().getMongo().loadDocument("playerData", uuid);
        return ChatChannel.valueOf(document.getString("chatChannel").toUpperCase());
    }

    public ChatColor getChatColor() {
        Document document = BungeePlugin.getInstance().getMongo().loadDocument("playerData", uuid);
        return ChatColor.of(document.getString("chatColor").toUpperCase());
    }

}
