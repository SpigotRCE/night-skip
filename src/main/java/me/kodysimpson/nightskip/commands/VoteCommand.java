package me.kodysimpson.nightskip.commands;

import me.kodysimpson.nightskip.managers.VoteManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VoteCommand implements CommandExecutor {

    private final VoteManager voteManager;

    public VoteCommand(VoteManager voteManager) {
        this.voteManager = voteManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can vote!");
            return true;
        }

        if (!voteManager.isVoteActive()) {
            player.sendMessage(ChatColor.RED + "There is no active vote at the moment!");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /vote <yes|no>");
        }

        String vote = args[0].toLowerCase();
        switch (vote) {
            case "yes":
                voteManager.castVote(player.getUniqueId(), true);
                break;
            case "no":
                voteManager.castVote(player.getUniqueId(), false);
                break;
            default:
                player.sendMessage(ChatColor.RED + "Usage: /vote <yes|no>");
                break;
        }

        return true;
    }
}
