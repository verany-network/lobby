package net.verany.hubsystem.events;

import net.verany.api.Verany;
import net.verany.api.event.events.PlayerLoadCompleteEvent;
import net.verany.api.gamemode.VeranyGameMode;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.skull.SkullBuilder;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.config.HubConfig;
import net.verany.hubsystem.utils.player.HubPlayer;
import net.verany.hubsystem.utils.scoreboard.HubScoreboard;
import net.verany.hubsystem.utils.settings.HubSetting;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

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

        player.setFoodLevel(20);
        player.setHealth(20);
        player.setLevel(0);
        player.setAllowFlight(true);
        player.setFlying(false);

        Bukkit.getScheduler().runTaskLater(HubSystem.INSTANCE, () -> {
            player.setHealthScale(2);
        }, 5);

        new HubScoreboard(player);
    }
}
