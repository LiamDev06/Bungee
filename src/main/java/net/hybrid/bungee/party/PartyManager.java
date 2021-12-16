package net.hybrid.bungee.party;

import java.util.HashMap;
import java.util.UUID;

public class PartyManager {

    private final HashMap<UUID, Party> parties;

    public PartyManager() {
        parties = new HashMap<>();
    }

    public Party getParty(UUID partyLeader) {
        if (parties.containsKey(partyLeader)) {
            return this.parties.get(partyLeader);
        }

        return null;
    }

    public HashMap<UUID, Party> getParties() {
        return parties;
    }

    public Party findPartyFromMember(UUID who) {
        if (parties.containsKey(who)) {
            return parties.get(who);
        }

        for (UUID uuid : parties.keySet()) {
            Party party = parties.get(uuid);

            if (party.getMembers().contains(who)) return party;
        }

        return null;
    }

    public Party findPartyFromGuideOrLeader(UUID who) {
        if (parties.containsKey(who)) {
            return parties.get(who);
        }

        for (UUID uuid : parties.keySet()) {
            Party party = parties.get(uuid);

            if (party.getGuides().contains(who)) return party;
            if (party.getLeader() == who) return party;
        }

        return null;
    }

    public Party findPartyFromMemberOrOutgoingInvites(UUID who) {
        if (parties.containsKey(who)) {
            return parties.get(who);
        }

        for (UUID uuid : parties.keySet()) {
            Party party = parties.get(uuid);

            if (party.getMembers().contains(who)) return party;
            if (party.getOutgoingInvites().containsKey(who)) return party;
        }

        return null;
    }

}











