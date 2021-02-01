package net.verany.hubsystem.utils.bees;

import com.google.gson.Gson;
import de.dytanic.cloudnet.ext.bridge.BridgePlayerManager;
import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.task.AbstractTask;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.config.HubConfig;
import net.verany.hubsystem.utils.location.LocationManager;
import net.verany.hubsystem.utils.settings.HubSetting;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BeeTimeTask implements Runnable {

    private final List<Location> beehives = new ArrayList<>();
    private final List<Entity> beeList = new ArrayList<>();

    public BeeTimeTask() {
        HubSystem.INSTANCE.getLocationManager().getData(LocationManager.HubLocations.class).getLocations().forEach((s, hubLocation) -> {
            if (s.contains("beenest")) {
                beehives.add(hubLocation.toLocation());
            }
        });
    }

    @Override
    public void run() {

        World world = HubSystem.INSTANCE.getLocationManager().getLocation("spawn").getWorld();
        world.setTime(getWorldTime());

        beeList.forEach(entity -> entity.setTicksLived(1));

        if (Bukkit.getOnlinePlayers().size() != 0) {
            if (!HubConfig.BEES_SPAWNED.getValue() && world.isDayTime()) {
                HubConfig.BEES_SPAWNED.setValue(true);

                for (Location beehive : beehives) {
                    int bees = Verany.getRandomNumberBetween(5, 9);
                    for (int i = 0; i < bees; i++) {
                        Bee bee = beehive.getWorld().spawn(beehive.clone().add(Verany.getRandomNumberBetween(-3, 3), -2, Verany.getRandomNumberBetween(-3, 3)), Bee.class);
                        beeList.add(bee);
                        bee.setBaby();
                        bee.setHive(beehive);
                    }
                }
            } else if (HubConfig.BEES_SPAWNED.getValue() && !world.isDayTime()) {
                HubConfig.BEES_SPAWNED.setValue(false);
                beeList.forEach(Entity::remove);
            }
        } else {
            HubConfig.BEES_SPAWNED.setValue(false);
            beeList.forEach(Entity::remove);
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();

            HubSetting.TimeType timeType = playerInfo.getSettingValue(HubSetting.TIME_TYPE);
            switch (timeType) {
                case DAY:
                    player.setPlayerTime(1000, false);
                    break;
                case NIGHT:
                    player.setPlayerTime(18000, false);
                    break;
            }
        }
    }

    private long getWorldTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HHmm");
        Instant nowUtc = Instant.now();
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(nowUtc, ZoneId.of("Europe/Berlin"));
        String rdat = dtf.format(dateTime) + "0";
        int worldTime = Integer.parseInt(rdat);
        worldTime -= 6000;
        if (worldTime < 0)
            worldTime += 24000;
        return worldTime;
    }
}
