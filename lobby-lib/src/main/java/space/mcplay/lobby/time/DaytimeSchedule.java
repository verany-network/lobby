package space.mcplay.lobby.time;

import org.bukkit.Bukkit;
import space.mcplay.plugin.spigot.SpigotPlugin;

import java.nio.charset.CoderResult;
import java.util.Calendar;
import java.util.TimerTask;

public class DaytimeSchedule {

  private final static Integer[] TICKS;
  private final static Calendar CALENDAR = Calendar.getInstance();

  static {
    TICKS = new Integer[]{
      18000, 19000, 20000, 21000, 22000, 23000, 0,
      1000, 2000, 3000, 4000, 5000, 6000, 7000, 8000,
      9000, 10000, 11000, 12000, 13000, 14000, 15000,
      16000, 17000
    };
  }

  public static TimerTask buildTask(SpigotPlugin spigotPlugin) {
    return new TimerTask() {
      @Override
      public void run() {
        if (Bukkit.getOnlinePlayers().isEmpty())
          return;

        final int time =
          //Ticks Hours
          TICKS[CALENDAR.get(Calendar.HOUR_OF_DAY)] +
            //Ticks Minutes
            ((int) (1000 * ((double) (CALENDAR.get(Calendar.SECOND) +
              (CALENDAR.get(Calendar.MINUTE) * 60)) / (double) 3600)));


        Bukkit.getScheduler().runTask(spigotPlugin.getPlugin(), () ->
          Bukkit.getWorlds().forEach(world ->
            //Set time for each world
            world.setTime(time)
          )
        );
      }
    };
  }
}
