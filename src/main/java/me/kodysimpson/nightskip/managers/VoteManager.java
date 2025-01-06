package me.kodysimpson.nightskip.managers;

import me.kodysimpson.nightskip.Nightskip;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public class VoteManager {

    private final Nightskip plugin;
    private final HashMap<UUID, Boolean> votes = new HashMap<>();
    private boolean voteActive = false;
    private BukkitTask voteTask;

    public VoteManager(Nightskip plugin) {
        this.plugin = plugin;
    }

    public void startVote() {
        if (this.voteActive) return;
        votes.clear();
        voteActive = true;

        // Create the vote message with clickable components
        TextComponent message = new TextComponent(ChatColor.GOLD + "A vote has started to skip the night! ");

        // Create YES vote button
        TextComponent yesButton = new TextComponent(ChatColor.GREEN + "[YES] ");
        yesButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote yes"));
        yesButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text("Click to vote for keeping it day")));

        // Create NO vote button
        TextComponent noButton = new TextComponent(ChatColor.RED + "[NO]");
        noButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/vote no"));
        noButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new Text("Click to vote for letting night continue")));

        // Send the complete message to all players
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.spigot().sendMessage(message, yesButton, noButton);
        });

        this.voteTask = Bukkit.getScheduler().runTaskLater(plugin, this::processVoteResults, 600L);
    }

    public void castVote(UUID uuid, boolean vote) {
        if (isVoteActive()) {
            votes.put(uuid, vote);
            String voteMessage = vote ? ChatColor.GREEN + "voted to keep it day" : ChatColor.RED + "voted to let night continue";
            Bukkit.broadcast(ChatColor.YELLOW + Bukkit.getPlayer(uuid).getName() + " " + voteMessage, "nightskip.spy");

            int onlinePlayersCount = Bukkit.getOnlinePlayers().size();
            if (votes.size() >= onlinePlayersCount) {
                this.voteTask.cancel();
                processVoteResults();
            }
        }
    }

    public boolean isVoteActive() {
        return this.voteActive;
    }

    private void processVoteResults() {
        int votesForDay = 0;
        int votesForNight = 0;

        for (Boolean vote : votes.values()) {
            if (vote) {
                votesForDay++;
            } else {
                votesForNight++;
            }
        }

        boolean keepDay = votesForDay > votesForNight;
        // Create a fancy border
        String border = ChatColor.GOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";

        // Broadcast results with fancy formatting
        Bukkit.broadcastMessage(border);
        Bukkit.broadcastMessage(ChatColor.GOLD + "        Vote Results");
        Bukkit.broadcastMessage(ChatColor.GREEN + "      Yes: " + votesForDay + ChatColor.GRAY + " | " +
                ChatColor.RED + "No: " + votesForNight);

        // Show the outcome
        if (keepDay) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "    The vote passed!");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "   Setting time to day...");
            for (World world : Bukkit.getWorlds()) {
                world.setTime(1000);
            }
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "    The vote failed!");
            Bukkit.broadcastMessage(ChatColor.YELLOW + "   Night will continue...");
        }
        Bukkit.broadcastMessage(border);

        votes.clear();
        voteActive = false;
    }

}
