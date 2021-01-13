package net.verany.hubsystem.utils.inventories;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.actionbar.DefaultActionbar;
import net.verany.api.enumhelper.EnumHelper;
import net.verany.api.enumhelper.VeranyEnum;
import net.verany.api.inventory.IInventoryBuilder;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

@AllArgsConstructor
public class TeleporterInventory {

    private final Player player;

    private final Integer[] itemSlots = {10, 11, 12, 13};

    public void setItems() {
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();

        Inventory inventory = InventoryBuilder.builder().size(9 * 6).title("§bTeleporter").event(event -> {
            event.setCancelled(true);

            Location spawnLocation = new Location(Bukkit.getWorld("world"), 6, 67, 13, 0, 0);
            Location flagWarsArea = new Location(Bukkit.getWorld("world"), 113.5, 64.2, -58.5, -137, 0);
            Location snowWarsArea = new Location(Bukkit.getWorld("world"), 143.5, 66.2, 11.5, -90, 0);
            Location duelsArea = new Location(Bukkit.getWorld("world"), 143.5, 66.2, -11.5, -87, 88);
            Location creativeLocation = new Location(Bukkit.getWorld("world"), -24.5, 65.2, 9.5, 160, 0);
            Location teamHallLocation = new Location(Bukkit.getWorld("world"), 85.5, 53.2, 110.5, -5, 0);
            Location dailyRewardLocation = new Location(Bukkit.getWorld("world"), 6.5, 66.2, 1.5, -145, 0);
            Location lootBoxesLocation = new Location(Bukkit.getWorld("world"), 44.5, 63.2, 21.5, -45, 0);
            Location hallOfPainLocation = new Location(Bukkit.getWorld("world"), -31.5, 47.2, -33.5, -16, 0);
            Location elytraLocation = new Location(Bukkit.getWorld("world"), 20.5, 66.2, 6.5, -71, 0);
            Location infinityJumpAndRunLocation = new Location(Bukkit.getWorld("world"), 19.5, 66.2, 16.5, -71, 0);

            if (event.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;

            // FlagWars
            if (event.getCurrentItem().getType().equals(Material.BLUE_BANNER)) {
                if (event.isLeftClick()) {
                    if (event.isShiftClick()) {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Eine Erklärung zu FlagWars findest du unter vrny.link/fwdesc§8.");
                    } else {
                        player.teleport(flagWarsArea);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                        playerInfo.addActionbar(new DefaultActionbar("§f§l§oDu wurdest zu §b§l§oFlagWars §f§l§oteleportert", 2000));
                    }
                }

                if (event.isRightClick()) {
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Finde Server§8...");
                }
            }

            // SnowWars
            if (event.getCurrentItem().getType().equals(Material.SNOWBALL)) {
                if (event.isLeftClick()) {
                    if (event.isShiftClick()) {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Eine Erklärung zu SnowWars findest du unter vrny.link/swdesc§8.");
                    } else {
                        player.teleport(snowWarsArea);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                        playerInfo.addActionbar(new DefaultActionbar("§f§l§oDu wurdest zu §b§l§oSnowWars §f§l§oteleportert", 2000));
                    }
                }

                if (event.isRightClick()) {
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Finde Server§8...");
                }
            }

            // RPG
            if (event.getCurrentItem().getType().equals(Material.TOTEM_OF_UNDYING)) {
                if (event.isLeftClick()) {
                    if (event.isShiftClick()) {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Eine Erklärung zu RPG findest du unter vrny.link/rpgdesc§8.");
                    } else {
                        player.teleport(spawnLocation);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                        playerInfo.addActionbar(new DefaultActionbar("§f§l§oDu wurdest zu §b§l§oRPG §f§l§oteleportert", 2000));
                    }
                }

                if (event.isRightClick()) {
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Finde Server§8...");
                }
            }

            // Spawn
            if (event.getCurrentItem().getType().equals(Material.BEACON)) {
                if (event.isLeftClick()) {
                    player.teleport(spawnLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                    playerInfo.addActionbar(new DefaultActionbar("§f§l§oDu wurdest zum §b§l§oSpawn §f§l§oteleportert", 2000));
                }

                if (event.isRightClick()) {
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                    player.teleport(spawnLocation);
                    playerInfo.addActionbar(new DefaultActionbar("§f§l§oDu wurdest zum §b§l§oSpawn §f§l§oteleportert", 2000));
                }
            }

            // Survival
            if (event.getCurrentItem().getType().equals(Material.CROSSBOW)) {
                if (event.isLeftClick()) {
                    if (event.isShiftClick()) {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Eine Erklärung zu Survival findest du unter vrny.link/sudesc§8.");
                    } else {
                        player.teleport(spawnLocation);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                        playerInfo.addActionbar(new DefaultActionbar("§f§l§oDu wurdest zu §b§l§oSurvival §f§l§oteleportert", 2000));
                    }
                }

                if (event.isRightClick()) {
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Finde Server§8...");
                }
            }

            // Duels
            if (event.getCurrentItem().getType().equals(Material.STICK)) {
                if (event.isLeftClick()) {
                    if (event.isShiftClick()) {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Eine Erklärung zu Duels findest du unter vrny.link/dudesc§8.");
                    } else {
                        player.teleport(spawnLocation);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "playerInfo.addActionbar(new DefaultActionbar(\"§f§l§oDu wurdest zu §b§l§oDuels §f§l§oteleportert\", 2000));");
                    }
                }

                if (event.isRightClick()) {
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Finde Server§8...");
                }
            }

            // Creative
            if (event.getCurrentItem().getType().equals(Material.NETHERITE_AXE)) {
                if (event.isLeftClick()) {
                    if (event.isShiftClick()) {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Eine Erklärung zu Creative findest du unter vrny.link/crdesc§8.");
                    } else {
                        player.teleport(creativeLocation);
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "playerInfo.addActionbar(new DefaultActionbar(\"§f§l§oDu wurdest zu §b§l§oCreative §f§l§oteleportert\", 2000));");
                    }
                }

                if (event.isRightClick()) {
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Finde Server§8...");
                }
            }

            // Team Hall
            if (event.getCurrentItem().getType().equals(Material.DIAMOND)) {
                if (event.isLeftClick()) {
                    player.teleport(teamHallLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "playerInfo.addActionbar(new DefaultActionbar(\"§f§l§oDu wurdest zur §b§l§oTeam Halle §f§l§oteleportert\", 2000));");
                }

            }

            // Loot Boxes
            if (event.getCurrentItem().getType().equals(Material.END_PORTAL_FRAME)) {
                if (event.isLeftClick()) {
                    player.teleport(lootBoxesLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "playerInfo.addActionbar(new DefaultActionbar(\"§f§l§oDu wurdest zu den §b§l§oLoot Boxen §f§l§oteleportert\", 2000));");
                }

            }

            // Hall of Pain
            if (event.getCurrentItem().getType().equals(Material.NETHER_STAR)) {
                if (event.isLeftClick()) {
                    player.teleport(hallOfPainLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "playerInfo.addActionbar(new DefaultActionbar(\"§f§l§oDu wurdest zur §b§l§oHall of Pain §f§l§oteleportert\", 2000));");
                }
            }

            // Daily Reward
            if (event.getCurrentItem().getType().equals(Material.EXPERIENCE_BOTTLE)) {
                if (event.isLeftClick()) {
                    player.teleport(dailyRewardLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "playerInfo.addActionbar(new DefaultActionbar(\"§f§l§oDu wurdest zu der §b§l§otäglichen Belohnung §f§l§oteleportert\", 2000));");
                }
            }

            // Elytra
            if (event.getCurrentItem().getType().equals(Material.ELYTRA)) {
                if (event.isLeftClick()) {
                    player.teleport(elytraLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "playerInfo.addActionbar(new DefaultActionbar(\"§f§l§oDu wurdest zur §b§l§oElytra §f§l§oteleportert\", 2000));");
                }
            }

            // Infinity Jump and Run
            if (event.getCurrentItem().getType().equals(Material.GOLDEN_BOOTS)) {
                if (event.isLeftClick()) {
                    player.teleport(infinityJumpAndRunLocation);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2F, 2F);
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "playerInfo.addActionbar(new DefaultActionbar(\"§f§l§oDu wurdest zum §b§l§oJump and Run §f§l§oteleportert\", 2000));");
                }
            }


        }).build().fillInventory(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setNoName().build()).buildAndOpen(player);

        inventory.setItem(11, new ItemBuilder(Material.BLUE_BANNER).setDisplayName(Verany.getPrefix("FlagWars", playerInfo.getPrefixPattern())).build());
        inventory.setItem(15, new ItemBuilder(Material.SNOWBALL).setDisplayName(Verany.getPrefix("SnowWars", playerInfo.getPrefixPattern())).build());
        inventory.setItem(19, new ItemBuilder(Material.TOTEM_OF_UNDYING).setDisplayName(Verany.getPrefix("RPG", playerInfo.getPrefixPattern())).build());
        inventory.setItem(22, new ItemBuilder(Material.BEACON).setDisplayName(Verany.getPrefix("Spawn", playerInfo.getPrefixPattern())).build());
        inventory.setItem(25, new ItemBuilder(Material.CROSSBOW).setDisplayName(Verany.getPrefix("Survival", playerInfo.getPrefixPattern())).build());
        inventory.setItem(29, new ItemBuilder(Material.STICK).setDisplayName(Verany.getPrefix("Duels", playerInfo.getPrefixPattern())).build());
        inventory.setItem(33, new ItemBuilder(Material.LADDER).setDisplayName(Verany.getPrefix("Arcade", playerInfo.getPrefixPattern())).build());
        inventory.setItem(46, new ItemBuilder(Material.NETHERITE_AXE).setDisplayName(Verany.getPrefix("Creative", playerInfo.getPrefixPattern())).build());
        inventory.setItem(47, new ItemBuilder(Material.DIAMOND).setDisplayName(Verany.getPrefix("Team Hall", playerInfo.getPrefixPattern())).build());
        inventory.setItem(48, new ItemBuilder(Material.END_PORTAL_FRAME).setDisplayName(Verany.getPrefix("Loot Boxes", playerInfo.getPrefixPattern())).build());
        inventory.setItem(49, new ItemBuilder(Material.NETHER_STAR).setDisplayName(Verany.getPrefix("Hall of Pain", playerInfo.getPrefixPattern())).build());
        inventory.setItem(50, new ItemBuilder(Material.EXPERIENCE_BOTTLE).setDisplayName(Verany.getPrefix("Daily Reward", playerInfo.getPrefixPattern())).build());
        inventory.setItem(51, new ItemBuilder(Material.ELYTRA).setDisplayName(Verany.getPrefix("Elytra", playerInfo.getPrefixPattern())).build());
        inventory.setItem(52, new ItemBuilder(Material.GOLDEN_BOOTS).setDisplayName(Verany.getPrefix(" Jump and Run", playerInfo.getPrefixPattern())).build());


    }
}
