package net.verany.lobbysystem.flagwars;

import net.verany.api.command.AbstractCommand;
import net.verany.api.region.GameRegion;
import net.verany.api.season.Season;
import net.verany.lobbysystem.flagwars.map.IMapObject;
import net.verany.lobbysystem.flagwars.round.AbstractRound;

import java.util.List;

public interface IFlagWarsManager {

    void onEnable();

    default long getCurrentSeasonStart() {
        return Season.getCurrentSeason().getStart().getTimeInMillis();
    }

    String getCurrentSeasonFormatted();

    IMapObject getMapObject();

    GameRegion getRegion();

    List<AbstractRound> getPreparingRounds();

}
