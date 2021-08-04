package net.verany.lobbysystem.flagwars.map.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class MapRating {
    private final UUID uuid;
    private final double rating;
}
