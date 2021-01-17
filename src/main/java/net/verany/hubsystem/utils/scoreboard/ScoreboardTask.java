package net.verany.hubsystem.utils.scoreboard;

import net.verany.api.task.AbstractTask;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScoreboardTask extends AbstractTask {

    private final List<Player> updateList = new CopyOnWriteArrayList<>();

    public ScoreboardTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        for (Player player : updateList) {
            HubScoreboard scoreboard = (HubScoreboard) player.getMetadata("scoreboard").get(0).value();
            scoreboard.update();
        }
    }

    public void addPlayer(Player player) {
        updateList.add(player);
    }

    public void removePlayer(Player player) {
        updateList.remove(player);
    }

}
