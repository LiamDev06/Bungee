package net.hybrid.bungee.utility;

import java.util.Arrays;
import java.util.List;

public enum ChatChannel {

    ALL("", PlayerRank.MEMBER, Arrays.asList("a", "all", "main", "mainchat")),
    GANG(CC.translate("&e(Gang Chat)"), PlayerRank.MEMBER, Arrays.asList("g", "gang", "gangchat", "gchat")),
    PARTY(CC.translate("&d(Party Chat)"), PlayerRank.MEMBER, Arrays.asList("p", "party", "partychat", "pchat")),
    STAFF(CC.translate("&b[STAFF]"), PlayerRank.HELPER, Arrays.asList("s", "staff", "staffchat", "schat")),
    BUILDER(CC.translate("&3(Builder Chat)"), PlayerRank.ADMIN, Arrays.asList("b", "build", "builder", "buildteam", "builderchat", "buildchat")),
    ADMIN(CC.translate("&c(Admin Chat)"), PlayerRank.ADMIN, Arrays.asList("ad", "admin", "adminchat", "adchat")),
    OWNER(CC.translate("&4(Owner Chat)"), PlayerRank.OWNER, Arrays.asList("o", "owner", "ownerchat", "ochat"));

    private final String prefix;
    private final List<String> aliases;
    private final PlayerRank requires;

    ChatChannel(String prefix, PlayerRank requires, List<String> aliases) {
        this.prefix = prefix;
        this.aliases = aliases;
        this.requires = requires;
    }

    public String getPrefix() {
        return prefix;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public PlayerRank getRequiredRank() {
        return requires;
    }

}
