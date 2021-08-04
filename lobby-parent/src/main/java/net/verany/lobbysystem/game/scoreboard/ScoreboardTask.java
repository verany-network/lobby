package net.verany.lobbysystem.game.scoreboard;

import net.verany.api.Verany;
import net.verany.api.settings.Settings;
import net.verany.api.task.AbstractTask;
import net.verany.lobbysystem.game.player.IHubPlayer;

public class ScoreboardTask extends AbstractTask {

    public ScoreboardTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        for (IHubPlayer player : Verany.getPlayers(IHubPlayer.class)) {
            if (player.getScoreboard() == null) continue;
            player.getScoreboard().setScores();
        }
    }

    public static class ScoreboardDisplayNameTask extends AbstractTask {

        public ScoreboardDisplayNameTask(long waitTime) {
            super(waitTime);
        }

        @Override
        public void run() {
            for (IHubPlayer player : Verany.getPlayers(IHubPlayer.class)) {
                if (player.getScoreboard() == null) continue;
                try {
                    //if (!Verany.getPlayer(player.getUniqueId()).getSettingValue(Settings.SCOREBOARD_ANIMATION)) continue;
                    player.getScoreboard().setDisplayName();
                } catch (Exception ignore) {
                }
            }
        }

    }
    public static class ScoreboardSideNameTask extends AbstractTask {

        public ScoreboardSideNameTask(long waitTime) {
            super(waitTime);
        }

        @Override
        public void run() {
            for (IHubPlayer player : Verany.getPlayers(IHubPlayer.class)) {
                if (player.getScoreboard() == null) continue;
                player.getScoreboard().addCurrentSide();
            }
        }
    }
}
