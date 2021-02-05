package net.verany.hubsystem.utils.inventories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.enumhelper.EnumHelper;
import net.verany.api.enumhelper.VeranyEnum;
import net.verany.api.hotbar.HotbarItem;
import net.verany.api.inventory.IInventoryBuilder;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.prefix.AbstractPrefixPattern;
import net.verany.api.prefix.PrefixPattern;
import net.verany.api.settings.AbstractSetting;
import net.verany.api.skull.SkullBuilder;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.settings.HubSetting;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;

public class ProfileInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private final Integer[] profileCategorySlots = {19, 20, 21, 22, 23, 24, 25};
    private final Integer[] settingsCategorySlots = {20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
    private final Integer[] settingsSlots = {20, 21, 22, 23, 24};
    private final IInventoryBuilder builder;
    private final Inventory inventory;
    private String clickInfo = "";

    public ProfileInventory(Player player) {
        this.player = player;
        this.playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
        this.builder = InventoryBuilder.builder().size(9 * 6).title(playerInfo.getKey("hub.profile.title")).event(this::onClick).build();
        this.inventory = this.builder.buildAndOpen(player);
    }

    public void setCategoryItems() {
        player.getInventory().clear();

        for (int i = 9; i <= 35; i++)
            player.getInventory().setItem(i, new ItemBuilder(Material.valueOf(Verany.toDyeColor(playerInfo.getPrefixPattern().getColor().getFirstColor()) + "_STAINED_GLASS_PANE")).setNoName().build());

        for (int i = 0; i < ProfileCategory.values().length; i++) {
            ProfileCategory category = ProfileCategory.values()[i];
            playerInfo.setItem(profileCategorySlots[i], new HotbarItem(new ItemBuilder(category.equals(ProfileCategory.FRIENDS) ? new SkullBuilder(playerInfo.getSkinData()).build() : new ItemStack(category.getMaterial())).addItemFlag(ItemFlag.values()).setDisplayName(playerInfo.getKey("hub.profile.category." + category.name().toLowerCase(Locale.ROOT))).setGlow(player.getMetadata("profile.category").get(0).value().equals(category)), player) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    setItems(category).setCategoryItems();
                }
            });
        }
    }

    public ProfileInventory setItems() {
        inventory.clear();
        builder.fillCycle(new ItemBuilder(Material.valueOf(Verany.toDyeColor(playerInfo.getPrefixPattern().getColor().getFirstColor()) + "_STAINED_GLASS_PANE")).setNoName().build());

        clickInfo = "";
        return this;
    }

    public ProfileInventory setItems(ProfileCategory category) {
        setItems();
        HubSystem.INSTANCE.setMetadata(player, "profile.category", category);
        HubSystem.INSTANCE.setMetadata(player, "profile.category.", category);

        switch (category) {
            case FRIENDS:
                setFriendItems();
                break;
            case SETTINGS:
                setSettingItems();
                break;
        }

        inventory.setItem(4, new ItemBuilder(category.getMaterial()).addItemFlag(ItemFlag.values()).setDisplayName(playerInfo.getKey("hub.profile." + category.name().toLowerCase(Locale.ROOT))).build());

        return this;
    }

    private void setFriendItems() {

    }

    private void setSettingItems() {
        clickInfo = "setting_categories";
        int settingCount = -1;
        for (int i = 0; i < ProfileCategory.SettingCategory.values().length; i++) {
            settingCount++;
            ProfileCategory.SettingCategory category = ProfileCategory.SettingCategory.values()[i];
            if (category.equals(ProfileCategory.SettingCategory.VERIFICATIONS) && player.hasPermission("verany.team") || category.equals(ProfileCategory.SettingCategory.TEAM) && !player.hasPermission("verany.team")) {
                settingCount--;
                continue;
            }
            inventory.setItem(settingsCategorySlots[settingCount], new ItemBuilder(category.getMaterial()).addItemFlag(ItemFlag.values()).setDisplayName(playerInfo.getKey("hub.profile.setting." + category.name().toLowerCase())).build());
        }
    }

    private void onClick(InventoryClickEvent event) {
        if (clickInfo.equals("setting_categories")) {
            ProfileCategory.SettingCategory clickedCategory = EnumHelper.INSTANCE.valueOf(event.getCurrentItem().getType(), ProfileCategory.SettingCategory.values());
            if (clickedCategory != null)
                setItems().setSettingItems(clickedCategory).setCategoryItems();
        } else if (clickInfo.startsWith("settings")) {
            if (event.getCurrentItem().getType().equals(Material.CLAY_BALL)) {
                setItems(ProfileCategory.SETTINGS).setCategoryItems();
                return;
            }
            if (clickInfo.endsWith("PREFIX")) {
                for (int i = 0; i < settingsCategorySlots.length; i++) {
                    int slot = settingsCategorySlots[i];
                    if (event.getSlot() == slot) {
                        AbstractPrefixPattern prefixPattern = PrefixPattern.VALUES.get(i);
                        playerInfo.setPrefixPattern(prefixPattern);
                        playerInfo.sendKey(Verany.getPrefix("CoreExecutor", playerInfo.getPrefixPattern()), "core.prefix.selected", new Placeholder("%name%", playerInfo.getKey("core.prefix." + prefixPattern.getKey().toLowerCase())));
                        setItems().setSettingItems(ProfileCategory.SettingCategory.PREFIX).setCategoryItems();
                        return;
                    }
                }
            }
        }
    }

    private ProfileInventory setSettingItems(ProfileCategory.SettingCategory category) {
        clickInfo = "settings_" + category.name();

        inventory.setItem(4, new ItemBuilder(category.getMaterial()).setDisplayName(playerInfo.getKey("hub.profile.setting." + category.name().toLowerCase())).addItemFlag(ItemFlag.values()).build());
        inventory.setItem(inventory.getSize() - 9, new ItemBuilder(Material.CLAY_BALL).setDisplayName(playerInfo.getKey("inventory.back")).build());

        if (category.equals(ProfileCategory.SettingCategory.PREFIX)) {
            for (int i = 0; i < PrefixPattern.VALUES.size(); i++) {
                AbstractPrefixPattern prefixPattern = PrefixPattern.VALUES.get(i);
                inventory.setItem(settingsCategorySlots[i], new ItemBuilder(Material.valueOf(Verany.toDyeColor(prefixPattern.getColor().getFirstColor()) + "_STAINED_GLASS_PANE")).setGlow(prefixPattern.equals(playerInfo.getPrefixPattern())).setDisplayName(prefixPattern.getExample() + "§7Prefix").addLoreArray(playerInfo.getKeyArray("core.prefix.select", "~")).build());
            }
            return this;
        }

        for (int i = 0; i < HubSetting.getSettings(category).size(); i++) {
            AbstractSetting<?> hubSetting = HubSetting.getSettings(category).get(i);
            inventory.setItem(settingsSlots[i], new ItemBuilder(hubSetting.getMaterial()).build());

            if (hubSetting.getTClass().equals(Boolean.class)) {
                AbstractSetting<Boolean> booleanAbstractSetting = (AbstractSetting<Boolean>) hubSetting;
                if (playerInfo.getSettingValue(booleanAbstractSetting))
                    inventory.setItem(settingsSlots[i] + 9, new ItemBuilder(Material.LIME_DYE).build());
                else
                    inventory.setItem(settingsSlots[i] + 9, new ItemBuilder(Material.RED_DYE).build());
            }
        }

        return this;
    }

    @AllArgsConstructor
    @Getter
    public enum ProfileCategory implements VeranyEnum {
        FRIENDS(Material.PLAYER_HEAD),
        CLAN(Material.IRON_CHESTPLATE),
        PARTY(Material.CAKE),
        SETTINGS(Material.COMPARATOR),
        MUSIC_AND_SOUNDS(Material.BELL),
        INVENTORY(Material.ENDER_CHEST),
        INBOX(Material.WRITABLE_BOOK);

        private final Material material;

        @AllArgsConstructor
        @Getter
        public enum SettingCategory implements VeranyEnum {
            SOUND(Material.MUSIC_DISC_CAT),
            PARTICLE(Material.BLAZE_POWDER),
            ANIMATIONS(Material.ENCHANTING_TABLE),
            FRIENDS(Material.PLAYER_HEAD),
            CLANS(Material.IRON_CHESTPLATE),
            PARTY(Material.CAKE),
            PREFIX(Material.PAPER),
            HUB(Material.BEACON),
            VERIFICATIONS(Material.NAME_TAG),
            TEAM(Material.DIAMOND_HORSE_ARMOR),
            PRIVACY(Material.BARRIER);

            private final Material material;
        }
    }

}
