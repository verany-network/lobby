package net.verany.hubsystem.game.scoreboard;

import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.settings.Settings;
import net.verany.api.task.AbstractTask;
import net.verany.hubsystem.game.player.IHubPlayer;
import net.verany.hubsystem.game.settings.HubSetting;

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
                if (!Verany.getPlayer(player.getUniqueId()).getSettingValue(Settings.SCOREBOARD_ANIMATION)) continue;
                player.getScoreboard().setDisplayName();
            }
        }
    }
}
