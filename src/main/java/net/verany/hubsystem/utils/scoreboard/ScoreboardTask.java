package net.verany.hubsystem.utils.scoreboard;

import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.setting.Settings;
import net.verany.api.task.AbstractTask;
import net.verany.hubsystem.utils.inventories.HubSwitcherInventory;
import net.verany.hubsystem.utils.inventories.TeleporterInventory;
import net.verany.hubsystem.utils.settings.HubSetting;
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
            if (player.getPlayer().hasMetadata("hub_switcher")) {
                HubSwitcherInventory inventory = (HubSwitcherInventory) player.getPlayer().getMetadata("hub_switcher").get(0).value();
                inventory.setItems();
                continue;
            }
            if (player.getPlayer().hasMetadata("teleporter")) {
                TeleporterInventory inventory = (TeleporterInventory) player.getPlayer().getMetadata("teleporter").get(0).value();
                inventory.setItems();
                continue;
            }
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
                if (!player.getSettingValue(Settings.SCOREBOARD_ANIMATION)) continue;
                HubScoreboard scoreboard = (HubScoreboard) player.getPlayer().getMetadata("scoreboard").get(0).value();
                scoreboard.setDisplayName();
            }
        }
    }

}
