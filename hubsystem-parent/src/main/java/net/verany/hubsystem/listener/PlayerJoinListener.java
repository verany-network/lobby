package net.verany.hubsystem.listener;

import net.verany.api.Verany;
import net.verany.api.event.AbstractListener;
import net.verany.api.event.events.PlayerLoadCompleteEvent;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.game.scoreboard.HubScoreboard;
import net.verany.hubsystem.game.scoreboard.IHubScoreboard;
import net.verany.hubsystem.game.settings.HubSetting;
import net.verany.hubsystem.game.player.HubPlayer;
import net.verany.hubsystem.game.player.IHubPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class PlayerJoinListener extends AbstractListener {

    public PlayerJoinListener(VeranyProject project) {
        super(project);

        Verany.registerListener(project, PlayerLoadCompleteEvent.class, event -> {
            Player player = event.getPlayer();
            IPlayerInfo playerInfo = Verany.getPlayer(player);

            IHubPlayer hubPlayer = new HubPlayer(project);
            hubPlayer.load(player.getUniqueId());
            Verany.setPlayer(IHubPlayer.class, hubPlayer);

            player.getInventory().clear();
            hubPlayer.setItems();

            IHubScoreboard scoreboard = new HubScoreboard(player);
            scoreboard.load();
            hubPlayer.setScoreboard(scoreboard);

            /*if (playerInfo.getSettingValue(HubSetting.LAST_LOCATION_TELEPORT))
                player.teleport(hubPlayer.getLastLocation().toLocation());
            else*/
                player.teleport(HubSystem.INSTANCE.getLocationManager().getLocation("spawn"));

            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setAllowFlight(false);
            Bukkit.getScheduler().runTaskLater(HubSystem.INSTANCE, () -> player.setHealthScale(2), 2);

            BossBar bossBar = Bukkit.createBossBar(new NamespacedKey(HubSystem.INSTANCE, "bossbar_" + player.getName()), "", BarColor.BLUE, BarStyle.SEGMENTED_6);
            bossBar.addPlayer(player);
            bossBar.addFlag(BarFlag.PLAY_BOSS_MUSIC);
            hubPlayer.setBossBar(bossBar);
        });
    }
}
