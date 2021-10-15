package net.hybrid.bungee.utility;

import net.hybrid.bungee.BungeePlugin;
import org.bson.Document;

import java.util.UUID;

public class MessageReply {

    public static void setMessageReply(UUID player, UUID target) {
        Document document = BungeePlugin.getInstance().getMongo().loadDocument("playerData", player);

        if (!document.getString("messageLastReplyUuid").equalsIgnoreCase(target.toString())) {
            document.replace("messageLastReplyUuid", target.toString());
        }

        BungeePlugin.getInstance().getMongo().saveDocument("playerData", document, player);
    }

    public static UUID getMessageReply(UUID from) {
        Document document = BungeePlugin.getInstance().getMongo().loadDocument("playerData", from);

        if (document.getString("messageLastReplyUuid").equalsIgnoreCase("")) {
            return null;
        } else {
            return UUID.fromString(document.getString("messageLastReplyUuid"));
        }
    }

}
