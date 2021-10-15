package net.hybrid.bungee.utility;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.data.Mongo;
import org.bson.Document;

import java.util.HashMap;
import java.util.UUID;

public class RankManager {

    private final UUID uuid;
    private final Mongo mongo = BungeePlugin.getInstance().getMongo();
    private static final HashMap<UUID, PlayerRank> rankCache = new HashMap<>();

    public RankManager(UUID uuid) {
        this.uuid = uuid;
    }

    public PlayerRank getRank() {
        if (rankCache.containsKey(uuid)) {
            return rankCache.get(uuid);
        }

        Document document = mongo.loadDocument("playerData", uuid);

        if (!document.getString("staffRank").equalsIgnoreCase("")) {
            return PlayerRank.valueOf(document.getString("staffRank").toUpperCase());

        } else if (!document.getString("specialRank").equalsIgnoreCase("")) {
            return PlayerRank.valueOf(document.getString("specialRank").toUpperCase());

        } else {
            return PlayerRank.valueOf(document.getString("playerRank").toUpperCase());
        }
    }

    public String getColoredName() {
        return this.getRank().getColor() + BungeePlugin.getInstance().getProxy().getPlayer(uuid).getName();
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

    public static HashMap<UUID, PlayerRank> getRankCache() {
        return rankCache;
    }

}










