package space.mcplay.lobby.reward;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import space.mcplay.Core;
import space.mcplay.inventory.SpigotInventory;
import space.mcplay.inventory.SpigotItem;
import space.mcplay.inventory.module.TaskModule;
import space.mcplay.item.ItemConstructor;
import space.mcplay.language.v2.LanguageAPI;
import space.mcplay.language.v2.replacer.MessageReplacer;
import space.mcplay.lobby.reward.collection.CreditReward;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.time.TimeUtils;
import space.mcplay.userprofile.profile.client.Profile;
import space.mcplay.userprofile.profile.client.ProfileHandle;
import space.mcplay.userprofile.profile.economy.EconomyProfile;
import space.mcplay.userprofile.profile.economy.EconomyProfileHandle;
import space.mcplay.userprofile.profile.economy.EconomyToken;
import space.mcplay.userprofile.profile.utils.EconomyFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RewardInventory extends SpigotInventory<ItemStack> {

  public static void build(SpigotPlugin spigotPlugin, Player player) {
    UUID uuid = player.getUniqueId();

    new RewardInventory(Bukkit.createInventory(null, 27,
      LanguageAPI.getInstance().getMessage(uuid, "reward/inventory")), spigotPlugin, uuid).registerAndOpen(player);
  }

  private final UUID uuid;
  private Profile profile;

  private boolean lock;

  public RewardInventory(Inventory inventory, SpigotPlugin spigotPlugin, UUID uuid) {
    super(inventory, spigotPlugin);
    this.uuid = uuid;

    ProfileHandle.queue(propertyProfileHandle -> propertyProfileHandle.getProfile(uuid).ifPresent(profile -> this.profile = profile));
    this.withTaskModule(new TaskModule(this, 20L, true));
  }

  @Override
  public void onOpen() {

    for (CreditReward value : CreditReward.values())
      this.withSpigotItem(new SpigotItem(new ItemConstructor(Material.BOOK).build(), value.getSlot(), (spigotItem, player, inventoryClickEvent) -> {
        if (RewardInventory.this.lock) {
          player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 0.75F, 0.75F);
          return;
        }

        RewardInventory.this.lock = true;

        Core.async(() -> {
          if (RewardInventory.this.withCreditReward(value)) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.75F, 0.75F);
            RewardInventory.this.executeUpdate();

            RewardInventory.this.getSpigotPlugin().getHook().syncThread(player::updateInventory);
          } else
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.75F, 0.75F);

          RewardInventory.this.lock = false;
        });

      }, spigotItem -> {
        long remaining = RewardInventory.this.getRemaining(value);

        spigotItem.withItemStack(new ItemConstructor(spigotItem.getItemStack())
          .constructItemStack(itemStack -> itemStack.setType(remaining > 0 ? Material.BOOK : Material.ENCHANTED_BOOK))
          .constructItemMeta(itemMeta -> {

            itemMeta.setDisplayName(LanguageAPI.getInstance().getMessage(RewardInventory.this.uuid, remaining > 0 ?
                (remaining >= TimeUnit.HOURS.toMillis(1) ? "reward/cooldown/hours" : "reward/cooldown/minutes") : "reward/pickup",
              new MessageReplacer().addReplacer("TIME", TimeUtils.toString(remaining))));

            itemMeta.addItemFlags(ItemFlag.values());
          }).build());
      }));
  }

  public boolean withCreditReward(CreditReward creditReward) {
    if (this.getRemaining(creditReward) > 0)
      return false;

    ProfileHandle.queue(propertyProfileHandle -> propertyProfileHandle.getProfile(this.uuid).ifPresent(profile -> propertyProfileHandle.syncProfile(this.profile = profile.withProperty(creditReward.getProfileValue(), TimeUtils.getTimeWithOffset(creditReward.getCoolDown())))));
    EconomyProfileHandle.queue(economyProfileHandle -> {
      EconomyProfile economyProfile = economyProfileHandle.getProfileOrDefault(this.uuid);
      economyProfileHandle.syncProfile(economyProfile.withTokenName(EconomyToken.COMMON.toString(), new EconomyFactory(economyProfile).addAndGet(EconomyToken.COMMON.toString(), creditReward.getDefaultReward())));
    });
    return true;
  }

  public long getRemaining(CreditReward creditReward) {
    long time = (long) this.profile.getProfileProperties().getOrDefault(creditReward.getProfileValue(), 0L);

    if (time <= 0) return 0;

    long remaining = time - System.currentTimeMillis();
    return remaining > 0 ? remaining : 0;
  }
}
