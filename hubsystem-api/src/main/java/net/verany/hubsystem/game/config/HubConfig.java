package net.verany.hubsystem.game.config;

import net.verany.api.config.IngameConfig;

public class HubConfig {

    public static final IngameConfig<Boolean> BEES_SPAWNED = new IngameConfig<>(Boolean.class, false);
    public static final IngameConfig<Integer> ACTIONBAR_SECONDS = new IngameConfig<>(Integer.class, 7);
    public static final IngameConfig<Integer> BOSSBAR_WAIT_SECONDS = new IngameConfig<>(Integer.class, 6);

}
