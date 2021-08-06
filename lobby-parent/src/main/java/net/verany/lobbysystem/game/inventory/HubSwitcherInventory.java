package net.verany.lobbysystem.game.inventory;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import lombok.SneakyThrows;
import net.verany.api.Verany;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.lobbysystem.LobbySystem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HubSwitcherInventory implements IHubInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private final Integer[] slots = {10, 11, 13, 14, 15, 16};
    private final Inventory inventory;

    public HubSwitcherInventory(Player player) {
        this.player = player;
        this.playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId(), IPlayerInfo.class).get();
        this.inventory = InventoryBuilder.builder().size(9 * 3).title(playerInfo.getKey("hub.switcher.title")).onClick(event -> {
            if (!event.getCurrentItem().getType().equals(Material.GOLDEN_HELMET) && !event.getCurrentItem().getType().equals(Material.IRON_HELMET))
                return;
            String serverName = ChatColor.stripColor(Verany.serializer.serialize(event.getCurrentItem().getItemMeta().displayName()).split(" ")[1]);
            if (serverName.startsWith("VIP") && !player.hasPermission("verany.vip")) return;
            playerInfo.playSound(Sound.ENTITY_PLAYER_LEVELUP, 1, 1.8F);
            playerInfo.sendOnServer(serverName);
        }).build().fillCycle(new ItemBuilder(Material.valueOf("GRAY_STAINED_GLASS_PANE")).setNoName().build()).createAndOpen(player);
    }

    @Override
    public void setItems() {
        LobbySystem.INSTANCE.setMetadata(player, "inventory", this);

        String[] hubs = {"VIPLobby", "Lobby"};

        List<ServiceInfoSnapshot> services = new ArrayList<>();
        for (String task : hubs)
            services.addAll(getSorted(task));
        for (int i = 0; i < services.size(); i++) {
            ServiceInfoSnapshot hub = services.get(i);
            String name = hub.getServiceId().getName();
            int online = hub.getProperty(BridgeServiceProperty.ONLINE_COUNT).orElse(0);
            inventory.setItem(slots[i], new ItemBuilder(hub.getServiceId().getName().startsWith("VIP") ? Material.GOLDEN_HELMET : Material.IRON_HELMET).setGlow(name.equals(playerInfo.getServer())).addItemFlag(ItemFlag.values()).setDisplayName(playerInfo.getKey("hub.switcher.item.hub", new Placeholder("%server_name%", name))).addLoreArray(playerInfo.getKeyArray("hub.switcher.item.hub.lore", '~', new Placeholder("%online%", Verany.asDecimal(online)))).build());
        }
        for (int i = services.size(); i < slots.length; i++) {
            inventory.setItem(slots[i], null);
        }

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
}
