package net.hybrid.bungee.party;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PartyListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();


    }

    @EventHandler
    public void onPlayerQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        PartyManager manager = BungeePlugin.getInstance().getPartyManager();

        Party party = manager.findPartyFromMemberOrOutgoingInvites(player.getUniqueId());
        if (party == null) return;

        final String who = new RankManager(player.getUniqueId()).getRank().getPrefixSpace() + player.getName();

        if (party.getLeader() == player.getUniqueId()) {
            // Disband party because the leader left

            for (UUID uuid : party.getMembers()) {
                ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(uuid);
                if (target != null) {
                    PartyUtils.partyMessage(target, "&c&lPARTY &c&lDISBANDED! &eThe &eparty &eleader &eleft &ethe &eserver &eand &etherefore &ethe &eparty &egot &edisbanded.");
                }
            }

            party.disband();
            return;
        }

        if (party.getOutgoingInvites().containsKey(player.getUniqueId())) {
            // Cancel invite because invited player left the server

            party.getOutgoingInvites().remove(player.getUniqueId());
            party.save();

            if (!party.getMembers().isEmpty()) {
                for (UUID uuid : party.getMembers()) {
                    ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(uuid);
                    if (target != null) {
                        PartyUtils.partyMessage(target, "&c&lCANCELLED! &eThe &eparty &einvite &eto " + who + " &ecancelled &ebecause &ethey &eleft &ethe &eserver.");
                    }
                }
            }

            PartyUtils.partyMessage(BungeePlugin.getInstance().getProxy().getPlayer(party.getLeader()), "&c&lCANCELLED! &eThe &eparty &einvite &eto " + who + " &ecancelled &ebecause &ethey &eleft &ethe &eserver.");
            return;
        }

        if (party.getMembers().contains(player.getUniqueId())) {
            // Announce that they left and remove from the party

            party.getMembers().remove(player.getUniqueId());
            party.getGuides().remove(player.getUniqueId());
            party.save();

            for (UUID uuid : party.getMembers()) {
                ProxiedPlayer target = BungeePlugin.getInstance().getProxy().getPlayer(uuid);
                if (target != null) {
                    PartyUtils.partyMessage(target, "&c&lLEFT &c&lPARTY! " + who + " &edisconnected from the server.");
                }
            }

            PartyUtils.partyMessage(BungeePlugin.getInstance().getProxy().getPlayer(party.getLeader()), "&c&lLEFT PARTY! " + who + " &edisconnected &efrom &ethe &eserver.");
        }
    }

    @EventHandler
    public void onServerConnect(ServerConnectEvent event) {

    }

}











