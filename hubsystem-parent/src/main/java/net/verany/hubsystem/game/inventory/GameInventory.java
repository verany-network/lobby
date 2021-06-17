package net.verany.hubsystem.game.inventory;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import lombok.SneakyThrows;
import net.verany.api.Verany;
import net.verany.api.inventory.IInventoryBuilder;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.message.KeyBuilder;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.game.VeranyGame;
import net.verany.volcano.round.ServerRoundData;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameInventory implements IHubInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private final VeranyGame game;
    private final IInventoryBuilder inventoryBuilder;
    private Inventory inventory;

    public GameInventory(Player player, VeranyGame game) {
        this.player = player;
        this.playerInfo = Verany.getPlayer(player);
        this.game = game;

        inventoryBuilder = InventoryBuilder.builder().size(9).title(game.name()).onClick(event -> {
            if (event.getCurrentItem().getType().equals(Material.ARMOR_STAND)) {
                connect(playerInfo, event.getCurrentItem());
                return;
            }
        }).build();
    }

    @SneakyThrows
    @Override
    public void setItems() {
        HubSystem.INSTANCE.setMetadata(player, "inventory", this);

        if (game.equals(VeranyGame.BINGO)) {
            Map<ServiceInfoSnapshot, List<Document>> rounds = new HashMap<>();

            for (ServiceInfoSnapshot cloudService : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync("Bingo").get()) {
                if (!cloudService.isConnected() || !cloudService.getProperties().contains("round_data"))
                    continue;
                String roundData = cloudService.getProperties().getString("round_data");
                List<Document> documents = Verany.GSON.fromJson(roundData, ServerRoundData.class).getDocuments();
                documents.removeIf(document -> !document.containsKey("gameState") || (document.containsKey("gameState") && !document.getString("gameState").equalsIgnoreCase("WAITING")));
                rounds.put(cloudService, documents);
            }

            BingoSortType sortType = BingoSortType.NAME;
            List<Verany.SortData<ItemStack>> sortData = new ArrayList<>();
            AtomicBoolean reverse = new AtomicBoolean(sortType.name().endsWith("_REVERSE"));

            rounds.forEach((serviceInfoSnapshot, documents) -> {

                for (Document document : documents) {
                    String id = document.getString("id");
                    int maxPlayers = (int) Math.round(document.getDouble("max_players"));
                    List<String> players = document.getList("players", String.class);

                    String key;
                    if (sortType == BingoSortType.PLAYERS) {
                        key = players.size() + "_" + id;
                        reverse.set(true);
                    } else {
                        key = id;
                    }

                    KeyBuilder displayName = KeyBuilder.builder().
                            key("hub.arcade.bingo.name").
                            placeholders(new Placeholder[]
                                    {
                                            new Placeholder("%name%", serviceInfoSnapshot.getServiceId().getName()),
                                            new Placeholder("%id%", id)
                                    }
                            ).
                            build();

                    KeyBuilder lore = KeyBuilder.builder().
                            key("hub.arcade.bingo.lore").
                            regex('~').
                            placeholders(new Placeholder[]
                                    {
                                            new Placeholder("%players%", players.size()),
                                            new Placeholder("%max_players%", maxPlayers)
                                    }
                            ).build();

                    sortData.add(new Verany.SortData<>(key, new ItemBuilder(Material.ARMOR_STAND).setAmount(players.isEmpty() ? 1 : players.size()).setDisplayName(playerInfo.getKey(displayName)).addLoreArray(playerInfo.getKeyArray(lore)).build()));
                }
            });

            List<ItemStack> items = Verany.sortList(sortData, reverse.get());

            if (items.size() == 1) {
                player.removeMetadata("inventory", HubSystem.INSTANCE);
                connect(playerInfo, items.get(0));
                return;
            }

            int page = playerInfo.getPage("bingo");
            inventoryBuilder.fillPageItems(new IInventoryBuilder.PageData(page, new Integer[]{0, 1, 2, 3, 4, 5}, 8, 7, items), type -> {
                playerInfo.switchPage("bingo", type);
                clear();
                setItems();
            });

            inventory = inventoryBuilder.createAndOpen(player);
            return;
        }
        List<ServiceInfoSnapshot> sorted = getSorted(game.getTaskName());
        if (sorted.size() == 1) {
            player.removeMetadata("inventory", HubSystem.INSTANCE);
            connect(playerInfo, sorted.get(0).getServiceId().getName());
            return;
        }
        for (int i = 0; i < sorted.size(); i++) {
            ServiceInfoSnapshot service = sorted.get(i);
            int online = service.getProperty(BridgeServiceProperty.PLAYERS).get().size();
            inventory.setItem(i, new ItemBuilder(Material.ARMOR_STAND).setDisplayName(service.getServiceId().getName()).setAmount(online == 0 ? 1 : online).build());
        }
    }

    private void connect(IPlayerInfo playerInfo, String service) {
        playerInfo.playSound(Sound.ENTITY_PLAYER_LEVELUP);
        playerInfo.sendOnServer(service);
    }

    private void connect(IPlayerInfo playerInfo, ItemStack itemStack) {
        String name = itemStack.getItemMeta().getDisplayName().split(" ")[1];

        if (name.split("#").length != 0) {
            String server = ChatColor.stripColor(name.split("#")[0]);
            String id = ChatColor.stripColor(name.split("#")[1]);

            ICloudPlayer cloudPlayer = playerInfo.getCloudPlayer();
            cloudPlayer.getProperties().append("round-id", id);
            CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).updateOnlinePlayer(cloudPlayer);

            connect(playerInfo, server);
            return;
        }
        String service = ChatColor.stripColor(name.split("-")[0] + name.split("-")[1]);
        connect(playerInfo, service);
    }

    private void clear() {
        inventoryBuilder.fillInventory(null);
    }

    @SneakyThrows
    private List<ServiceInfoSnapshot> getSorted(String task) {
        Collection<ServiceInfoSnapshot> services = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync(task).get();
        List<Verany.SortData<ServiceInfoSnapshot>> sortData = new ArrayList<>();
        for (ServiceInfoSnapshot service : services)
            if (service.getProperty(BridgeServiceProperty.IS_ONLINE).isPresent() && service.getProperty(BridgeServiceProperty.IS_ONLINE).get())
                sortData.add(new Verany.SortData<>(service.getServiceId().getName(), service));
        services = Verany.sortList(sortData, false);
        return new ArrayList<>(services);
    }

    public enum BingoSortType {
        NAME,
        PLAYERS
    }

}
