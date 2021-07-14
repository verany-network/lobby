package space.mcplay.lobby.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import space.mcplay.item.ItemConstructor;
import space.mcplay.language.v2.LanguageAPI;
import space.mcplay.language.v2.message.LanguageMessageFactory;
import space.mcplay.lobby.switcher.SwitcherInventory;

import java.util.UUID;

public class LobbyItemFactory {

  public static void buildItems(Player player) {
    final UUID uniqueId = player.getUniqueId();
    final Inventory inventory = player.getInventory();

    inventory.clear();

    final LanguageMessageFactory messageFactory = LanguageAPI.getInstance().buildMessageFactory(uniqueId);

    inventory.setItem(1, new ItemConstructor(Material.COMPASS).constructItemMeta(itemMeta ->
      itemMeta.setDisplayName(messageFactory.getMessage("lobby/item/teleporter"))).build());

    inventory.setItem(7, new ItemConstructor(SwitcherInventory.SAME_LOBBY.clone()).constructItemMeta(itemMeta ->
      itemMeta.setDisplayName(messageFactory.getMessage("lobby/item/lobbyswitcher"))).build());
  }
}
