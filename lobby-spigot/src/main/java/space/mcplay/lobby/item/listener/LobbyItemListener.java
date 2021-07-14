package space.mcplay.lobby.item.listener;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import space.mcplay.Core;
import space.mcplay.language.v2.spigot.SpigotLanguage;
import space.mcplay.lobby.LobbyPlugin;
import space.mcplay.lobby.switcher.SwitcherInventory;
import space.mcplay.lobby.teleport.TeleportInventory;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class LobbyItemListener extends SpigotListener {

  public LobbyItemListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    final ItemStack item = player.getItemInHand();
    final int slot = player.getInventory().getHeldItemSlot();

    if (item.getType() == Material.AIR) return;

    switch (event.getAction()) {
      case RIGHT_CLICK_AIR:
      case RIGHT_CLICK_BLOCK:

        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 0.75F, 0.75F);


        switch (item.getType()) {

          case PLAYER_HEAD:

            if (slot == 7)
              Core.async(() ->
                new SwitcherInventory(SpigotLanguage.newInstance().buildInventory(player, "lobbyswitcher/inventory", 27, null),
                  LobbyPlugin.getInstance(), player.getUniqueId()).registerAndOpen(player)
              );

            break;

          case COMPASS:
            Core.async(() ->
              new TeleportInventory(SpigotLanguage.newInstance().buildInventory(player, "teleporter/inventory", 54, null),
                LobbyPlugin.getInstance(), player.getUniqueId(), LobbyPlugin.getInstance().getLocationPool()).registerAndOpen(player)
            );
            break;

        }
        break;
      default:
        break;
    }
  }
}
