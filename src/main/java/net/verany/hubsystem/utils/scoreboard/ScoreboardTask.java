package net.verany.hubsystem.utils.scoreboard;

import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.task.AbstractTask;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScoreboardTask extends AbstractTask {

    public ScoreboardTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        for (IPlayerInfo player : Verany.getOnlinePlayers()) {
            if (player.getPlayer() == null || !player.getPlayer().hasMetadata("scoreboard")) continue;
            HubScoreboard scoreboard = (HubScoreboard) player.getPlayer().getMetadata("scoreboard").get(0).value();
            scoreboard.update();
        }
    }

    public static class ScoreboardDisplayNameTask extends AbstractTask {

        public ScoreboardDisplayNameTask(long waitTime) {
            super(waitTime);
        }

        @Override
        public void run() {
            for (IPlayerInfo player : Verany.getOnlinePlayers()) {
                if (player.getPlayer() == null || !player.getPlayer().hasMetadata("scoreboard")) continue;
                HubScoreboard scoreboard = (HubScoreboard) player.getPlayer().getMetadata("scoreboard").get(0).value();
                scoreboard.setDisplayName();
            }
        }
    }

}
