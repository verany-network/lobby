package net.verany.lobbysystem.game.level;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import net.verany.api.task.AbstractTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LevelTask extends AbstractTask {
    public LevelTask(long waitTime) {
        super(waitTime);
    }

    private final IPlayerManager playerManager = CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class);

    @Override
    public void run() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            int online = playerManager.getOnlineCount();
            onlinePlayer.setLevel(online);
            onlinePlayer.setExp((float) online / 512);
        }
    }
}
