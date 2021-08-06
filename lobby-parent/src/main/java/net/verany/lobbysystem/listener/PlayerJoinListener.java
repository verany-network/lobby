package net.verany.lobbysystem.listener;

import net.verany.api.Verany;
import net.verany.api.event.AbstractListener;
import net.verany.api.event.events.PlayerLoadCompleteEvent;
import net.verany.api.module.VeranyPlugin;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.lobbysystem.LobbySystem;
import net.verany.lobbysystem.flagwars.player.FlagWarsPlayer;
import net.verany.lobbysystem.flagwars.player.IFlagWarsPlayer;
import net.verany.lobbysystem.game.player.IHubPlayer;
import net.verany.lobbysystem.game.player.LobbyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class PlayerJoinListener extends AbstractListener {

    public PlayerJoinListener(VeranyPlugin project) {
        super(project);

        Verany.registerListener(project, PlayerLoadCompleteEvent.class, event -> {
            Player player = event.getPlayer();
            IPlayerInfo playerInfo = Verany.getPlayer(player);

            IHubPlayer hubPlayer = new LobbyPlayer(project);
            hubPlayer.load(player.getUniqueId());
            Verany.setPlayer(IHubPlayer.class, hubPlayer);

            IFlagWarsPlayer flagWarsPlayer = new FlagWarsPlayer(project);
            flagWarsPlayer.load(player.getUniqueId());
            Verany.setPlayer(IFlagWarsPlayer.class, flagWarsPlayer);

            player.getInventory().clear();
            hubPlayer.setItems();

            hubPlayer.setScoreboard();

            /*if (playerInfo.getSettingValue(HubSetting.LAST_LOCATION_TELEPORT))
                player.teleport(hubPlayer.getLastLocation().toLocation());
            else*/
                player.teleport(LobbySystem.INSTANCE.getLocationManager().getLocation("spawn"));

            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setAllowFlight(false);
            /*Bukkit.getScheduler().runTaskLater(LobbySystem.INSTANCE, () -> {
                player.setHealthScale(2);

                LobbySystem.INSTANCE.getLocationManager().getDataOptional(AbstractLocationManager.VeranyLocations.class).ifPresent(veranyLocations -> {
                    veranyLocations.getLocations().forEach((s, veranyLocation) -> {
                        if (s.startsWith("npc_")) {
                            INPC npc = Verany.createNPC(s.split("_")[1], veranyLocation.toLocation(), player);
                            npc.setGameProfile(playerInfo.getSkinData());
                            npc.spawn();
                        }
                    });
                });
            }, 5);*/

            BossBar bossBar = Bukkit.createBossBar(new NamespacedKey(LobbySystem.INSTANCE, "bossbar_" + player.getName()), "", BarColor.BLUE, BarStyle.SEGMENTED_6);
            bossBar.addPlayer(player);
            bossBar.addFlag(BarFlag.PLAY_BOSS_MUSIC);
            hubPlayer.setBossBar(bossBar);
        });
    }
}
