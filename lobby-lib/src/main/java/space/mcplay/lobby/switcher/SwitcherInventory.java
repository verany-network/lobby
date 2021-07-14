package space.mcplay.lobby.switcher;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import space.mcplay.Core;
import space.mcplay.api.item.head.HeadItem;
import space.mcplay.inventory.SpigotInventory;
import space.mcplay.inventory.SpigotItem;
import space.mcplay.inventory.module.TaskModule;
import space.mcplay.item.ItemConstructor;
import space.mcplay.language.v2.LanguageAPI;
import space.mcplay.language.v2.message.LanguageMessageFactory;
import space.mcplay.language.v2.replacer.MessageReplacer;
import space.mcplay.plugin.spigot.SpigotPlugin;
import team.dotspace.dolphin.api.NodeAPI;
import team.dotspace.dolphin.node.group.NodeInfoGroup;
import team.dotspace.dolphin.node.info.NodeInfo;

import java.util.*;

public class SwitcherInventory extends SpigotInventory<ItemStack> {

  public static final ItemStack SAME_LOBBY = HeadItem.get("pokeball_default");

  private final NodeInfoGroup nodeInfoGroup;
  private final LanguageMessageFactory messageFactory;

  public SwitcherInventory(Inventory inventory, SpigotPlugin spigotPlugin, UUID uuid) {
    super(inventory, spigotPlugin);

    this.nodeInfoGroup = NodeAPI.getInstance().getNodeInfoGroup("lobby");
    this.messageFactory = LanguageAPI.getInstance().buildMessageFactory(uuid);

    this.withTaskModule(new TaskModule(this, 100L, true));
  }

  @Override
  public void onOpen() {
    for (int i = 0; i < 9; i++)
      this.getInventory().setItem(i, ItemConstructor.placeholder());

    for (int i = 18; i < 27; i++)
      this.getInventory().setItem(i, ItemConstructor.placeholder());

  }

  @Override
  public void onUpdate() {
    if (this.getSpigotItems() != null)
      this.getSpigotItems().clear();

    int slot = 9;

    for (NodeInfo nodeInfo : this.nodeInfoGroup.getNodeInfos().valuesAsList()) {
      boolean local = nodeInfo.getNodeId() == NodeAPI.getInstance().getNodeInfo().getNodeId();

      this.withSpigotItem(new SpigotItem(new ItemConstructor(local ?
        SAME_LOBBY.clone() : new ItemStack(Material.GRAY_DYE))
        .constructItemStack(itemStack ->
          itemStack.setAmount(nodeInfo.getNodeId()))
        .constructItemMeta(itemMeta -> {

          itemMeta.setDisplayName(this.messageFactory.getMessage("lobby_switcher/item/name", new MessageReplacer()
            .addReplacer("GROUP", nodeInfo.getDisplayName())
            .addReplacer("ID", Integer.toString(nodeInfo.getNodeId()))));

          itemMeta.setLore(Arrays.asList(this.messageFactory.getMessageArray(local ?
            "lobby_switcher/item/loresame" : "lobby_switcher/item/lore", new MessageReplacer()
            .addReplacer("PLAYER", Integer.toString(nodeInfo.total()))
            .addReplacer("MAXPLAYER", Integer.toString(nodeInfo.getMaxClients())))));

          itemMeta.addItemFlags(ItemFlag.values());

        }).build(), slot++, (spigotItem, player, inventoryClickEvent) -> {
        final ItemStack itemStack = inventoryClickEvent.getCurrentItem();

        if (itemStack == null ||
          itemStack.getType() == Material.AIR ||
          itemStack.getType() == Material.BLACK_STAINED_GLASS_PANE)
          return;

        if (itemStack.getType() == Material.PLAYER_HEAD) {
          player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 0.75F, 0.75F);
          return;
        }

        player.closeInventory();

        Core.async(() ->
          NodeAPI.getInstance().getNodeBridge().connectToServer(nodeInfo.display(), player.getUniqueId())
        );

      }, spigotItem -> {
      }));

    }

    for (int rest = slot; rest < 8; rest++)
      this.getInventory().setItem(rest, ItemConstructor.empty());
  }
}
