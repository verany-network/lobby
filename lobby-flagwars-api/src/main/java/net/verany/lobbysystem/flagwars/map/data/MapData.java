package net.verany.lobbysystem.flagwars.map.data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.verany.lobbysystem.flagwars.Variant;
import org.bukkit.Material;

import java.util.List;

@RequiredArgsConstructor
@Getter
@Setter
public class MapData {

    private final String name;
    private final Variant variant;
    private final Material material;
    private final int maxUpgrades;
    private final List<String> builder;
    private final List<MapFlag> flags;
    private final boolean enabled;
    private final List<MapRating> ratings;
    private float averageWinChance;

    public double getRating() {
        double toReturn = 0;
        for (MapRating mapRating : ratings)
            toReturn += mapRating.getRating();
        return toReturn / ratings.size();
    }

}
