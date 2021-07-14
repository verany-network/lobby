package space.mcplay.lobby.holograms.online;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import space.mcplay.hologram.hologram.ServerHologram;
import space.mcplay.language.v2.LanguageAPI;
import space.mcplay.language.v2.replacer.MessageReplacer;
import space.mcplay.plugin.spigot.SpigotPlugin;
import team.dotspace.dolphin.api.NodeAPI;

import java.util.Arrays;

public class OnlineHologram extends ServerHologram {

  private int players;

  public OnlineHologram(SpigotPlugin spigotPlugin) {
    super(spigotPlugin, new Location(Bukkit.getWorld("world"), 11.5, 103.4, 0.5), 100);

    this.withServerExecutor(serverHologram ->
      this.players = NodeAPI.getInstance().getNodeInfoGroup("proxy").total());

    this.withPlayerExecutor((serverHologram, playerHologram) ->
      Arrays.asList(LanguageAPI.getInstance().getMessageArray(playerHologram.getPlayer().getUniqueId(), "hologram/cloud/online",
        new MessageReplacer().addReplacer("PLAYERS", Integer.toString(OnlineHologram.this.players)))));

  }


}
