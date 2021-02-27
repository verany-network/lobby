package net.verany.hubsystem.utils.inventories.games;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import net.verany.api.Verany;
import net.verany.api.enumhelper.VeranyEnum;
import net.verany.api.inventory.IInventoryBuilder;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.setting.SettingWrapper;
import net.verany.api.settings.AbstractSetting;
import net.verany.hubsystem.HubSystem;
import net.verany.volcano.round.ServerRoundData;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ArcadeInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private final Integer[] contentSlot = {10, 11, 12, 13, 14, 15, 19, 20, 21, 22, 23, 24, 28, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42};
    private final Inventory inventory;
    private final IInventoryBuilder inventoryBuilder;
    private final Category category;

    private final AbstractSetting<BingoSortType> bingoSortType = new SettingWrapper.TempSettingWrapper<>("bingo_sort", BingoSortType.class, BingoSortType.NAME);

    public ArcadeInventory(Player player, Category category) {
        this.player = player;
        this.playerInfo = Verany.getPlayer(player);
        this.category = category;
        this.inventoryBuilder = InventoryBuilder.builder().size(9 * 6).title(playerInfo.getKey("hub.arcade." + category.name().toLowerCase())).event(event -> {

            if (event.getCurrentItem().getType().equals(Material.HOPPER)) {
                switch (category) {
                    case BINGO: {
                        playerInfo.setSettingValue(bingoSortType, BingoSortType.valueOf(event.isLeftClick() ? Verany.getNextEnumValue(BingoSortType.class, playerInfo.getSettingValue(bingoSortType)) : Verany.getPreviousEnumValue(BingoSortType.class, playerInfo.getSettingValue(bingoSortType))));
                        setItems();
                        break;
                    }
                }
                return;
            }

            if (event.getCurrentItem().getType().equals(Material.ARMOR_STAND)) {
                String name = event.getCurrentItem().getItemMeta().getDisplayName().split(" ")[1];

                String server = ChatColor.stripColor(name.split("#")[0]);
                String id = ChatColor.stripColor(name.split("#")[1]);

                ICloudPlayer cloudPlayer = playerInfo.getCloudPlayer();
                cloudPlayer.getProperties().append("round-id", id);
                CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).updateOnlinePlayer(cloudPlayer);

                playerInfo.sendOnServer(server);
            }
        }).build().fillCycle(new ItemBuilder(Material.valueOf(Verany.toDyeColor(playerInfo.getPrefixPattern().getColor().getFirstColor()) + "_STAINED_GLASS_PANE")).setNoName().build());
        this.inventory = inventoryBuilder.buildAndOpen(player);
    }

    @SneakyThrows
    public void setItems() {
        HubSystem.INSTANCE.setMetadata(player, "arcade", this);

        Map<ServiceInfoSnapshot, List<Document>> rounds = new HashMap<>();

        for (ServiceInfoSnapshot cloudService : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync(category.getTaskName()).get()) {
            if (!cloudService.isConnected() || !cloudService.getProperties().contains("round_data")) continue;
            String roundData = cloudService.getProperties().getString("round_data");
            List<Document> documents = Verany.GSON.fromJson(roundData, ServerRoundData.class).getDocuments();
            documents.removeIf(document -> !document.containsKey("gameState") || (document.containsKey("gameState") && !document.getString("gameState").equalsIgnoreCase("WAITING")));
            rounds.put(cloudService, documents);
        }

        for (Integer integer : contentSlot)
            inventory.setItem(integer, null);

        BingoSortType sortType = playerInfo.getSettingValue(bingoSortType);
        List<Verany.SortData<ItemStack>> sortData = new ArrayList<>();
        AtomicBoolean reverse = new AtomicBoolean(sortType.name().endsWith("_REVERSE"));
        rounds.forEach((serviceInfoSnapshot, documents) -> {
            for (Document document : documents) {
                String id = document.getString("id");
                String difficulty = document.getString("difficulty");
                List<String> players = document.getList("players", String.class);

                String key;
                switch (sortType) {
                    case PLAYERS:
                        key = players.size() + "_" + id;
                        reverse.set(true);
                        break;
                    case DIFFICULTY:
                    case DIFFICULTY_REVERSE:
                        if (difficulty.equals("EASY"))
                            key = "a" + "_" + id;
                        else if (difficulty.equals("NORMAL"))
                            key = "b" + "_" + id;
                        else
                            key = "c" + "_" + id;
                        break;
                    default:
                        key = id;
                }

                sortData.add(new Verany.SortData<>(key, new ItemBuilder(Material.ARMOR_STAND).setAmount(players.isEmpty() ? 1 : players.size()).setDisplayName(playerInfo.getKey("hub.arcade." + category.name().toLowerCase() + ".name", new Placeholder("%name%", serviceInfoSnapshot.getServiceId().getName()), new Placeholder("%id%", id))).addLoreArray(playerInfo.getKeyArray("hub.arcade." + category.name().toLowerCase() + ".lore", '~', new Placeholder("%players%", players.size()), new Placeholder("%difficulty%", difficulty))).build()));
            }
        });

        List<ItemStack> items = Verany.sortList(sortData, reverse.get());

        int page = playerInfo.getPage("arcade." + category.name());
        inventoryBuilder.fillPageItems(new IInventoryBuilder.PageData(page, contentSlot, 52, 51, items), type -> {
            playerInfo.switchPage("arcade." + category.name(), type);
            setItems();
        });

        List<String> list = new ArrayList<>();
        for (BingoSortType value : BingoSortType.values())
            list.add(" §8» " + (sortType.equals(value) ? "§a" : "§7") + playerInfo.getKey("hub.sorting.name." + value.name().toLowerCase()));
        inventory.setItem(inventory.getSize() - 9, new ItemBuilder(Material.HOPPER).setDisplayName(playerInfo.getKey("hub.sorting.name")).addLoreAll(list).build());
    }

    @AllArgsConstructor
    @Getter
    public enum Category implements VeranyEnum {
        BINGO(Material.POPPY, "Bingo");

        private final Material material;
        private final String taskName;
    }

    public enum BingoSortType {
        NAME,
        PLAYERS,
        DIFFICULTY,
        DIFFICULTY_REVERSE
    }


}
