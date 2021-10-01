package net.hybrid.bungee.utility;

import net.md_5.bungee.api.ChatColor;

public enum PlayerRank {

    OWNER(ChatColor.DARK_RED, CC.translate("&4[OWNER]"), CC.translate("&4[OWNER] "), CC.translate("&4Owner"), true, 11),
    ADMIN(ChatColor.DARK_RED, CC.translate("&c[ADMIN]"), CC.translate("&c[ADMIN] "), CC.translate("&cAdmin"), true, 10),
    SENIOR_MODERATOR(ChatColor.DARK_RED, CC.translate("&6[SR MOD]"), CC.translate("&6[SR MOD] "), CC.translate("&6Senior Moderator"), true, 9),
    MODERATOR(ChatColor.DARK_RED, CC.translate("&6[MOD]"), CC.translate("&6[MOD] "), CC.translate("&6Moderator"), true, 8),
    HELPER(ChatColor.DARK_RED, CC.translate("&3[HELPER]"), CC.translate("&3[HELPER] "), CC.translate("&3Helper"), true, 7),
    PARTNER(ChatColor.DARK_RED, CC.translate("&2[PARTNER]"), CC.translate("&2[PARTNER] "), CC.translate("&2Partner"), false, 6),
    YOUTUBER(ChatColor.DARK_RED, CC.translate("&c[YOU&fTUBE&c]"), CC.translate("&c[YOU&fTUBE&c] "), CC.translate("&cYouTube"), false, 5),
    TWITCH_STREAMER(ChatColor.DARK_RED, CC.translate("&5[TW&fICH&5]"), CC.translate("&5[TW&fICH&5] "), CC.translate("&5Twitch"), false, 4),
    DIAMOND(ChatColor.DARK_RED, CC.translate("&b[DIAMOND]"), CC.translate("&b[DIAMOND] "), CC.translate("&bDiamond"), false, 3),
    IRON(ChatColor.DARK_RED, CC.translate("&f[IRON]"), CC.translate("&f[IRON] "), CC.translate("&fIron"), false, 2),
    MEMBER(ChatColor.DARK_RED, CC.translate("&a"), CC.translate("&a"), CC.translate("&aMember"), false, 1);

    private final ChatColor color;
    private final String prefix;
    private final String prefixSpace;
    private final String displayName;
    private final int ordering;
    private final boolean isStaffRank;

    PlayerRank(ChatColor color, String prefix, String prefixSpace, String displayName, boolean isStaffRank, int ordering){
        this.color = color;
        this.prefix = prefix;
        this.prefixSpace = prefixSpace;
        this.displayName = displayName;
        this.ordering = ordering;
        this.isStaffRank = isStaffRank;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPrefixSpace() {
        return prefixSpace;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isStaffRank() {
        return isStaffRank;
    }

    public int getOrdering() {
        return ordering;
    }

}







