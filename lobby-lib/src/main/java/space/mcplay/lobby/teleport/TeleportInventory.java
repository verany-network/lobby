package space.mcplay.lobby.teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import space.mcplay.inventory.SpigotInventory;
import space.mcplay.inventory.SpigotItem;
import space.mcplay.inventory.module.TaskModule;
import space.mcplay.item.ItemConstructor;
import space.mcplay.language.v2.LanguageAPI;
import space.mcplay.language.v2.message.LanguageMessageFactory;
import space.mcplay.language.v2.replacer.MessageReplacer;
import space.mcplay.lobby.teleport.collection.TeleportItem;
import space.mcplay.location.list.SpigotDataLocationPool;
import space.mcplay.plugin.spigot.SpigotPlugin;
import team.dotspace.dolphin.api.NodeAPI;

import java.util.Arrays;
import java.util.UUID;

public class TeleportInventory extends SpigotInventory<ItemStack> {

  private final UUID uuid;
  private final SpigotDataLocationPool dataLocationPool;

  private final LanguageMessageFactory factory;

  public TeleportInventory(Inventory inventory, SpigotPlugin spigotPlugin, UUID uuid, SpigotDataLocationPool dataLocationPool) {
    super(inventory, spigotPlugin);
    this.uuid = uuid;
    this.dataLocationPool = dataLocationPool;

    this.factory = LanguageAPI.getInstance().buildMessageFactory(this.uuid);

    this.withTaskModule(new TaskModule(this, 50L, true));
  }

  @Override
  public void onOpen() {
    for (int i = 0; i < 9; i++)
      this.getInventory().setItem(i, ItemConstructor.placeholder());

    for (int i = 45; i < 54; i++)
      this.getInventory().setItem(i, ItemConstructor.placeholder());

    //Spawn Item
    this.withSpigotItem(new SpigotItem(new ItemConstructor(Material.CLOCK).constructItemMeta(itemMeta -> {
      itemMeta.setDisplayName(this.factory.getMessage("teleporter/spawn/name"));
      itemMeta.setLore(Arrays.asList(this.factory.getMessageArray("teleporter/spawn/lore")));
    }).build(), 22, (spigotItem, player, inventoryClickEvent) -> {
      Location location = TeleportInventory.this.dataLocationPool.get("spawn");

      player.teleport(location);
      player.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75F, 0.75F);
    }, spigotItem -> {
    }));


    //Default Items
    for (TeleportItem value : TeleportItem.values())
      this.withSpigotItem(new SpigotItem(new ItemConstructor(value.getItem().clone())
        .constructItemMeta(itemMeta ->
          itemMeta.setDisplayName(this.factory.getMessage("teleporter/" + value.name().toLowerCase() + "/name")))
        .build(), value.getSlot(), (spigotItem, player, inventoryClickEvent) -> {

        switch (inventoryClickEvent.getAction()) {
          case PICKUP_ALL:
            Location location = TeleportInventory.this.dataLocationPool.get(value.name().toLowerCase());

            player.teleport(location);
            player.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 0.75F, 0.75F);
            break;
          case PICKUP_HALF:
            player.performCommand("servergui " + value.toString() + " " + player.getUniqueId().toString());
            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 0.75F, 0.75F);
            break;
          case NOTHING:
            player.sendMessage("-> info");
            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 0.75F, 0.75F);
            break;
        }

      }, spigotItem ->

        new ItemConstructor(spigotItem.getItemStack())
          .constructItemStack(itemStack -> {

            switch (value) {
              case QUICKSHOT:
                itemStack.setType(itemStack.getType() == Material.BOW ? Material.ARROW : Material.BOW);
                break;
              case WATERFFA:
                itemStack.setType(itemStack.getType() == Material.WATER_BUCKET ? Material.BUCKET : Material.WATER_BUCKET);
                break;
            }

          }).constructItemMeta(itemMeta ->
          itemMeta.setLore(Arrays.asList(this.factory.getMessageArray("teleporter/" + value.name().toLowerCase() + "/lore",
            new MessageReplacer().addReplacer("PLAYERS", Integer.toString(NodeAPI.getInstance().getNodeInfoGroup(value.getCloudGroup()).total())))))
        )

      ));
  }
}
