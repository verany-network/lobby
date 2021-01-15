package net.verany.hubsystem.utils.location;

import lombok.Getter;
import net.verany.api.loader.database.DatabaseLoadObject;
import net.verany.api.loader.database.DatabaseLoader;
import net.verany.api.module.VeranyProject;
import org.bukkit.Location;

import java.util.*;

public class LocationManager extends DatabaseLoader {

    public LocationManager(VeranyProject project) {
        super(project, "locations");

        load(new LoadInfo<>("hub_locations", HubLocations.class, new HubLocations()));
    }

    public void save() {
        save("hub_locations");
    }

    public void createLocation(String name, Location location) {
        getData(HubLocations.class).getLocations().put(name, HubLocation.toHubLocation(location));
    }

    public Location getLocation(String name) {
        return getData(HubLocations.class).getLocations().get(name).toLocation();
    }

    public boolean existLocation(String name) {
        return getData(HubLocations.class).getLocations().containsKey(name);
    }

    @Getter
    public static class HubLocations extends DatabaseLoadObject {

        private final Map<String, HubLocation> locations = new HashMap<>();

        public HubLocations() {
            super("hub_locations");
        }
    }

}
