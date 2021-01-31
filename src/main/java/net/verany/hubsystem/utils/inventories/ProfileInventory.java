package net.verany.hubsystem.utils.inventories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.enumhelper.VeranyEnum;
import net.verany.api.hotbar.HotbarItem;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.skull.SkullBuilder;
import net.verany.hubsystem.HubSystem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ProfileInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private final Integer[] profileCategorySlots = {19, 20, 21, 22, 23, 24, 25};

    public ProfileInventory(Player player) {
        this.player = player;
        this.playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
    }

    public void setCategoryItems() {
        for (int i = 0; i < ProfileCategory.values().length; i++) {
            ProfileCategory category = ProfileCategory.values()[i];
            playerInfo.setItem(profileCategorySlots[i], new HotbarItem(new ItemBuilder(category.equals(ProfileCategory.FRIENDS) ? new SkullBuilder(playerInfo.getSkinData()).build() : new ItemStack(category.getMaterial())).setGlow(player.getMetadata("profile.category").get(0).value().equals(category)), player) {
                @Override
                public void onClick(InventoryClickEvent event) {
                    setItems(category).setCategoryItems();
                }
            });
        }
    }

    public ProfileInventory setItems(ProfileCategory category) {
        HubSystem.INSTANCE.setMetadata(player, "profile.category", category);

        switch (category) {
            case FRIENDS:
                setFriendItems();
                break;
            case SETTINGS:
                setSettingItems();
                break;
        }

        return this;
    }

    private void setFriendItems() {

    }

    private void setSettingItems() {

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
            ;

            private final Material material;
        }
    }

}
