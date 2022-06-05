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
        if (args.length <= 1) {
            player.sendMessage(t.t("builder_team.to_less_arguments", playerUUID));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length == 2) {
                    status = team.createTeam(playerUUID, args[1]);
                    switch (status) {
                        case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.create.only_name", playerUUID));
                        case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.create.team_could_not_be_created", playerUUID));
                        case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.team_with_name_already_exists", playerUUID));
                        case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.player_can_only_create_one_team", playerUUID));
                        case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.other_error", playerUUID));
                    }
                    break;
                }
                String description = this.getDescription(args, 2);
                status = team.createTeam(playerUUID, args[1], description);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.Create.name_desc", playerUUID, Arrays.asList(description)));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.create.team_could_not_be_created", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.team_with_name_already_exists", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.player_can_only_create_one_team", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.other_error", playerUUID));
                }
                break;
            case "edit":
                if (args.length == 2) {
                    player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.to_less_arguments", playerUUID));
                    break;
                }
                switch (args[0].toLowerCase()) {
                    case "name" -> {
                        if (args.length == 3) {
                            player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.to_less_arguments", playerUUID));
                            break;
                        }
                        status = team.editTeam(playerUUID, args[2], args[3]);
                        switch (status) {
                            case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.edit.name", playerUUID));
                            case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_manager_of_this_team", playerUUID));
                            case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_member_of_this_team", playerUUID));
                            case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.team_does_not_exist", playerUUID));
                            case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_has_no_team", playerUUID));
                            case 5 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.other_error", playerUUID));
                        }
                    }
                    case "description" -> {
                        String teamName = team.getTeam(args[2]).getString("name");
                        status = team.editTeam(playerUUID, args[2], teamName, this.getDescription(args, 2));
                        switch (status) {
                            case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.edit.description", playerUUID));
                            case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_manager_of_this_team", playerUUID));
                            case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_member_of_this_team", playerUUID));
                            case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.team_does_not_exist", playerUUID));
                            case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_has_no_team", playerUUID));
                            case 5 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.other_error", playerUUID));
                        }
                    }
                }
                break;
            case "leave":
                status = team.leaveTeam(playerUUID);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.leave_Team_Success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.User_has_no_team", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.other_error", playerUUID));
                }
                break;
            case "kick":
                String playerToKick = Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId().toString();
                status = team.kickMember(playerUUID, playerToKick);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.kick_user_success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_user_of_this_team", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_manager", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.team_does_not_exist", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.other_error", playerUUID));
                }
                break;
            case "addmanager":
                String playerToAddManager = Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId().toString();
                status = team.promoteMember(playerUUID, playerToAddManager);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.promoteMember_success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_already_manager_of_this_team", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_to_promote_is_not_in_this_team", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_to_promote_is_not_a_team", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_manager", playerUUID));
                    case 5 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.team_does_not_exist", playerUUID));
                    case 6 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.other_error", playerUUID));
                }
                break;
            case "removemanager":
                String playerToRemoveManager = Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId().toString();
                status = team.demoteMember(playerUUID, playerToRemoveManager);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.demoteMember_success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_a_manager_of_this_team", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_to_demoteMember_is_not_in_this_team", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_to_demoteMember_is_not_a_team", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_manager", playerUUID));
                    case 5 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.team_does_not_exist", playerUUID));
                    case 6 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.other_error", playerUUID));
                }
                break;
            case "add":
                String playerToAdd = Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId().toString();
                status = team.addMember(playerUUID, playerToAdd);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.add_success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_user_of_this_team", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_manager", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.team_does_not_exist", playerUUID));
                    case 4 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.other_error", playerUUID));
                }
                break;
            case "invite":
                String receiverUUID = Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId().toString();
                if (Bukkit.getOfflinePlayer(receiverUUID) == null) {
                    player.sendMessage(ChatColor.RED.toString() + args[2] + t.t("builder_team.is_not_a_player", playerUUID));
                    break;
                }
                status = team.inviteMember(playerUUID, receiverUUID);
                switch (status) {
                    case 0 -> player.sendMessage(ChatColor.GREEN.toString() + t.t("builder_team.invite_success", playerUUID));
                    case 1 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_already_manager_of_this_team", playerUUID));
                    case 2 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.user_is_not_manager", playerUUID));
                    case 3 -> player.sendMessage(ChatColor.RED.toString() + t.t("builder_team.team_does_not_exist", playerUUID));
                }
                Player receiver = Bukkit.getPlayer(args[1]);
                JSONObject getTeam = team.getTeamNameByMember(playerUUID);
                String teamname = getTeam.getString("name");
                player.sendMessage(ChatColor.AQUA + teamname + " " + "builder_team.invite.sender" + " " + ChatColor.GREEN + receiver.getName());
                receiver.sendMessage(ChatColor.AQUA + "builder_team.invite.receiver" + " " + ChatColor.GREEN + teamname);
                break;
            case "accept":
                team.acceptRequest(playerUUID, args[1]);
                break;
            case "deny":
                team.denyRequest(playerUUID, args[1]);
                break;
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
