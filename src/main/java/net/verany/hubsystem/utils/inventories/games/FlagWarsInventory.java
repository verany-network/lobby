package net.verany.hubsystem.utils.inventories.games;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.player.CloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import lombok.Getter;
import lombok.SneakyThrows;
import net.verany.api.Verany;
import net.verany.api.inventory.IInventoryBuilder;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.settings.SettingWrapper;
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

public class FlagWarsInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private final Integer[] contentSlot = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    private final Inventory inventory;
    private final IInventoryBuilder inventoryBuilder;

    public FlagWarsInventory(Player player) {
        this.player = player;
        this.playerInfo = Verany.getPlayer(player);
        this.inventoryBuilder = InventoryBuilder.builder().size(9 * 6).title(playerInfo.getKey("hub.flagwars.solo")).event(event -> {

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
    public void setItems(FlagWarsVariant flagWarsVariant) {
        HubSystem.INSTANCE.setMetadata(player, "flagwars", this);
        HubSystem.INSTANCE.setMetadata(player, "variant", flagWarsVariant);

        Map<ServiceInfoSnapshot, List<Document>> rounds = new HashMap<>();

        for (ServiceInfoSnapshot cloudService : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync("FW-Lobby").get()) {
            if (!cloudService.isConnected() || !cloudService.getProperties().contains("round_data")) continue;
            String roundData = cloudService.getProperties().getString("round_data");
            List<Document> documents = Verany.GSON.fromJson(roundData, ServerRoundData.class).getDocuments();
            documents.removeIf(document -> !document.containsKey("gameState") || (document.containsKey("gameState") && !document.getString("gameState").equalsIgnoreCase("WAITING")));
            rounds.put(cloudService, documents);
        }

        for (Integer integer : contentSlot)
            inventory.setItem(integer, null);
        List<Verany.SortData<ItemStack>> sortData = new ArrayList<>();

        rounds.forEach((serviceInfoSnapshot, documents) -> {

            for (Document document : documents) {
                String id = document.getString("id");
                int maxPlayers = (int) Math.round(document.getDouble("max_players"));
                List<String> players = document.getList("players", String.class);
                String variant = document.getString("variant");
                if (!Arrays.asList(flagWarsVariant.getVariants()).contains(variant)) continue;

                sortData.add(new Verany.SortData<>(id, new ItemBuilder(Material.ARMOR_STAND).setAmount(players.isEmpty() ? 1 : players.size()).setDisplayName(playerInfo.getKey("hub.flagwars." + flagWarsVariant.name().toLowerCase() + ".name", new Placeholder("%name%", serviceInfoSnapshot.getServiceId().getName()), new Placeholder("%id%", id))).addLoreArray(playerInfo.getKeyArray("hub.flagwars.lore", '~', new Placeholder("%players%", players.size()), new Placeholder("%max_players%", maxPlayers), new Placeholder("%variant%", variant))).build()));
            }
        });

        List<ItemStack> items = new ArrayList<>();

        ICloudPlayer cloudPlayer = playerInfo.getCloudPlayer();
        for (String variant : flagWarsVariant.getVariants()) {
            for (ServiceInfoSnapshot serviceInfoSnapshot : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync("FW-" + variant).get()) {
                if (cloudPlayer.getProperties().contains("rejoin-time") && cloudPlayer.getProperties().contains("server") && cloudPlayer.getProperties().getString("server").equals(serviceInfoSnapshot.getServiceId().getName())) {
                    long rejoinTime = cloudPlayer.getProperties().getLong("rejoin-time");
                    System.out.println(rejoinTime);
                    if (rejoinTime >= System.currentTimeMillis()) {
                        String roundData = serviceInfoSnapshot.getProperties().getString("round_data");

                        Document dataDocument = Verany.GSON.fromJson(roundData, ServerRoundData.class).getDocuments().stream().filter(document -> document.containsKey("id") && document.getString("id").equals(cloudPlayer.getProperties().getString("round-id"))).findAny().orElse(null);
                        if (dataDocument == null) continue;

                        String id = dataDocument.getString("id");
                        int secondsRemaining = Math.toIntExact((rejoinTime - System.currentTimeMillis()) / 1000);
                        items.add(new ItemBuilder(Material.ARMOR_STAND).setGlow().setDisplayName(playerInfo.getKey("hub.flagwars." + flagWarsVariant.name().toLowerCase() + ".name", new Placeholder("%name%", serviceInfoSnapshot.getServiceId().getName()), new Placeholder("%id%", id))).addLoreArray("§7Zeit zum rejoinen§8: §b" + Verany.formatSeconds(secondsRemaining)).build());
                    }
                }
            }
        }

        items.addAll(Verany.sortList(sortData, false));

        int page = playerInfo.getPage("flagwars.solo");
        inventoryBuilder.fillPageItems(new IInventoryBuilder.PageData(page, contentSlot, 52, 51, items), type -> {
            playerInfo.switchPage("flagwars.solo", type);
            setItems(flagWarsVariant);
        });

    }

    @Getter
    public enum FlagWarsVariant {
        SOLO("2x1", "8x1"),
        DUO("2x2");

        private final String[] variants;

        FlagWarsVariant(String... variants) {
            this.variants = variants;
        }
    }

}
