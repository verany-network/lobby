package net.verany.lobbysystem.flagwars;

import net.verany.api.season.Season;

public interface IFlagWarsManager {

    void onEnable();

    default long getCurrentSeasonStart() {
        return Season.getCurrentSeason().getStart().getTimeInMillis();
    }

    String getCurrentSeasonFormatted();

}
