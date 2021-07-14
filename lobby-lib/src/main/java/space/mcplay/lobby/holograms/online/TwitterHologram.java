package space.mcplay.lobby.holograms.online;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import space.mcplay.Core;
import space.mcplay.hologram.hologram.ServerHologram;
import space.mcplay.language.v2.LanguageAPI;
import space.mcplay.language.v2.replacer.MessageReplacer;
import space.mcplay.plugin.spigot.SpigotPlugin;

import java.util.Arrays;

public class TwitterHologram extends ServerHologram {

  public static class TwitterInfo {

    private int followers;

    public TwitterInfo withFollowers(int followers) {
      this.followers = followers;
      return this;
    }

    public int getFollowers() {
      return this.followers;
    }
  }

  private TwitterInfo info = new TwitterInfo().withFollowers(0);

  public TwitterHologram(SpigotPlugin spigotPlugin) {
    super(spigotPlugin, new Location(Bukkit.getWorld("world"), 6.5, 101.5, 9.5), 2400L);

    this.withServerExecutor(serverHologram -> {

      try {
        this.info = Core.getArangoManager().getConnection().db("cloud").collection("environment")
          .getDocument("twitter", TwitterInfo.class);
      } catch (Exception ignore) {
        this.info = new TwitterInfo().withFollowers(-1);
      }
    });

    this.withPlayerExecutor((serverHologram, playerHologram) ->
      Arrays.asList(LanguageAPI.getInstance().getMessageArray(playerHologram.getPlayer().getUniqueId(), "hologram/twitter",
        new MessageReplacer().addReplacer("FOLLOWERS", Integer.toString(this.info.getFollowers())))));
  }
}
