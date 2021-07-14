package space.mcplay.lobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import space.mcplay.Core;
import space.mcplay.lobby.LobbyPlugin;
import space.mcplay.lobby.item.LobbyItemFactory;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;
import space.mcplay.scoreboard.ScoreboardAPI;

public class PlayerJoinListener extends SpigotListener {

  public PlayerJoinListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler
  public void handle(PlayerJoinEvent event) {
    final Player player = event.getPlayer();

    event.setJoinMessage(null);

    player.getInventory().setHeldItemSlot(4);
    player.teleportAsync(LobbyPlugin.getInstance().getLocationPool().get("spawn"));

    Core.async(() -> LobbyItemFactory.buildItems(player));

    Bukkit.getScheduler().runTaskLaterAsynchronously(LobbyPlugin.getInstance(),
      () -> ScoreboardAPI.getInstance().updateSidebarOfPlayer(player), 10L);
  }
}
