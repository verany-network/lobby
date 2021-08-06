package net.verany.lobbysystem.flagwars.map;

import com.google.common.collect.Lists;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import net.verany.api.loader.database.DatabaseLoadObject;
import net.verany.api.loader.database.DatabaseLoader;
import net.verany.api.module.VeranyProject;
import net.verany.api.plugin.IVeranyPlugin;
import net.verany.lobbysystem.flagwars.Variant;
import net.verany.lobbysystem.flagwars.map.data.MapData;
import net.verany.lobbysystem.flagwars.map.data.MapFlag;
import net.verany.lobbysystem.flagwars.map.data.MapRating;
import org.bukkit.Material;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Getter
public class MapObject extends DatabaseLoader implements IMapObject {

    private final List<MapData> mapsOfTheDay = new ArrayList<>();

    public MapObject(VeranyProject project) {
        super(project, "maps", "flagwars");
    }

    public void saveWorlds() {
        save("maps");
    }

    @Override
    public void load() {
        load(new LoadInfo<>("maps", MapDataObject.class, new MapDataObject("maps", Collections.singletonList(new MapData("example", Variant.TWOTIMESONE, Material.STONE, 2, Lists.newArrayList("NicoVRNY", "Sthyq"), Collections.singletonList(MapFlag.INTERACTIVE_BLOCKS), true, new ArrayList<>())))));
        mapsOfTheDay.addAll(getRandomMaps(3));
    }

    @Override
    public List<MapData> getMaps() {
        if (getDataOptional(MapDataObject.class).isEmpty()) return new ArrayList<>();
        return getDataOptional(MapDataObject.class).get().getMaps().stream().filter(MapData::isEnabled).collect(Collectors.toList());
    }

    @Override
    public List<MapData> getMapsWithoutDuplicate() {
        return getMaps().stream().filter(distinctBy(MapData::getName)).collect(Collectors.toList());
    }

    @Override
    public List<MapData> getMapsWithoutDuplicate(Variant variant) {
        return getMaps(variant).stream().filter(distinctBy(MapData::getName)).collect(Collectors.toList());
    }

    private <T> Predicate<T> distinctBy(Function<? super T, ?> f) {
        Set<Object> objects = new HashSet<>();
        return t -> objects.add(f.apply(t));
    }

    @Override
    public List<MapData> getMaps(Variant variant) {
        return getMapsWithoutDuplicate().stream().filter(worldData -> worldData.getVariant().equals(variant) && worldData.isEnabled()).collect(Collectors.toList());
    }

    @Override
    public MapData getRandomMap(Variant variant) {
        List<MapData> worlds = getMapsWithoutDuplicate(variant);
        return worlds.get(new Random().nextInt(worlds.size()));
    }

    @Override
    public MapData getRandomMap() {
        List<MapData> worlds = getMapsWithoutDuplicate();
        return worlds.get(new Random().nextInt(worlds.size()));
    }

    @Override
    public List<MapData> getRandomMaps(int amount) {
        List<MapData> worlds = new ArrayList<>(getMapsWithoutDuplicate());
        Collections.shuffle(worlds);
        return worlds.subList(0, Math.min(worlds.size(), amount));
    }

    @Override
    public MapData getMap(String name) {
        return getMapsWithoutDuplicate().stream().filter(worldData -> worldData.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    @Override
    public MapData getMap(Material material) {
        return getMapsWithoutDuplicate().stream().filter(worldData -> worldData.getMaterial().equals(material)).findFirst().orElse(null);
    }

    @Override
    public MapData getMap(Material material, Variant variant) {
        return getMapsWithoutDuplicate(variant).stream().filter(mapData -> mapData.getMaterial().equals(material)).findFirst().orElse(null);
    }

    @Override
    public void rateMap(UUID uuid, String name, double rating) {
        MapData map = getMap(name);
        map.getRatings().add(new MapRating(uuid, rating));
        saveWorlds();
    }

    @Override
    public double getRating(String name) {
        return getMap(name).getRating();
    }

    @Override
    public boolean hasRated(UUID uuid, String name) {
        return getMap(name).getRatings().stream().anyMatch(mapRating -> mapRating.getUuid().equals(uuid));
    }

    @Override
    public MapRating getRating(UUID uuid, String name) {
        return getMap(name).getRatings().stream().filter(mapRating -> mapRating.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    @Getter
    public static class MapDataObject extends DatabaseLoadObject {

        private final List<MapData> maps;

        public MapDataObject(String uuid, List<MapData> maps) {
            super(uuid);
            this.maps = maps;
        }
    }
}