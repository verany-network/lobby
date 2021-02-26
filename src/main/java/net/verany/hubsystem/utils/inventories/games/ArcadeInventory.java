package net.verany.hubsystem.utils.inventories.games;

import de.dytanic.cloudnet.common.document.gson.JsonDocProperty;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.ext.bridge.bukkit.BukkitCloudNetHelper;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.enumhelper.VeranyEnum;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import net.verany.volcano.round.ServerRoundData;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class ArcadeInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private final Integer[] contentSlot = {10, 11, 12, 13, 14, 15, 19, 20, 21, 22, 23, 24, 28, 29, 30, 31, 32, 33, 37, 38, 39, 40, 41, 42};
    private final Inventory inventory;
    private final Category category;

    public ArcadeInventory(Player player, Category category) {
        this.player = player;
        this.playerInfo = Verany.getPlayer(player);
        this.category = category;
        this.inventory = InventoryBuilder.builder().size(9 * 6).title(playerInfo.getKey("hub.arcade." + category.name().toLowerCase())).event(event -> {

            if (event.getCurrentItem().getType().equals(Material.ARMOR_STAND)) {
                String name = event.getCurrentItem().getItemMeta().getDisplayName();

                String server = name.split("#")[0];
                String id = name.split("#")[1];

                ICloudPlayer cloudPlayer = playerInfo.getCloudPlayer();
                cloudPlayer.getProperties().append("round-id", id);
                CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).updateOnlinePlayer(cloudPlayer);

                playerInfo.sendOnServer(server);
            }
        }).build().fillCycle(new ItemBuilder(Material.valueOf(Verany.toDyeColor(playerInfo.getPrefixPattern().getColor().getFirstColor()) + "_STAINED_GLASS_PANE")).setNoName().build()).buildAndOpen(player);
    }

    public void setItems() {
        HubSystem.INSTANCE.setMetadata(player, "arcade", this);

        Map<ServiceInfoSnapshot, List<Document>> rounds = new HashMap<>();

        for (ServiceInfoSnapshot cloudService : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServices(category.getTaskName())) {
            if (!cloudService.isConnected() || !cloudService.getProperties().contains("round_data")) continue;
            String roundData = cloudService.getProperties().getString("round_data");
            List<Document> documents = Verany.GSON.fromJson(roundData, ServerRoundData.class).getDocuments();
            documents.removeIf(document -> !document.containsKey("gameState") || (document.containsKey("gameState") && !document.getString("gameState").equalsIgnoreCase("WAITING")));
            rounds.put(cloudService, documents);
        }

        for (Integer integer : contentSlot)
            inventory.setItem(integer, null);

        rounds.forEach((serviceInfoSnapshot, documents) -> {
            for (int i = 0; i < documents.size(); i++) {
                Document document = documents.get(i);

                String id = document.getString("id");
                String difficulty = document.getString("difficulty");
                List<String> players = document.getList("players", String.class);

                inventory.setItem(contentSlot[i], new ItemBuilder(Material.ARMOR_STAND).setDisplayName(serviceInfoSnapshot.getServiceId().getName() + "#" + id).addLoreArray("§7Players: §b" + players.size() + " §8/ §b16", "§7Difficulty§8: §b" + difficulty).build());
            }
        });
    }

    @AllArgsConstructor
    @Getter
    public enum Category implements VeranyEnum {
        BINGO(Material.POPPY, "Bingo");

        private final Material material;
        private final String taskName;
    }

}
