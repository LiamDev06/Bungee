package net.hybrid.bungee.managers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.utility.PlayerRank;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class MessageListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        if (!event.getTag().equals("BungeeCord")) return;

        Mongo mongo = BungeePlugin.getInstance().getMongo();

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String subChannel = in.readUTF();

        if (subChannel.equalsIgnoreCase("Forward")) {
            String serverValue = in.readUTF();
            String updateValue = in.readUTF();
            String rankName = in.readUTF();
            String uuidValue = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
            !updateValue.equalsIgnoreCase("RankUpdate")) return;

            UUID targetUuid = UUID.fromString(uuidValue);
            PlayerRank playerRank = PlayerRank.valueOf(rankName.toUpperCase());
            RankManager rankManager = new RankManager(targetUuid);


        }
    }
}
