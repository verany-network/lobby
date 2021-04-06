package net.verany.hubsystem.events;

import lombok.SneakyThrows;
import net.verany.api.Verany;
import net.verany.api.event.events.PlayerLoadCompleteEvent;
import net.verany.api.gamemode.VeranyGameMode;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.npc.INPC;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.skull.SkullBuilder;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.config.HubConfig;
import net.verany.hubsystem.utils.player.HubPlayer;
import net.verany.hubsystem.utils.scoreboard.HubScoreboard;
import net.verany.hubsystem.utils.settings.HubSetting;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

public class JoinEvent implements Listener {

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @SneakyThrows
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerLoadCompleteEvent event) {
        Player player = event.getPlayer();
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0);
        player.setWalkSpeed(0.3F);
        player.setGameMode(GameMode.ADVENTURE);

        HubPlayer hubPlayer = new HubPlayer(HubSystem.INSTANCE);
        hubPlayer.load(player.getUniqueId());
        Verany.setPlayer(HubPlayer.class, hubPlayer);

        if (playerInfo.getSettingValue(HubSetting.LAST_LOCATION_TELEPORT))
            player.teleport(hubPlayer.getData(HubPlayer.PlayerData.class).getLastLocation().toLocation());
        else
            player.teleport(HubSystem.INSTANCE.getLocationManager().getLocation("spawn"));

        hubPlayer.setItems();

        BossBar bossBar = Bukkit.createBossBar(new NamespacedKey(HubSystem.INSTANCE, "bossbar_" + player.getName()), "", BarColor.BLUE, BarStyle.SEGMENTED_6);
        bossBar.addPlayer(player);

        player.setFoodLevel(20);
        player.setHealth(20);
        player.setLevel(0);
        player.setAllowFlight(true);
        player.setFlying(false);

        Bukkit.getScheduler().runTaskLater(HubSystem.INSTANCE, () -> {
            player.setHealthScale(2);
        }, 5);

        playerInfo.setSkinData();
        INPC npc = Verany.createNPC("Survival", new Location(Bukkit.getWorld("world"), 1.5D, 68D, 17.5D, -135F, 5F), true, player);
        npc.setGameProfile(playerInfo.getSkinData());
        npc.lookAtPlayer(player);
        npc.spawn();

        new HubScoreboard(player);
    }
}
