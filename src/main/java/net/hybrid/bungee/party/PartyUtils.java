package net.hybrid.bungee.party;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.UUID;

public class PartyUtils {

    public static void sendHelpMenu(ProxiedPlayer player) {
        ArrayList<String> msg = new ArrayList<>();

        msg.add("&9&m----------------------------------------");
        msg.add("&d&l         Hybrid Party");
        msg.add("&b/party help &8-&7 view &7this &7help &7menu");
        msg.add("&b/party [player] &8-&7 invite a &7player to &7the &7party");
        msg.add("&b/party invite [player] &8-&7 invite a &7player to &7the &7party");
        msg.add("&b/party accept [player] &8-&7 accept a &7party invite");
        msg.add("&b/party deny [player] &8-&7 deny a &7party invite");
        msg.add("&b/party leave &8-&7 leave your &7current &7party");
        msg.add("&b/party create &8-&7 create &7a party &7without &7inviting");
        msg.add("&b/party warp &8-&7 warp your &7party to &7your &7server/&7world");
        msg.add("&b/party delete &8-&7 delete &7your party");
        msg.add("&b/party transfer [player] &8-&7 transfer &7the &7party to &7someone &7else");
        msg.add("&b/party kick [player] &8-&7 kick &7someone from &7the party");
        msg.add("&b/party chat &8-&7 toggle the &7party chat");
        msg.add("&b/party list &8-&7 list &7the players &7in your &7party");
        msg.add("&b/party promote [player] &8-&7 promote &7someone to &7party &7guide");
        msg.add("&b/party demote [player] &8-&7 demote &7someone &7from party &7guide");
        msg.add("&b/party mute [<player>/everyone] &8-&7 mute a player &7in your &7party &7or &7entire &7party");
        msg.add("&9&m----------------------------------------");

        for (String s : msg) {
            player.sendMessage(new TextComponent(CC.translate(s)));
        }
    }

    public static boolean canInvite(ProxiedPlayer player, RankManager manager) {
        return true;
    }

    public static void sendInviteMessage(ProxiedPlayer target, RankManager from, ProxiedPlayer fromPlayer) {
        target.sendMessage(new TextComponent(CC.translate("&9&m--------------------------------")));
        TextComponent accept = new TextComponent("      [ACCEPT]     ");
        accept.setColor(ChatColor.GREEN);
        accept.setBold(true);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party accept " + fromPlayer.getName()));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click here to accept").italic(true).color(ChatColor.GRAY).create()));

        TextComponent deny = new TextComponent("[DENY]");
        deny.setColor(ChatColor.RED);
        deny.setBold(true);
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/party deny " + fromPlayer.getName()));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Click here to deny").italic(true).color(ChatColor.GRAY).create()));
        accept.addExtra(deny);

        target.sendMessage(new TextComponent(CC.translate(
                "&eYou &ereceived &ea &eparty &einvite &efrom " + from.getRank().getPrefixSpace() + fromPlayer.getName() + "&e!"
        )));
        target.sendMessage(accept);
        target.sendMessage(new TextComponent(CC.translate("&9&m--------------------------------")));
    }


    public static void sendInviteExpired(UUID uuid, Party party) {
        if (!BungeePlugin.getInstance().getPartyManager().getParties().containsValue(party)) {
            return;
        }

        party.getOutgoingInvites().remove(uuid);

        ProxyServer proxy = BungeePlugin.getInstance().getProxy();
        ProxiedPlayer player = proxy.getPlayer(uuid);
        ProxiedPlayer leader = proxy.getPlayer(party.getLeader());
        RankManager leaderRank = new RankManager(leader.getUniqueId());
        RankManager playerRank = new RankManager(player.getUniqueId());

        partyMessage(player, "&c&lEXPIRED! &eThe &eparty &einvite &eto " + leaderRank.getRank().getPrefixSpace() + leader.getName() + "&e's &eparty &eexpired!");

        for (UUID member : party.getMembers()) {
            ProxiedPlayer memberPlayer = BungeePlugin.getInstance().getProxy().getPlayer(member);
            partyMessage(memberPlayer, "&c&lEXPIRED! &eThe &eparty &einvite &eto " + playerRank.getRank().getPrefixSpace() + player.getName() + " &eexpired!");
        }

        partyMessage(leader, "&c&lEXPIRED! &eThe &eparty &einvite &eto " + playerRank.getRank().getPrefixSpace() + player.getName() + " &eexpired!");
    }

    public static void partyMessage(ProxiedPlayer player, String... messages) {
        player.sendMessage(new TextComponent(CC.translate("&9&m---------------&9&m-----------------")));
        for (String s : messages) {
            player.sendMessage(new TextComponent(CC.translate(s)));
        }
        player.sendMessage(new TextComponent(CC.translate("&9&m----------------&9&m----------------")));
    }

    public static boolean isInParty(ProxiedPlayer player) {
        PartyManager partyManager = BungeePlugin.getInstance().getPartyManager();

        Party party = partyManager.getParty(player.getUniqueId());
        if (party == null) {
            party = partyManager.findPartyFromMember(player.getUniqueId());

            return party != null;
        }

        return true;
    }

    public static Party getPartyIgnoreRole(ProxiedPlayer player) {
        PartyManager partyManager = BungeePlugin.getInstance().getPartyManager();
        Party party = partyManager.getParty(player.getUniqueId());

        if (party == null) {
            party = partyManager.findPartyFromMember(player.getUniqueId());
        }

        return party;
    }
}











