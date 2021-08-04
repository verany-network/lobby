package net.verany.lobbysystem.game.time;

import net.verany.api.module.VeranyPlugin;
import net.verany.api.module.VeranyProject;
import org.bukkit.Bukkit;

import java.util.Calendar;
import java.util.TimerTask;

public class DaytimeTask {

    private final Integer[] ticks = new Integer[] {
            18000, 19000, 20000, 21000, 22000, 23000, 0,
            1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000,
            9000, 10000, 11000, 12000, 13000, 14000, 15000,
            16000, 17000
    };
    private final Calendar calendar = Calendar.getInstance();

    public TimerTask buildTask(VeranyPlugin project) {
        return new TimerTask() {
            @Override
            public void run() {
                if(Bukkit.getOnlinePlayers().isEmpty())
                    return;

                int time = ticks[calendar.get(Calendar.HOUR_OF_DAY)] +
                        ((int) (1000 * ((double) (calendar.get(Calendar.SECOND) + (calendar.get(Calendar.MINUTE) * 60)) / (double) 3600)));

                Bukkit.getScheduler().scheduleSyncDelayedTask(project, () -> {
                    Bukkit.getWorlds().forEach(world -> {
                        world.setTime(time);
                    });
                });
            }
        };
    }

}
