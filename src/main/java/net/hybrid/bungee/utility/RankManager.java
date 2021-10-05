package net.hybrid.bungee.utility;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;

import java.util.UUID;

public class RankManager {

    private final UUID uuid;
    private final Mongo mongo = BungeePlugin.getInstance().getMongo();

    public RankManager(UUID uuid) {
        this.uuid = uuid;
    }

    public void setRank(PlayerRank playerRank) {
        Document document = mongo.loadDocument("playerData", uuid);

        if (playerRank.isStaffRank()) {
            document.replace("staffRank", playerRank.name());
            document.replace("staffNotifyMode", true);

        } else if (playerRank == PlayerRank.YOUTUBER || playerRank == PlayerRank.TWITCH_STREAMER || playerRank == PlayerRank.PARTNER) {
            document.replace("specialRank", playerRank.name());
            document.replace("staffRank", "");
            document.replace("staffNotifyMode", false);
            document.replace("staffBuildMode", false);

            if (document.getString("chatChannel").equalsIgnoreCase("staff") ||
                    document.getString("chatChannel").equalsIgnoreCase("builder") ||
                    document.getString("chatChannel").equalsIgnoreCase("admin") ||
                    document.getString("chatChannel").equalsIgnoreCase("owner")) {
                document.replace("chatChannel", "ALL");
            }

        } else {
            document.replace("playerRank", playerRank.name());
            document.replace("specialRank", "");
            document.replace("staffRank", "");
            document.replace("staffNotifyMode", false);
            document.replace("staffBuildMode", false);

            if (document.getString("chatChannel").equalsIgnoreCase("staff") ||
                    document.getString("chatChannel").equalsIgnoreCase("builder") ||
                    document.getString("chatChannel").equalsIgnoreCase("admin") ||
                    document.getString("chatChannel").equalsIgnoreCase("owner")) {
                document.replace("chatChannel", "ALL");
            }

        }

        document.replace("adminDebugMode", false);
        document.replace("chatColor", ChatColor.WHITE.name());

        mongo.saveDocument("playerData", document, uuid);
    }

    public PlayerRank getRank() {
        Document document = mongo.loadDocument("playerData", uuid);

        if (!document.getString("staffRank").equalsIgnoreCase("")) {
            return PlayerRank.valueOf(document.getString("staffRank").toUpperCase());

        } else if (!document.getString("specialRank").equalsIgnoreCase("")) {
            return PlayerRank.valueOf(document.getString("specialRank").toUpperCase());

        } else {
            return PlayerRank.valueOf(document.getString("playerRank").toUpperCase());
        }
    }

    public boolean hasRank(PlayerRank playerRank) {
        return (getRank().getOrdering() >= playerRank.getOrdering());
    }

    public boolean hasRankOnly(PlayerRank playerRank) {
        return getRank() == playerRank;
    }

    public boolean isStaff() {
        return getRank().isStaffRank();
    }

    public boolean isModerator() {
        return hasRank(PlayerRank.MODERATOR);
    }

    public boolean isSeniorModerator() {
        return hasRank(PlayerRank.SENIOR_MODERATOR);
    }

    public boolean isAdmin() {
        return hasRank(PlayerRank.ADMIN);
    }

    public String getName() {
        return BungeePlugin.getInstance().getProxy().getPlayer(uuid).getName();
    }

    public String getColoredName() {
        return this.getRank().getColor() + getName();
    }

}










