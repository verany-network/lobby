package net.verany.lobbysystem.game.scoreboard;

import net.verany.api.Verany;
import net.verany.api.settings.Settings;
import net.verany.api.task.AbstractTask;
import net.verany.lobbysystem.game.player.IHubPlayer;

public class ScoreboardTask extends AbstractTask {

    private static int currentSlot = 0;

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
                    currentSlot++;
                    if (currentSlot >= AbstractHubScoreboard.DISPLAY_NAME.length)
                        currentSlot = 0;
                    player.getScoreboard().setDisplayName(currentSlot);
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
