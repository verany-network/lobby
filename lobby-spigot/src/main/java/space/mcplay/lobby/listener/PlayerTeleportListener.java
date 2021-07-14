package space.mcplay.lobby.listener;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class PlayerTeleportListener extends SpigotListener {

  public PlayerTeleportListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler
  public void handle(final PlayerTeleportEvent event) {
    final Player player = event.getPlayer();
    final Location location = event.getTo();

    if (event.getCause() != PlayerTeleportEvent.TeleportCause.PLUGIN) return;

    Bukkit.getScheduler().runTaskAsynchronously(this.getPlugin().getPlugin(), () -> {
      final Block block = location.clone().subtract(0.0, 1.0, 0.0).getBlock();

      if (block.getType() != Material.AIR && block.getType().isSolid())
        player.playEffect(block.getLocation(), Effect.STEP_SOUND, block.getType());
    });

  }
}
