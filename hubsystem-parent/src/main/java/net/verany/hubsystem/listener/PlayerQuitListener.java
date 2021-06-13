package net.verany.hubsystem.listener;

import net.verany.api.Verany;
import net.verany.api.event.AbstractListener;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.game.bossbar.BossBarTask;
import net.verany.hubsystem.game.player.IHubPlayer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarFlag;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class PlayerQuitListener extends AbstractListener {

    public PlayerQuitListener(VeranyProject project) {
        super(project);

        Verany.registerListener(project, PlayerQuitEvent.class, event -> {
            Player player = event.getPlayer();
            IPlayerInfo playerInfo = Verany.getPlayer(player);

            IHubPlayer hubPlayer = playerInfo.getPlayer(IHubPlayer.class);
            hubPlayer.setLastLocation();
            hubPlayer.getBossBar().setTitle("");
            hubPlayer.getBossBar().removePlayer(player);
            hubPlayer.setBossBar(null);
            hubPlayer.update();
            playerInfo.setTempSetting(BossBarTask.BossBarSetting.CURRENT_TEXT, 0);
            playerInfo.setTempSetting(BossBarTask.BossBarSetting.CURRENT_TEXT_CHARACTER, 0);
            playerInfo.setTempSetting(BossBarTask.BossBarSetting.CURRENT_MESSAGE, "");
            playerInfo.setTempSetting(BossBarTask.BossBarSetting.LAST_COLOR, new StringBuilder());
            playerInfo.setTempSetting(BossBarTask.BossBarSetting.BACK, false);
            playerInfo.setTempSetting(BossBarTask.BossBarSetting.WAITING, 0L);
            Verany.removePlayer(player.getUniqueId().toString(), IHubPlayer.class);

            Bukkit.removeBossBar(new NamespacedKey(HubSystem.INSTANCE, "bossbar_" + player.getName()));

            for (Entity entity : player.getWorld().getEntities()) {
                if (entity instanceof Trident) {
                    Trident trident = (Trident) entity;
                    if (trident.getShooter() instanceof Player) {
                        Player shooter = (Player) trident.getShooter();
                        if (shooter.getName().equals(player.getName()))
                            trident.remove();
                    }
                }
            }
        });
    }
}
