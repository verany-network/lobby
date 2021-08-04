package net.verany.lobbysystem.flagwars.map;

import net.verany.lobbysystem.flagwars.Variant;
import net.verany.lobbysystem.flagwars.map.data.MapData;
import net.verany.lobbysystem.flagwars.map.data.MapRating;
import org.bukkit.Material;

import java.util.List;
import java.util.UUID;

public interface IMapObject {

    void saveWorlds();

    void load();

    List<MapData> getMaps();

    List<MapData> getMapsWithoutDuplicate();

    List<MapData> getMapsWithoutDuplicate(Variant variant);

    List<MapData> getMaps(Variant variant);

    MapData getRandomMap(Variant variant);

    MapData getRandomMap();

    MapData getMap(String name);

    MapData getMap(Material material);

    List<MapData> getRandomMaps(int amount);

    MapData getMap(Material material, Variant variant);

    List<MapData> getMapsOfTheDay();

    void rateMap(UUID uuid, String name, double rating);

    double getRating(String name);

    boolean hasRated(UUID uuid, String name);

    MapRating getRating(UUID uuid, String name);

}
