package net.verany.hubsystem.game.hubswitcher;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import lombok.SneakyThrows;
import net.verany.api.Verany;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HubSwitcherInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private final Integer[] slots = {10, 11, 13, 14, 15, 16};
    private final Inventory inventory;

    public HubSwitcherInventory(Player player) {
        this.player = player;
        this.playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
        this.inventory = InventoryBuilder.builder().size(9 * 3).title(playerInfo.getKey("hub.switcher.title")).onClick(event -> {
            String taskName = event.getCurrentItem().getType().equals(Material.GOLDEN_HELMET) ? "VIP-" : "" + "Hub";
            List<ServiceInfoSnapshot> services = getSorted(taskName);
            for (int i = 0; i < slots.length; i++) {
                int slot = slots[i];
                if (event.getSlot() == slot) {
                    ServiceInfoSnapshot hub = services.get(i);
                    if (taskName.startsWith("VIP") && !player.hasPermission("verany.vip")) continue;
                    playerInfo.sendOnServer(hub.getName());
                }
            }
        }).build().fillCycle(new ItemBuilder(Material.valueOf(Verany.toDyeColor(playerInfo.getPrefixPattern().getColor().getFirstColor()) + "_STAINED_GLASS_PANE")).setNoName().build()).createAndOpen(player);
    }

    public void setItems() {
        HubSystem.INSTANCE.setMetadata(player, "hub_switcher", this);

        String[] hubs = {"Hub", "VIPHub"};

        List<ServiceInfoSnapshot> services = new ArrayList<>();
        for (String task : hubs)
            services.addAll(getSorted(task));
        for (int i = 0; i < services.size(); i++) {
            ServiceInfoSnapshot hub = services.get(i);
            if (hub.getProperty(BridgeServiceProperty.IS_ONLINE).isEmpty()) continue;
            String name = hub.getName();
            int online = Verany.GAME_MODE_OBJECT.getOnlinePlayers(new String[]{hub.getServiceId().getTaskName()}, new String[]{name});
            inventory.setItem(slots[i], new ItemBuilder(hub.getServiceId().getName().startsWith("VIP") ? Material.GOLDEN_HELMET : Material.IRON_HELMET).setGlow(name.equals(playerInfo.getServer())).addItemFlag(ItemFlag.values()).setDisplayName(playerInfo.getKey("hub.switcher.item.hub", new Placeholder("%server_name%", name))).addLoreArray(playerInfo.getKeyArray("hub.switcher.item.hub.lore", '~', new Placeholder("%online%", Verany.asDecimal(online)))).build());
        }

    }

    @SneakyThrows
    private List<ServiceInfoSnapshot> getSorted(String task) {
        Collection<ServiceInfoSnapshot> services = CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync(task).get();
        List<Verany.SortData<ServiceInfoSnapshot>> sortData = new ArrayList<>();
        for (ServiceInfoSnapshot service : services)
            sortData.add(new Verany.SortData<>(service.getServiceId().getName(), service));
        services = Verany.sortList(sortData, false);
        return new ArrayList<>(services);
    }
}
