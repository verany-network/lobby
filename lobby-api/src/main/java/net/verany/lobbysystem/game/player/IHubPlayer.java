package net.verany.lobbysystem.game.player;

import net.verany.api.interfaces.IDefault;
import net.verany.api.locationmanager.VeranyLocation;
import net.verany.lobbysystem.game.scoreboard.IHubScoreboard;
import org.bukkit.boss.BossBar;

import java.util.UUID;

public interface IHubPlayer extends IDefault<UUID> {

    void setItems();

    void startElytra();

    void resetElytra();

    void setFirework(boolean first);

    int getJumpAndRunHighScore();

    void setJumpAndRunHighScore(int highScore);

    void addStatistics(String key);

    void setLastLocation();

    void setScoreboard();

    VeranyLocation getLastLocation();

    BossBar getBossBar();

    void setBossBar(BossBar bossBar);

    IHubScoreboard getScoreboard();

    void setScoreboard(IHubScoreboard scoreboard);

}
