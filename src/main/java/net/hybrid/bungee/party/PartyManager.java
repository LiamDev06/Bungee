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

        return new Party(partyLeader);
    }

    public HashMap<UUID, Party> getParties() {
        return parties;
    }

    public Party findPartyFromMember(UUID who) {
        for (UUID uuid : parties.keySet()) {
            Party party = parties.get(uuid);

            if (party.getMembers().contains(who)) return party;
        }

        return null;
    }

}











