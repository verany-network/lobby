package net.verany.hubsystem.game.level;

import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import net.verany.api.task.AbstractTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LevelTask extends AbstractTask {
    public LevelTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int online = BridgePlayerManager.getInstance().getOnlineCount();
            onlinePlayer.setLevel(online);
            onlinePlayer.setExp((float) online / 512);
        }
    }
}
