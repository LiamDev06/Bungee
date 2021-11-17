package net.hybrid.bungee.party;

import net.hybrid.bungee.BungeePlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Party {

    // The leader, is by default the one creating but can also be transferred to
    private UUID leader;

    // The ones in the party
    private final List<UUID> members;
    private final List<UUID> moderators;
    private final List<UUID> admins;

    // Invites            Who   Sent
    private final HashMap<UUID, Long> outgoingInvites;

    public Party(UUID leader) {
        this.leader = leader;

        this.members = new ArrayList<>();
        this.moderators = new ArrayList<>();
        this.admins = new ArrayList<>();

        this.outgoingInvites = new HashMap<>();
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public List<UUID> getAdmins() {
        return admins;
    }

    public List<UUID> getModerators() {
        return moderators;
    }

    public HashMap<UUID, Long> getOutgoingInvites() {
        return outgoingInvites;
    }

    public Party addOutgoingInvite(UUID to) {
        if (!outgoingInvites.containsKey(to)) {
            outgoingInvites.put(to, System.currentTimeMillis());
        }

        return this;
    }

    public Party removeOutgoingInvite(UUID from) {
        outgoingInvites.remove(from);
        return this;
    }

    public Party addMember(UUID who) {
        if (!members.contains(who)) {
            members.add(who);
        }

        return this;
    }

    public Party removeMember(UUID who) {
        members.remove(who);
        return this;
    }

    public Party addAdmin(UUID who) {
        if (!admins.contains(who)) {
            admins.add(who);
        }

        return this;
    }

    public Party removeAdmin(UUID who) {
        admins.remove(who);

        return this;
    }

    public Party addModerator(UUID who) {
        if (!moderators.contains(who)) {
            moderators.add(who);
        }

        return this;
    }

    public Party removeModerator(UUID who) {
        moderators.remove(who);
        return this;
    }

    public boolean inviteHasExpired(UUID to) {
        if (!outgoingInvites.containsKey(to)) return true;
        return outgoingInvites.get(to) > System.currentTimeMillis();
    }

    public void save() {
        HashMap<UUID, Party> parties = BungeePlugin.getInstance().getPartyManager().getParties();

        if (parties.containsKey(leader)) {
            parties.replace(leader, this);
            return;
        }

        parties.put(leader, this);
    }

}











