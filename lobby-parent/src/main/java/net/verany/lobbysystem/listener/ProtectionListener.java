package net.verany.lobbysystem.listener;

import net.verany.api.Verany;
import net.verany.api.event.AbstractListener;
import net.verany.api.event.events.PlayerAfkEvent;
import net.verany.api.event.events.PlayerLanguageUpdateEvent;
import net.verany.api.event.events.PlayerPrefixUpdateEvent;
import net.verany.api.module.VeranyPlugin;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.player.afk.IAFKObject;
import net.verany.lobbysystem.LobbySystem;
import net.verany.lobbysystem.game.LobbyGame;
import net.verany.lobbysystem.game.VeranyGame;
import net.verany.lobbysystem.game.inventory.GameInventory;
import net.verany.lobbysystem.game.jumpandrun.JumpAndRun;
import net.verany.lobbysystem.game.player.IHubPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.Vector;

public class ProtectionListener extends AbstractListener {

    public ProtectionListener(VeranyPlugin project) {
        super(project);

        Verany.registerListener(project, BlockBreakEvent.class, event -> event.setCancelled(true));
        Verany.registerListener(project, BlockPlaceEvent.class, event -> event.setCancelled(true));
        Verany.registerListener(project, PlayerSwapHandItemsEvent.class, event -> event.setCancelled(true));
        Verany.registerListener(project, InventoryClickEvent.class, event -> event.setCancelled(true));
        Verany.registerListener(project, FoodLevelChangeEvent.class, event -> event.setCancelled(true));
        Verany.registerListener(project, EntityDamageEvent.class, event -> event.setCancelled(true));
        Verany.registerListener(project, PlayerDropItemEvent.class, event -> event.setCancelled(true));
        Verany.registerListener(project, WeatherChangeEvent.class, event -> event.setCancelled(true));

        Verany.registerListener(project, PlayerInteractAtEntityEvent.class, event -> {
            event.setCancelled(true);
            if (event.getRightClicked() instanceof ArmorStand) {
                Player player = event.getPlayer();
                if (player.getInventory().getItemInMainHand().getType().equals(Material.NAME_TAG)) return;
                IPlayerInfo playerInfo = Verany.getPlayer(player);
                IHubPlayer hubPlayer = playerInfo.getPlayer(IHubPlayer.class);
                if (event.getRightClicked().hasMetadata("hubGame")) {
                    LobbyGame hubGame = (LobbyGame) event.getRightClicked().getMetadata("hubGame").get(0).value();
                    if (hubGame == null) return;
                    switch (hubGame) {
                        case ELYTRA -> hubPlayer.startElytra();
                        case JUMPANDRUN -> {
                            JumpAndRun jumpAndRun = new JumpAndRun();
                            jumpAndRun.start(player);
                            LobbySystem.INSTANCE.setMetadata(player, "jump_and_run", jumpAndRun);
                            player.getInventory().clear();
                            player.setWalkSpeed(0.2F);
                            player.setAllowFlight(false);
                        }
                    }
                    return;
                }
                if (event.getRightClicked().hasMetadata("veranyGame")) {
                    VeranyGame veranyGame = (VeranyGame) event.getRightClicked().getMetadata("veranyGame").get(0).value();
                    if (veranyGame == null) return;
                    /*switch (veranyGame) {
                        case FLAGWARS: {
                            new GameInventory(player, veranyGame).setItems();
                            break;
                        }
                        case BINGO: {
                            Map<ServiceInfoSnapshot, List<Document>> rounds = new HashMap<>();

                            try {
                                for (ServiceInfoSnapshot cloudService : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync("Bingo").get()) {
                                    if (!cloudService.isConnected() || !cloudService.getProperties().contains("round_data"))
                                        continue;
                                    String roundData = cloudService.getProperties().getString("round_data");
                                    List<Document> documents = Verany.GSON.fromJson(roundData, ServerRoundData.class).getDocuments();
                                    documents.removeIf(document -> !document.containsKey("gameState") || (document.containsKey("gameState") && !document.getString("gameState").equalsIgnoreCase("WAITING")));
                                    rounds.put(cloudService, documents);
                                }
                                ServiceInfoSnapshot service = rounds.keySet().stream().findAny().orElse(null);
                                if (service == null) return;

                                List<Document> documents = rounds.get(service);
                                Document round = documents.get(new Random().nextInt(documents.size()));
                                String id = round.getString("id");

                                ICloudPlayer cloudPlayer = playerInfo.getCloudPlayer();
                                cloudPlayer.getProperties().append("round-id", id);
                                CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).updateOnlinePlayer(cloudPlayer);

                                playerInfo.playSound(Sound.ENTITY_PLAYER_LEVELUP);
                                playerInfo.sendOnServer(service.getServiceId().getName());
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }*/
                    new GameInventory(player, veranyGame).setItems();
                    return;
                }
            }
        });

        Verany.registerListener(project, PlayerInteractEvent.class, event -> {
            Player player = event.getPlayer();

            event.setCancelled(true);

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR))
                if (event.getItem() != null && player.hasMetadata("elytra") && event.getItem().getType().equals(Material.FIREWORK_ROCKET)) {
                    Bukkit.getScheduler().runTaskLater(LobbySystem.INSTANCE, () -> Verany.getPlayer(player.getUniqueId(), IHubPlayer.class).setFirework(false), 2);
                    event.setCancelled(false);
                    return;
                }

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) && player.getInventory().getItemInMainHand().getType().equals(Material.TRIDENT))
                event.setCancelled(false);
        });

        Verany.registerListener(project, InventoryCloseEvent.class, event -> {
            Player player = (Player) event.getPlayer();
            if (player.hasMetadata("inventory")) {
                player.removeMetadata("inventory", LobbySystem.INSTANCE);
            }
        });

        Verany.registerListener(project, PlayerLanguageUpdateEvent.class, event -> {
            Player player = event.getPlayer();
            if (!player.hasMetadata("profile.category"))
                Verany.getPlayer(player).getPlayer(IHubPlayer.class).setItems();
        });

        Verany.registerListener(project, PlayerPrefixUpdateEvent.class, event -> {
            Player player = event.getPlayer();
            IPlayerInfo playerInfo = Verany.getPlayer(player);
            BossBar bar = playerInfo.getPlayer(IHubPlayer.class).getBossBar();
            bar.setColor(toBarColor(ChatColor.valueOf(event.getNewPattern().getColor().firstColor())));
            if (!player.hasMetadata("profile.category"))
                playerInfo.getPlayer(IHubPlayer.class).setItems();
        });

        Verany.registerListener(project, PlayerAfkEvent.class, event -> {
            Player player = event.getPlayer();
            try {
                IPlayerInfo playerInfo = Verany.getPlayer(player);

                if (playerInfo.getAfkObject().isAfk()) {
                    if (player.hasMetadata("jump_and_run")) {
                        playerInfo.getAfkObject().disableAfkCheck(IAFKObject.CheckType.MOVE);
                        JumpAndRun jumpAndRun = (JumpAndRun) player.getMetadata("jump_and_run").get(0).value();
                        jumpAndRun.stop(player);
                        playerInfo.getAfkObject().enableAfkCheck(IAFKObject.CheckType.MOVE);
                    }
                }
            } catch (Exception ignore) {
            }
        });

        Verany.registerListener(project, InventoryCloseEvent.class, event -> {
            Player player = (Player) event.getPlayer();
            if (player.hasMetadata("profile.category.")) {
                player.getInventory().clear();
                Verany.getPlayer(player.getUniqueId(), IHubPlayer.class).setItems();
                LobbySystem.INSTANCE.removeMetadata(player, "profile.category.");
                //Verany.getPlayer(player.getUniqueId()).playSound(VeranySound.INVENTORY_CLOSE);
            }
        });

        Verany.registerListener(project, PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();

            if (player.hasMetadata("jump_and_run")) {
                Block underBlock = player.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
                JumpAndRun jumpAndRun = (JumpAndRun) player.getMetadata("jump_and_run").get(0).value();
                if (jumpAndRun.isFreeze()) {
                    int movX = event.getFrom().getBlockX() - event.getTo().getBlockX();
                    int movZ = event.getFrom().getBlockZ() - event.getTo().getBlockZ();
                    if ((Math.abs(movX) > 0.5) || (Math.abs(movZ) > 0.5))
                        player.teleport(event.getFrom());
                    return;
                }
                if (underBlock.getLocation().getBlockX() == jumpAndRun.getNextLocation().getBlockX() && underBlock.getLocation().getBlockY() == jumpAndRun.getNextLocation().getBlockY() && underBlock.getLocation().getBlockZ() == jumpAndRun.getNextLocation().getBlockZ())
                    jumpAndRun.nextBlock(player, false);
                if (player.getLocation().getY() < jumpAndRun.getCurrentLocation().getY() - 1) {
                    jumpAndRun.stop(player);
                    player.setWalkSpeed(0.3F);
                    player.setAllowFlight(true);
                }
                return;
            }

            if (player.getLocation().getBlockY() <= 0)
                player.teleport(LobbySystem.INSTANCE.getLocationManager().getLocation("spawn"));

            if (player.getGameMode().equals(GameMode.ADVENTURE)) {
                if (player.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR) {
                    player.setAllowFlight(true);
                    player.setFlying(false);
                }
            }
            if (player.hasMetadata("elytra") && (System.currentTimeMillis() > player.getMetadata("elytra").get(0).asLong()))
                if (player.isOnGround() || player.getLocation().getBlock().isLiquid()) {
                    Verany.getPlayer(player.getUniqueId(), IHubPlayer.class).resetElytra();
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1.5F);
                }
            if (player.getLocation().getBlock().isLiquid()) {
                if (!player.hasMetadata("liquid") && player.getInventory().contains(Material.TRIDENT)) {
                    LobbySystem.INSTANCE.setMetadata(player, "liquid", true);
                    Verany.getPlayer(player.getUniqueId(), IHubPlayer.class).setItems();
                }
            } else {
                if (player.hasMetadata("liquid") && player.getInventory().contains(Material.TRIDENT)) {
                    LobbySystem.INSTANCE.removeMetadata(player, "liquid");
                    Verany.getPlayer(player.getUniqueId(), IHubPlayer.class).setItems();
                }
            }
        });

        Verany.registerListener(project, ProjectileHitEvent.class, event -> {
            if (event.getEntity() instanceof Trident) {
                if (event.getEntity().getShooter() instanceof Player) {
                    Player shooter = (Player) event.getEntity().getShooter();
                    Location location = event.getEntity().getLocation();
                    location.setPitch(shooter.getLocation().getPitch());
                    location.setYaw(shooter.getLocation().getYaw());
                    event.getEntity().remove();
                    shooter.teleport(location.clone().add(0, 0.2, 0));
                    Verany.getPlayer(shooter.getUniqueId(), IHubPlayer.class).setItems();
                    //Verany.PROFILE_OBJECT.getPlayer(shooter.getUniqueId()).get().playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
        });

        Verany.registerListener(project, PlayerToggleFlightEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.getGameMode().equals(GameMode.ADVENTURE)) {
                event.setCancelled(true);
                player.setAllowFlight(false);
                player.setFlying(false);
                Vector vector = player.getLocation().getDirection().multiply(1.3).setY(1);
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 3, 1);
                player.setVelocity(vector);
            }
        });

        /*Verany.registerListener(project, NPCInteractEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.hasMetadata("npc.waiting")) {
                long time = player.getMetadata("npc.waiting").get(0).asLong();
                if (time > System.currentTimeMillis()) return;
            }
            LobbySystem.INSTANCE.setMetadata(player, "npc.waiting", System.currentTimeMillis() + 500);
            VeranyGame game = VeranyGame.valueOf(event.getNpc().getName().toUpperCase());
            new GameInventory(player, game).setItems();
        });*/
    }

    private BarColor toBarColor(ChatColor color) {
        try {
            return BarColor.valueOf(color.name().replace("DARK_", "").replace("LIGHT_", ""));
        } catch (Exception e) {
            return BarColor.BLUE;
        }
    }
}
