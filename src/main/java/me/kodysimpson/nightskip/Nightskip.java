package me.kodysimpson.nightskip;

import me.kodysimpson.nightskip.commands.VoteCommand;
import me.kodysimpson.nightskip.managers.VoteManager;
import me.kodysimpson.nightskip.tasks.TimeCheckTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class Nightskip extends JavaPlugin {

    private VoteManager voteManager;
    private BukkitTask timeCheckTask;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.voteManager = new VoteManager(this);

        getCommand("nvote").setExecutor(new VoteCommand(this.voteManager));

        this.timeCheckTask = Bukkit.getScheduler().runTaskTimer(this,
                new TimeCheckTask(this.voteManager), 0L, 20L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (this.timeCheckTask != null) {
            this.timeCheckTask.cancel();
            this.timeCheckTask = null;
        }
    }
}
