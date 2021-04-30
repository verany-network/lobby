package net.verany.hubsystem.game.inventory;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import lombok.SneakyThrows;
import net.verany.api.Verany;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.game.VeranyGame;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameInventory implements IHubInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private final VeranyGame game;
    private final Inventory inventory;

    public GameInventory(Player player, VeranyGame game) {
        this.player = player;
        this.playerInfo = Verany.getPlayer(player);
        this.game = game;

        inventory = InventoryBuilder.builder().size(9).title(game.name()).onClick(event -> {
            String name = event.getCurrentItem().getItemMeta().getDisplayName();
            playerInfo.playSound(Sound.ENTITY_PLAYER_LEVELUP);
            playerInfo.sendOnServer(name);
        }).build().createAndOpen(player);
    }

    @Override
    public void setItems() {
        List<ServiceInfoSnapshot> sorted = getSorted(game.getTaskName());
        if (sorted.size() == 1) {
            playerInfo.playSound(Sound.ENTITY_PLAYER_LEVELUP);
            playerInfo.sendOnServer(sorted.get(0).getServiceId().getName());
            return;
        }
        HubSystem.INSTANCE.setMetadata(player, "inventory", this);
        for (int i = 0; i < sorted.size(); i++) {
            ServiceInfoSnapshot service = sorted.get(i);
            int online = service.getProperty(BridgeServiceProperty.PLAYERS).get().size();
            inventory.setItem(i, new ItemBuilder(Material.ARMOR_STAND).setDisplayName(service.getServiceId().getName()).setAmount(online == 0 ? 1 : online).build());
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
