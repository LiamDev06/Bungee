package net.hybrid.bungee.party;

import net.hybrid.bungee.BungeePlugin;
import net.hybrid.bungee.utility.CC;
import net.hybrid.bungee.utility.RankManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class PartyCommand extends Command {

    public PartyCommand() {
        super("party", "", "p", "serverparty");
    }

    private ProxiedPlayer player;

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        this.player = player;

        RankManager rank = new RankManager(player.getUniqueId());
        ProxyServer proxy = BungeePlugin.getInstance().getProxy();
        PartyManager partyManager = BungeePlugin.getInstance().getPartyManager();

        if (args.length == 0) {
            PartyUtils.sendHelpMenu(player);
            return;
        }

        if (args[0].equalsIgnoreCase("help")) {
            PartyUtils.sendHelpMenu(player);
            return;
        }

        if (args[0].equalsIgnoreCase("invite")) {
            return;
        }

        if (args[0].equalsIgnoreCase("accept")) {
            if (args.length > 1) {
                ProxiedPlayer target = proxy.getPlayer(args[1]);
                if (target == null) {
                    partyMessage("&cThis player &cis not &conline!");
                    return;
                }

                if (target.getUniqueId() == player.getUniqueId()) {
                    partyMessage("&cYou cannot &caccept an &cinvite from &cyourself!");
                    return;
                }

                Party party = partyManager.findPartyFromGuideOrLeader(target.getUniqueId());

                if (party == null) {
                    partyMessage("&c&lNO PARTY! &cThis &cparty &cdoes &cnot &cexist! &cMaybe &cit &cwas &cdisbanded?");
                    return;
                }

                if (!party.getOutgoingInvites().containsKey(player.getUniqueId())) {
                    partyMessage("&c&lNO INVITE! &cYou &cdo &cnot &chave &ca &cpending &cinvite &cto &cthis &cparty. &cMaybe &cit &cexpired?");
                    return;
                }

                final String leaderWho = new RankManager(party.getLeader()).getRank().getPrefixSpace() + proxy.getPlayer(party.getLeader());
                final String whoInvited = rank.getRank().getPrefixSpace() + player.getName();

                if (!party.getMembers().isEmpty()) {
                    for (UUID uuid : party.getMembers()) {

                        ProxiedPlayer targetLoop = proxy.getPlayer(uuid);
                        if (targetLoop != null) {
                            partyMessage(targetLoop, "&a&lPARTY JOIN! " + whoInvited + " &ejoined &ethe &eparty!");
                        }
                    }
                }

                partyMessage(proxy.getPlayer(party.getLeader()), "&a&lPARTY JOIN! " + whoInvited + " &ejoined &ethe &eparty!");

                party.getOutgoingInvites().remove(player.getUniqueId());
                party.getMembers().remove(player.getUniqueId());
                party.getMembers().add(player.getUniqueId());
                party.save();

                partyMessage(player, "&a&lINVITE ACCEPTED! &eYou &ejoined " + leaderWho + "&e's &eparty!");
                return;
            }

            partyMessage("&c&lMISSING &c&lARGUMENTS! &cInclude a &cplayer with &6/party &6accept &6<player>&c!");
            return;
        }

        if (args[0].equalsIgnoreCase("deny")) {
            return;
        }

        if (args[0].equalsIgnoreCase("leave")) {
            return;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (PartyUtils.isInParty(player)) {
                partyMessage("&cYou &care &calready &cin &ca &cparty!");
                return;
            }

            new Party(player.getUniqueId()).save();
            partyMessage("&a&lPARTY CREATED! &eYou &ecreated &ea &enew &eparty! &eInvite &eplayers &ewith &6/p &6<player>&e!");

            return;
        }

        if (args[0].equalsIgnoreCase("warp")) {
            return;
        }

        if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("disband")) {
            return;
        }

        if (args[0].equalsIgnoreCase("transfer")) {
            Party party = PartyUtils.getPartyIgnoreRole(player);
            if (party == null) {
                partyMessage("&cYou &care &cnot &cin &ca &cparty!");
                return;
            }

            if (party.getLeader() != player.getUniqueId()) {
                partyMessage("&c&lBLOCKED! &cOnly &cthe &cparty &cleader &ccan &cdo &cthis!");
                return;
            }

            if (args.length > 1) {
                ProxiedPlayer target = proxy.getPlayer(args[1]);

                if (target == null) {
                    partyMessage("&cThis player &cis not &conline!");
                    return;
                }

                if (target.getUniqueId() == player.getUniqueId()) {
                    partyMessage("&cYou cannot &ctransfer to &cyourself!");
                    return;
                }

                party.setLeader(target.getUniqueId());
                party.removeGuide(target.getUniqueId());
                party.removeMember(target.getUniqueId());

                party.addMember(player.getUniqueId());
                party.addGuide(player.getUniqueId());

                partyMessage("no energy for fancy message this works - will update later");
                return;
            }

            partyMessage("&c&lMISSING &c&lARGUMENTS! &cInclude a &cplayer with &6/party &6transfer &6<player>&c!");
            return;
        }

        if (args[0].equalsIgnoreCase("kick")) {
            return;
        }

        if (args[0].equalsIgnoreCase("chat")) {
            player.chat("/chat party");
            return;
        }

        if (args[0].equalsIgnoreCase("list")) {
            Party party = PartyUtils.getPartyIgnoreRole(player);
            if (party == null) {
                partyMessage("&cYou &care &cnot &cin &ca &cparty!");
                return;
            }

            ArrayList<String> messages = new ArrayList<>();
            messages.add(CC.translate("           &e&lParty &e&lList"));
            messages.add(CC.translate("&6➤ &6&lParty Leader: " + new RankManager(party.getLeader()).getRank().getPrefixSpace() + idPlayer(party.getLeader()).getName()));

            if (party.getGuides().isEmpty()) {
                messages.add(CC.translate("&6➤ Party Guides &f(0)&6: &7None"));
            } else {
                StringBuilder guides = new StringBuilder();
                for (UUID uuid : party.getGuides()) {
                    ProxiedPlayer target = idPlayer(uuid);
                    RankManager targetRank = new RankManager(target.getUniqueId());
                    guides.append(targetRank.getRank().getPrefixSpace()).append(target.getName()).append(CC.translate("&7, "));
                }

                messages.add(CC.translate("&6➤ Party Guides &f(" + party.getGuides().size() + ")&6: " + guides.substring(0, guides.length() - 2).trim()));
            }

            if (party.getMembers().isEmpty() || party.getMembers().size() == party.getGuides().size()) {
                messages.add(CC.translate("&6➤ Party Members &f(0)&6: &7None"));
            } else {
                StringBuilder members = new StringBuilder();
                for (UUID uuid : party.getMembers()) {
                    if (!party.getGuides().contains(uuid)) {
                        ProxiedPlayer target = idPlayer(uuid);
                        RankManager targetRank = new RankManager(target.getUniqueId());
                        members.append(targetRank.getRank().getPrefixSpace()).append(target.getName()).append(CC.translate("&7, "));
                    }
                }

                messages.add(CC.translate("&6➤ Party Members &f(" + (party.getMembers().size() - party.getGuides().size()) + ")&6: " + members.substring(0, members.length() - 2).trim()));
            }

            partyMessage(messages);
            return;
        }

        if (args[0].equalsIgnoreCase("mute")) {
            return;
        }

        if (args[0].equalsIgnoreCase("promote")) {
            return;
        }

        if (args[0].equalsIgnoreCase("demote")) {
            return;
        }

        // -----------------------------------------------------
        // Admin Commands

        if (args[0].equalsIgnoreCase("hijack")) {
            if (!rank.isAdmin()){
                partyMessage("&cYou are &cnot allowed &cto do this!");
                return;
            }

            if (args.length > 1) {
                ProxiedPlayer target = proxy.getPlayer(args[1]);

                if (target == null) {
                    partyMessage("&cThis player &cis not &conline!");
                    return;
                }

                if (target.getUniqueId() == player.getUniqueId()) {
                    partyMessage("&cYou cannot &chijack yourself!");
                    return;
                }

                Party party = partyManager.getParty(target.getUniqueId());
                if (party == null) {
                    party = partyManager.findPartyFromMember(target.getUniqueId());

                    if (party == null) {
                        partyMessage("&c&lNOT FOUND! &cNo &cparty &cwas &cfound &cwith '&6" + target.getName() + "&c' &cin &cit!");
                        return;
                    }
                }

                if (party.getLeader() == player.getUniqueId()) {
                    partyMessage("&cYou &care &calready &cthe &cparty &cleader &cof &cthat &cparty!");
                    return;
                }

                RankManager leaderRankManager = new RankManager(party.getLeader());
                String leaderWho = leaderRankManager.getRank().getPrefixSpace() + proxy.getPlayer(party.getLeader());
                String thisWho = rank.getRank().getPrefixSpace() + player.getName();

                StringBuilder builderLeaderWho = new StringBuilder();
                for (String s : leaderWho.split(" ")) {
                    builderLeaderWho.append(leaderRankManager.getRank().getColor()).append(s).append(" ");
                }

                StringBuilder builderThisWho = new StringBuilder();
                for (String s : thisWho.split(" ")) {
                    builderThisWho.append(rank.getRank().getColor()).append(s).append(" ");
                }

                leaderWho = builderLeaderWho.toString().trim();
                thisWho = builderThisWho.toString().trim();

                boolean isSilent = false;

                try {
                    if (args[2].equalsIgnoreCase("-s")) {
                        isSilent = true;
                    }
                } catch (Exception ignored) {}

                if (!party.getMembers().isEmpty() && !isSilent) {
                    for (UUID uuid : party.getMembers()) {

                        ProxiedPlayer targetLoop = proxy.getPlayer(uuid);
                        if (targetLoop != null && uuid != player.getUniqueId()) {
                            partyMessage(targetLoop, "&c&l⚠ &c&lPARTY &c&lHIJACKED &c&l⚠ &eThe &eparty &ewas &ehijacked &eby " + thisWho + "&e!");
                        }
                    }
                }

                if (!isSilent) {
                    partyMessage(proxy.getPlayer(party.getLeader()), "&c&l⚠ &c&lPARTY &c&lHIJACKED &c&l⚠ &eThe &eparty &ewas &ehijacked &eby " + thisWho + "&e!");
                }

                partyMessage("&a&lHIJACK INITIATED! &aYou &asuccessfully &ahijacked " + leaderWho + "&a's &aparty! &a&o*evil &a&olaugh*");

                party.addMember(party.getLeader());
                party.addGuide(party.getLeader());

                party.removeMember(player.getUniqueId());
                party.removeGuide(player.getUniqueId());
                party.setLeader(player.getUniqueId());
                party.save();
                return;
            }

            partyMessage("&c&lMISSING &c&lARGUMENTS! &cInclude a &cplayer with &6/party &6hijack &6<player>&c!");
            return;
        }

        if (args[0].equalsIgnoreCase("yoink")) {
            if (!rank.isAdmin()){
                partyMessage("&cYou are not &callowed to &cdo this!");
                return;
            }

            if (args.length > 1) {
                ProxiedPlayer target = proxy.getPlayer(args[1]);

                if (target == null) {
                    partyMessage("&cThis player &cis not &conline!");
                    return;
                }

                if (target.getUniqueId() == player.getUniqueId()) {
                    partyMessage("&cYou cannot &cyoink yourself!");
                    return;
                }

                Party party = partyManager.getParty(player.getUniqueId());
                if (party == null) {
                    party = partyManager.findPartyFromMember(player.getUniqueId());

                    if (party == null) {
                        partyMessage("&c&lNO PARTY! &cYou &cmust &cbe &cin &ca &cparty &cto &cdo &cthis!");
                        return;
                    }
                }

                if (target.getUniqueId() == player.getUniqueId()) {
                    partyMessage("&cYou &ccannot &cyoink &cyourself!");
                    return;
                }

                if (party.getMembers().contains(target.getUniqueId())) {
                    partyMessage("&cThis &cplayer &cis &calready &cin &cthe &cparty!");
                    return;
                }

                RankManager targetRankManager = new RankManager(target.getUniqueId());
                RankManager leaderRankManager = new RankManager(party.getLeader());

                String thisWho = rank.getRank().getPrefixSpace() + player.getName();
                String targetWho = targetRankManager.getRank().getPrefixSpace() + proxy.getPlayer(target.getUniqueId()).getName();
                String leaderWho = leaderRankManager.getRank().getPrefixSpace() + proxy.getPlayer(party.getLeader()).getName();

                StringBuilder builderThisWho = new StringBuilder();
                for (String s : thisWho.split(" ")) {
                    builderThisWho.append(rank.getRank().getColor()).append(s).append(" ");
                }

                StringBuilder builderTargetWho = new StringBuilder();
                for (String s : targetWho.split(" ")) {
                    builderTargetWho.append(targetRankManager.getRank().getColor()).append(s).append(" ");
                }

                StringBuilder builderLeaderWho = new StringBuilder();
                for (String s : leaderWho.split(" ")) {
                    builderLeaderWho.append(leaderRankManager.getRank().getColor()).append(s).append(" ");
                }

                thisWho = builderThisWho.toString().trim();
                targetWho = builderTargetWho.toString().trim();
                leaderWho = builderLeaderWho.toString().trim();

                boolean isSilent = false;

                try {
                    if (args[2].equalsIgnoreCase("-s")) {
                        isSilent = true;
                    }
                } catch (Exception ignored) {}

                if (!party.getMembers().isEmpty() && !isSilent) {
                    for (UUID uuid : party.getMembers()) {

                        ProxiedPlayer targetLoop = proxy.getPlayer(uuid);
                        if (targetLoop != null && player.getUniqueId() != uuid) {
                            partyMessage(targetLoop, thisWho + " &eyoinked " + targetWho + " &einto &ethe &eparty!");
                        }
                    }
                }

                if (party.getLeader() != player.getUniqueId() && !isSilent) {
                    partyMessage(proxy.getPlayer(party.getLeader()), thisWho + " &eyoinked " + targetWho + " &einto &ethe &eparty!");
                }

                partyMessage("&a&lYOINKED MEMBER! &eYou &eyoinked " + targetWho + " &einto &ethe &eparty!");
                partyMessage(proxy.getPlayer(target.getUniqueId()), "&eYou &ewere &eyoinked &einto " + leaderWho + "&e's &eparty &eby " + thisWho + "&e!");

                party.addMember(target.getUniqueId());
                party.getOutgoingInvites().remove(target.getUniqueId());
                party.save();
                return;
            }

            partyMessage("&c&lMISSING &c&lARGUMENTS! &cInclude a &cplayer with &6/party &6yoink &6<player>&c!");
            return;
        }

        if (args[0].equalsIgnoreCase("forcejoin")) {
            if (!rank.isAdmin()){
                partyMessage("&cYou are not &callowed to &cdo this!");
                return;
            }


            return;
        }

        // -----------------------------------------------------
        // Invite Command via /party <player>

        ProxiedPlayer target = proxy.getPlayer(args[0]);
        if (target == null) {
            partyMessage("&cThis player &cis not &conline!");
            return;
        }

        if (target.getUniqueId() == player.getUniqueId()) {
            partyMessage("&cYou cannot &cinvite yourself!");
            return;
        }

        RankManager targetRank = new RankManager(target.getUniqueId());
        if (!PartyUtils.canInvite(target, targetRank) && !rank.isStaff()) {
            partyMessage("&cThis player &ccannot be &cinvited to &ca party!");
            return;
        }

        Party party = partyManager.findPartyFromMember(player.getUniqueId());
        if (party == null) {
            party = new Party(player.getUniqueId()).save();

        } else if (party.getLeader() != player.getUniqueId() && !party.getGuides().contains(player.getUniqueId())) {
            partyMessage("&c&lDENIED! &cOnly &cthe party &cleader or a &cparty guide can &cinvite someone &cto the party!");
            return;
        }

        if (party.getOutgoingInvites().containsKey(target.getUniqueId())) {
            partyMessage("&cYou &calready &chave a &cpending &cinvite &cto this &cplayer.", "&cWait for &cthem &cto &caccept &cit!");
            return;
        }

        if (party.getMembers().contains(target.getUniqueId()) || party.getLeader() == target.getUniqueId()) {
            //TODO: send already in party message
            return;
        }

        partyMessage("&a&lINVITED! &eYou &einvited " + targetRank.getRank().getPrefixSpace() + target.getName() + " &eto &ethe &eparty! &eWait &efor &ethem &eto &eaccept &ethe &einvite.");
        PartyUtils.sendInviteMessage(target, rank, player);
        party.addOutgoingInvite(target.getUniqueId()).save();
    }

    private void partyMessage(String... messages) {
        player.sendMessage(new TextComponent(CC.translate("&9&m-------------&9&m-------------------")));
        for (String s : messages) {
            player.sendMessage(new TextComponent(CC.translate(s)));
        }
        player.sendMessage(new TextComponent(CC.translate("&9&m---------------&9&m-----------------")));
    }

    private void partyMessage(ArrayList<String> messages) {
        player.sendMessage(new TextComponent(CC.translate("&9&m-------------&9&m-------------------")));
        for (String s : messages) {
            player.sendMessage(new TextComponent(CC.translate(s)));
        }
        player.sendMessage(new TextComponent(CC.translate("&9&m---------------&9&m-----------------")));
    }

    private void partyMessage(ProxiedPlayer player, String... messages) {
        player.sendMessage(new TextComponent(CC.translate("&9&m---------------&9&m-----------------")));
        for (String s : messages) {
            player.sendMessage(new TextComponent(CC.translate(s)));
        }
        player.sendMessage(new TextComponent(CC.translate("&9&m-------------&9&m-------------------")));
    }

    private void message(String message) {
        player.sendMessage(new TextComponent(CC.translate(message)));
    }

    public ProxiedPlayer idPlayer(UUID uuid) {
        return BungeePlugin.getInstance().getProxy().getPlayer(uuid);
    }

}












