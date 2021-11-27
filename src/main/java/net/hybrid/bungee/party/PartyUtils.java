package net.hybrid.bungee.party;

import net.hybrid.bungee.utility.CC;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;

public class PartyUtils {

    public static void sendHelpMenu(ProxiedPlayer player) {
        ArrayList<String> msg = new ArrayList<>();

        msg.add("&7&m----------------------------------------");
        msg.add("&d&l         Hybrid Party");
        msg.add("&b/party help &8-&7 view this help menu");
        msg.add("&b/party [player] &8-&7 invite a player to the party");
        msg.add("&b/party invite [player] &8-&7 invite a player to the party");
        msg.add("&b/party accept [player] &8-&7 accept a party invite");
        msg.add("&b/party deny [player] &8-&7 deny a party invite");
        msg.add("&b/party leave &8-&7 leave your current party");
        msg.add("&b/party warp &8-&7 warp your party to your server/world");
        msg.add("&b/party delete &8-&7 delete your party");
        msg.add("&b/party transfer [player] &8-&7 transfer the party to someone else");
        msg.add("&b/party kick [player] &8-&7 kick someone from the party");
        msg.add("&b/party chat &8-&7 toggle the party chat");
        msg.add("&b/party list &8-&7 list the players in your party");
        msg.add("&b/party promote [player] &8-&7 promote someone to party guide");
        msg.add("&b/party demote [player] &8-&7 demote someone from party guide");
        msg.add("&b/party mute [<player>/everyone] &8-&7 mute a player in your party or entire party");
        msg.add("&7&m----------------------------------------");

        for (String s : msg) {
            player.sendMessage(new TextComponent(CC.translate(s)));
        }
    }


}
