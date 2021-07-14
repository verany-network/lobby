package space.mcplay.lobby.teleport.collection;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import space.mcplay.api.texture.TextureSkullItem;

public enum TeleportItem {

  QUICKSHOT(20, new ItemStack(Material.BOW), "quickshot"),
  SKYWARSFFA(24, new TextureSkullItem("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDVjNmRjMmJiZjUxYzM2Y2ZjNzcxNDU4NWE2YTU2ODNlZjJiMTRkNDdkOGZmNzE0NjU0YTg5M2Y1ZGE2MjIifX19").build(), "skywarsffa"),
  WATERFFA(32, new ItemStack(Material.WATER_BUCKET), "waterffa"),
  BEDRUSH(30, new ItemStack(Material.RED_BED), "bedrush");

  private final int slot;
  private final ItemStack item;
  private final String cloudGroup;

  TeleportItem(int slot, ItemStack item, String cloudGroup) {
    this.slot = slot;
    this.item = item;
    this.cloudGroup = cloudGroup;
  }

  public int getSlot() {
    return this.slot;
  }

  public ItemStack getItem() {
    return this.item;
  }

  public String getCloudGroup() {
    return this.cloudGroup;
  }
}
