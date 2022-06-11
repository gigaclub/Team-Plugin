package net.gigaclub.team.commands;

import net.gigaclub.team.Main;
import net.gigaclub.teamapi.Team;
import net.gigaclub.translation.Translation;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class TeamCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        String playerUUID = player.getUniqueId().toString();
        Translation t = Main.getTranslation();
        Team team = Main.getTeam();
        int status;
        if(!(sender instanceof Player)) {
            Bukkit.getLogger().info("You´r not a Player  XD");
            return true;
        }
        if (args.length < 1) {
            player.sendMessage(t.t("team.command.to_less_arguments", playerUUID));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length <= 2) {
                    status = team.createTeam(playerUUID, args[1]);
                    switch (status) {
                        case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("team.command.create.success", playerUUID));
                        case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.create.team_could_not_be_created", playerUUID));
                        case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.create.team_with_this_name_already_exists", playerUUID));
                        case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.create.user_has_no_access_to_create_a_team", playerUUID));
                        case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.create.other_error", playerUUID));
                    }
                    break;
                }
                String description = this.getDescription(args, 2);
                status = team.createTeam(playerUUID, args[1], description);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("team.command.create.success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.create.team_could_not_be_created", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.create.team_with_this_name_already_exists", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.create.user_has_no_access_to_create_a_team", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.create.other_error", playerUUID));
                }
                break;
            case "edit":
                if (args.length == 2) {
                    player.sendMessage(ChatColor.RED.toString() + t.t("team.command.to_less_arguments", playerUUID));
                    break;
                }
                switch (args[1].toLowerCase()) {
                    case "name" -> {
                        if (args.length == 3) {
                            player.sendMessage(ChatColor.RED.toString() + t.t("team.command.to_less_arguments", playerUUID));
                            break;
                        }
                        status = team.editTeam(playerUUID, Integer.parseInt(args[2]), args[3]);
                        switch (status) {
                            case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("team.command.edit.success", playerUUID));
                            case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.edit.no_valid_team_found_for_this_user", playerUUID));
                            case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.edit.other_error", playerUUID));
                        }
                    }
                    case "description" -> {
                        status = team.editTeam(playerUUID, Integer.parseInt(args[2]), "", this.getDescription(args, 3));
                        switch (status) {
                            case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("team.command.edit.success", playerUUID));
                            case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.edit.no_valid_team_found_for_this_user", playerUUID));
                            case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.edit.other_error", playerUUID));
                        }
                    }
                }
                break;
            case "leave":
                status = team.leaveTeam(playerUUID, Integer.parseInt(args[1]));
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("team.command.leave.success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.leave.user_has_no_team", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.leave.user_has_no_permission_to_leave_teams", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.leave.other_error", playerUUID));
                }
                break;
            case "kick":
                int teamId = Integer.parseInt(args[1]);
                String playerToKick = Objects.requireNonNull(Bukkit.getPlayer(args[2])).getUniqueId().toString();
                status = team.kickMember(playerUUID, teamId, playerToKick);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("team.command.kick.success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.kick.user_is_not_member_of_team", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.kick.not_allowed_to_kick_owner", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.kick.user_to_kick_does_not_exist", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.kick.no_valid_team_found_for_this_user", playerUUID));
                    case 5 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.kick.other_error", playerUUID));
                }
                break;
            case "invite":
                teamId = Integer.parseInt(args[1]);
                Player receiver = Bukkit.getPlayer(args[2]);
                if (receiver == null) {
                    player.sendMessage(ChatColor.RED.toString() + args[2] + t.t("team.command.invite.is_not_a_player", playerUUID));
                    break;
                }
                String receiverUUID = receiver.getUniqueId().toString();
                status = team.inviteMember(playerUUID, teamId, receiverUUID);
                switch (status) {
                    case 0 -> {
                        player.sendMessage(ChatColor.GREEN.toString() + t.t("team.command.invite.success", playerUUID));
                        JSONObject getTeam = team.getTeam(teamId);
                        String teamname = getTeam.getString("name");
                        player.sendMessage(ChatColor.AQUA + teamname + " " + "team.command.invite.sender" + " " + ChatColor.GREEN + receiver.getName());
                        receiver.sendMessage(ChatColor.AQUA + "team.command.invite.receiver" + " " + ChatColor.GREEN + teamname);
                    }
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.invite.request_already_sent", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.invite.user_is_already_member_of_this_team", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.invite.user_to_invite_not_found", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.invite.no_valid_team_found_for_the_user", playerUUID));
                    case 5 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.invite.other_error", playerUUID));
                }
                break;
            case "accept":
                teamId = Integer.parseInt(args[1]);
                status = team.acceptRequest(playerUUID, teamId);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("team.command.accept.success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.accept.request_does_not_exist", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.accept.team_does_not_exist", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.accept.user_has_no_permission_to_accept_requests", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.accept.other_error", playerUUID));
                }
                break;
            case "deny":
                teamId = Integer.parseInt(args[1]);
                status = team.denyRequest(playerUUID, teamId);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("team.command.deny.success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.deny.request_does_not_exist", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.deny.team_does_not_exist", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.deny.user_has_no_permission_to_reject_requests", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("team.command.deny.other_error", playerUUID));
                }
                break;
            case "list":
                JSONArray teamList = team.getTeamsByMember(playerUUID);
                for (int i = 0; i < teamList.length(); i++) {
                    JSONObject teamObject = teamList.getJSONObject(i);
                    teamId = teamObject.getInt("id");
                    String teamName = teamObject.getString("name");
                    String teamDescription = teamObject.getString("description");
                    String teamOwner = teamObject.getString("owner_id");
                    JSONArray teamMembers = teamObject.getJSONArray("user_ids");
                    // rework after translation rework
                    sender.sendMessage(t.t("team.command.list.id", playerUUID) + ": " + teamId);
                    sender.sendMessage(t.t("team.command.list.name", playerUUID) + ": " + teamName);
                    sender.sendMessage(t.t("team.command.list.description", playerUUID) + ": " + teamDescription);
                    sender.sendMessage(t.t("team.command.list.owner", playerUUID) + ": " + teamOwner);
                    for (int j = 0; j < teamMembers.length(); j++) {
                        JSONObject teamMember = teamMembers.getJSONObject(j);
                        String teamMemberId = teamMember.getString("mc_uuid");
                        sender.sendMessage(t.t("team.command.list.user", playerUUID) + ": " + teamMemberId);
                    }
                    sender.sendMessage("");
                }
        }
        return true;
    }

    private String getDescription(String[] args, int at) {
        StringBuilder res = new StringBuilder();
        for (int i = at; i < args.length; i++) {
            res.append(args[i]).append(" ");
        }
        return res.toString();
    }

}
