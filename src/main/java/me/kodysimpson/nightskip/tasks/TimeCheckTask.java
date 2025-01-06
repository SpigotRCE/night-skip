package me.kodysimpson.nightskip.tasks;

import me.kodysimpson.nightskip.managers.VoteManager;
import org.bukkit.Bukkit;

public class TimeCheckTask implements Runnable{

    private final VoteManager voteManager;

    public TimeCheckTask(VoteManager voteManager) {
        this.voteManager = voteManager;
    }

    @Override
    public void run() {
        for (var world : Bukkit.getWorlds()) {
            long time = world.getTime();

            if (time >= 12000L && time <= 12100L && !voteManager.isVoteActive()) {
                voteManager.startVote();
                break;
            }
        }
    }
}
