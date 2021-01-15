package net.verany.hubsystem.utils.location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@AllArgsConstructor
@Getter
public class HubLocation {

    private final String world;
    private final double x, y, z;
    private final float yaw, pitch;

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public static HubLocation toHubLocation(Location location) {
        return new HubLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }
}
