package net.verany.lobbysystem.flagwars.player.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.verany.lobbysystem.flagwars.Variant;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class RoundInfo {
    private final String id;
    private final List<UUID> players;
    private final List<UUID> winner;
    private final long start, stop = System.currentTimeMillis();
    private final String playedMap;
    private final Variant variant;
    private final Map<String, Object> votingData;
    private int kills = 0, deaths = 0, grabbedBanner = 0;

    public boolean isWinner(UUID uuid) {
        return winner.contains(uuid);
    }
}
