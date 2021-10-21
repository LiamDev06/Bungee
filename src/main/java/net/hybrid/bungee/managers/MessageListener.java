package net.hybrid.bungee.managers;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.hybrid.bungee.utility.PlayerRank;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PluginMessageEvent;
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
            final String serverValue = in.readUTF();
            final String updateValue = in.readUTF();
            final String rankName = in.readUTF();
            final String uuidValue = in.readUTF();

            if (!serverValue.equalsIgnoreCase("ONLINE") &&
            !updateValue.equalsIgnoreCase("RankUpdate")) return;

            UUID targetUuid = UUID.fromString(uuidValue);
            PlayerRank playerRank = PlayerRank.valueOf(rankName.toUpperCase());

            mongo.getStaff().remove(targetUuid);
            mongo.getAdmins().remove(targetUuid);
            mongo.getOwners().remove(targetUuid);
            RankManager.getRankCache().remove(targetUuid);

            if (playerRank.isStaffRank()) {
                mongo.getStaff().add(targetUuid);
            }

            if (playerRank == PlayerRank.ADMIN) {
                mongo.getAdmins().add(targetUuid);
            }

            if (playerRank == PlayerRank.OWNER) {
                mongo.getAdmins().add(targetUuid);
                mongo.getOwners().add(targetUuid);
            }
        }
    }
}
