package net.verany.lobbysystem.flagwars.voting;

import net.verany.api.Verany;
import net.verany.api.enumhelper.EnumHelper;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.module.VeranyPlugin;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.lobbysystem.flagwars.LobbyFlagWars;
import net.verany.lobbysystem.flagwars.VariantType;
import net.verany.lobbysystem.flagwars.map.data.MapData;
import net.verany.lobbysystem.flagwars.player.IFlagWarsPlayer;
import net.verany.lobbysystem.flagwars.round.AbstractRound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class VotingInventory {

    private final VeranyPlugin plugin;
    private final AbstractRound round;
    private final BukkitRunnable task;

    private VotingCategory currentCategory = VotingCategory.TEAMS;
    private int currentTime = 10;

    public VotingInventory(VeranyPlugin plugin, AbstractRound round, Runnable onFinish) {
        this.plugin = plugin;
        this.round = round;
        this.currentCategory = round.getVariant().getVariantType().equals(VariantType.SOLO) ? VotingCategory.MAPS : VotingCategory.TEAMS;

        this.task = new BukkitRunnable() {
            @Override
            public void run() {
                currentTime--;
                if (currentTime == 0 || hasAllVoted()) {
                    if (currentCategory.equals(VotingCategory.COBWEB)) {
                        task.cancel();
                        onFinish.run();
                        return;
                    }
                    currentCategory = getNextCategory();
                    if (hasAllVoted()) {
                        while (hasAllVoted()) {
                            currentCategory = VotingCategory.valueOf(EnumHelper.INSTANCE.getNextEnumValue(VotingCategory.class, currentCategory));
                        }
                        return;
                    }
                    currentTime = 10;
                    createInventory();
                    return;
                }
                for (Player bukkitPlayer : round.getBukkitPlayers()) {
                    bukkitPlayer.getOpenInventory().setItem(4, new ItemBuilder(Material.CLOCK).setAmount(currentTime).setDisplayName(Verany.getPlayer(bukkitPlayer).getKey("flagwars.lobby.time_remaining")).build());
                }
            }
        };

        createInventory();

    }

    private void createInventory() {
        createInventory(null);
    }

    private void createInventory(Player player) {
        switch (currentCategory) {
            case TEAMS: {
                for (Player bukkitPlayer : round.getBukkitPlayers()) {
                    if (player != null && !bukkitPlayer.equals(player)) continue;
                    IPlayerInfo playerInfo = Verany.getPlayer(bukkitPlayer);
                    IFlagWarsPlayer lobbyPlayer = playerInfo.getPlayer(IFlagWarsPlayer.class);

                    /*Inventory inventory = InventoryBuilder.builder().size(9).title("Teams").onClick(event -> {
                        if (event.getCurrentItem().getType().name().endsWith("_BANNER")) {
                            AbstractGameTeam team = GameTeamWrapper.getTeamByKey(event.getCurrentItem().getType().name().split("_")[0].toLowerCase());
                            //playerInfo.getPlayer(IFlagWarsPlayer.class).getTeamObject().setTeam(team);
                            createInventory(player);
                        }
                    }).build().createAndOpen(bukkitPlayer);
                    for (int i = 0; i < round.getVariant().getTeams(); i++) {
                        AbstractGameTeam team = GameTeamWrapper.VALUES.get(i);
                        inventory.setItem(i, new ItemBuilder(Material.valueOf(team.getDyeColor().name() + "_BANNER")).build());
                    }*/
                }
                break;
            }
            case MAPS: {
                for (Player bukkitPlayer : round.getBukkitPlayers()) {
                    if (player != null && !bukkitPlayer.equals(player)) continue;
                    IPlayerInfo playerInfo = Verany.getPlayer(bukkitPlayer);
                    IFlagWarsPlayer lobbyPlayer = playerInfo.getPlayer(IFlagWarsPlayer.class);

                    Inventory inventory = InventoryBuilder.builder().title(playerInfo.getKey("flagwars.voting.map.title")).inventoryType(InventoryType.HOPPER).onClick(event -> {
                        MapData mapData = LobbyFlagWars.INSTANCE.getMapObject().getMap(event.getCurrentItem().getType(), round.getVariant());
                        if (mapData == null) return;
                        if (round.getMapVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), mapData)) return;
                        round.getMapVoting().vote(bukkitPlayer.getUniqueId(), mapData);
                        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.4F);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(player), 2);
                    }).onClose(event -> {
                        if (round.getTargetService() == null && bukkitPlayer.getOpenInventory().getTitle().equals(playerInfo.getKey("flagwars.voting.map.title")))
                            Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(bukkitPlayer), 2);
                    }).build().createAndOpen(bukkitPlayer);

                    List<MapData> maps = round.getMapVoting().getValues();

                    if (lobbyPlayer.getVotingValue("maps") != null && !round.getMapVoting().hasVoted(bukkitPlayer.getUniqueId())) {
                        List<String> preferredMap = new ArrayList<>(lobbyPlayer.getVotingValue("maps"));
                        for (MapData map : maps) {
                            if (preferredMap.contains(map.getName())) {
                                round.getMapVoting().vote(bukkitPlayer.getUniqueId(), map);
                                break;
                            }
                        }
                    }

                    for (int i = 0; i < maps.size(); i++) {
                        MapData map = maps.get(i);
                        int votes = round.getMapVoting().getVotes(map);

                        int winChance = (int) playerInfo.getPlayer(IFlagWarsPlayer.class).getAverageWinChance(map.getName());

                        inventory.setItem(i, new ItemBuilder(map.getMaterial()).setGlow(round.getMapVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), map)).setAmount(votes == 0 ? 1 : votes).setDisplayName(playerInfo.getKey("flagwars.lobby.map.voting", new Placeholder("%name%", map.getName()))).addLoreArray(playerInfo.getKey("flagwars.lobby.voting.map.lore", new Placeholder("%votes%", votes), new Placeholder("%winChance%", winChance))).build());
                    }
                    inventory.setItem(4, new ItemBuilder(Material.CLOCK).setAmount(currentTime).setDisplayName(playerInfo.getKey("flagwars.lobby.time_remaining")).build());
                }
                break;
            }
            case DIAMOND: {
                for (Player bukkitPlayer : round.getBukkitPlayers()) {
                    if (player != null && !bukkitPlayer.equals(player)) continue;
                    IPlayerInfo playerInfo = Verany.getPlayer(bukkitPlayer);
                    IFlagWarsPlayer lobbyPlayer = playerInfo.getPlayer(IFlagWarsPlayer.class);
                    if (lobbyPlayer.getVotingValue("diamond") != null && !round.getDiamondVoting().hasVoted(bukkitPlayer.getUniqueId()))
                        round.getDiamondVoting().vote(bukkitPlayer.getUniqueId(), lobbyPlayer.getVotingValue("diamond"));

                    Inventory inventory = InventoryBuilder.builder().title(playerInfo.getKey("flagwars.voting.diamond.title")).inventoryType(InventoryType.HOPPER).onClick(event -> {
                        boolean value = event.getCurrentItem().getType().equals(Material.LIME_DYE);
                        if (round.getDiamondVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), value)) return;
                        round.getDiamondVoting().vote(bukkitPlayer.getUniqueId(), value);
                        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.4F);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(player), 2);
                    }).onClose(event -> {
                        if (round.getTargetService() == null && bukkitPlayer.getOpenInventory().getTitle().equals(playerInfo.getKey("flagwars.voting.diamond.title")))
                            Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(bukkitPlayer), 2);
                    }).build().createAndOpen(bukkitPlayer);
                    inventory.setItem(4, new ItemBuilder(Material.CLOCK).setAmount(currentTime).setDisplayName(playerInfo.getKey("flagwars.lobby.time_remaining")).build());

                    int votesEnabled = round.getDiamondVoting().getVotes(true);
                    int votesDisabled = round.getDiamondVoting().getVotes(false);

                    inventory.setItem(1, new ItemBuilder(Material.LIME_DYE).setDisplayName(playerInfo.getKey("flagwars.lobby.enabled")).addLoreArray(" ", "§7Votes§8: §b" + votesEnabled).setGlow(round.getDiamondVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), true)).setAmount(votesEnabled == 0 ? 1 : votesEnabled).build());
                    inventory.setItem(2, new ItemBuilder(Material.GRAY_DYE).setDisplayName(playerInfo.getKey("flagwars.lobby.disabled")).addLoreArray(" ", "§7Votes§8: §b" + votesDisabled).setGlow(round.getDiamondVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), false)).setAmount(votesDisabled == 0 ? 1 : votesDisabled).build());
                }
                break;
            }
            case BOW: {
                for (Player bukkitPlayer : round.getBukkitPlayers()) {
                    if (player != null && !bukkitPlayer.equals(player)) continue;
                    IPlayerInfo playerInfo = Verany.getPlayer(bukkitPlayer);
                    IFlagWarsPlayer lobbyPlayer = playerInfo.getPlayer(IFlagWarsPlayer.class);
                    if (lobbyPlayer.getVotingValue("bow") != null && !round.getBowVoting().hasVoted(bukkitPlayer.getUniqueId()))
                        round.getBowVoting().vote(bukkitPlayer.getUniqueId(), lobbyPlayer.getVotingValue("bow"));

                    Inventory inventory = InventoryBuilder.builder().title(playerInfo.getKey("flagwars.voting.bow.title")).inventoryType(InventoryType.HOPPER).onClick(event -> {
                        boolean value = event.getCurrentItem().getType().equals(Material.LIME_DYE);
                        if (round.getBowVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), value)) return;
                        round.getBowVoting().vote(bukkitPlayer.getUniqueId(), value);
                        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.4F);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(player), 2);
                    }).onClose(event -> {
                        if (round.getTargetService() == null && bukkitPlayer.getOpenInventory().getTitle().equals(playerInfo.getKey("flagwars.voting.bow.title")))
                            Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(bukkitPlayer), 2);
                    }).build().createAndOpen(bukkitPlayer);
                    inventory.setItem(4, new ItemBuilder(Material.CLOCK).setAmount(currentTime).setDisplayName(playerInfo.getKey("flagwars.lobby.time_remaining")).build());

                    int votesEnabled = round.getBowVoting().getVotes(true);
                    int votesDisabled = round.getBowVoting().getVotes(false);

                    inventory.setItem(1, new ItemBuilder(Material.LIME_DYE).setDisplayName(playerInfo.getKey("flagwars.lobby.enabled")).addLoreArray(" ", "§7Votes§8: §b" + votesEnabled).setGlow(round.getBowVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), true)).setAmount(votesEnabled == 0 ? 1 : votesEnabled).build());
                    inventory.setItem(2, new ItemBuilder(Material.GRAY_DYE).setDisplayName(playerInfo.getKey("flagwars.lobby.disabled")).addLoreArray(" ", "§7Votes§8: §b" + votesDisabled).setGlow(round.getBowVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), false)).setAmount(votesDisabled == 0 ? 1 : votesDisabled).build());
                }
                break;
            }
            case ITEM_DROPS: {
                for (Player bukkitPlayer : round.getBukkitPlayers()) {
                    if (player != null && !bukkitPlayer.equals(player)) continue;
                    IPlayerInfo playerInfo = Verany.getPlayer(bukkitPlayer);
                    IFlagWarsPlayer lobbyPlayer = playerInfo.getPlayer(IFlagWarsPlayer.class);
                    if (lobbyPlayer.getVotingValue("item_drops") != null && !round.getItemDropVoting().hasVoted(bukkitPlayer.getUniqueId()))
                        round.getItemDropVoting().vote(bukkitPlayer.getUniqueId(), lobbyPlayer.getVotingValue("item_drops"));

                    Inventory inventory = InventoryBuilder.builder().title(playerInfo.getKey("flagwars.voting.item_drops.title")).inventoryType(InventoryType.HOPPER).onClick(event -> {
                        boolean value = event.getCurrentItem().getType().equals(Material.LIME_DYE);
                        if (round.getItemDropVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), value)) return;
                        round.getItemDropVoting().vote(bukkitPlayer.getUniqueId(), value);
                        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.4F);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(player), 2);
                    }).onClose(event -> {
                        if (round.getTargetService() == null && bukkitPlayer.getOpenInventory().getTitle().equals(playerInfo.getKey("flagwars.voting.item_drops.title")))
                            Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(bukkitPlayer), 2);
                    }).build().createAndOpen(bukkitPlayer);
                    inventory.setItem(4, new ItemBuilder(Material.CLOCK).setAmount(currentTime).setDisplayName(playerInfo.getKey("flagwars.lobby.time_remaining")).build());

                    int votesEnabled = round.getItemDropVoting().getVotes(true);
                    int votesDisabled = round.getItemDropVoting().getVotes(false);

                    inventory.setItem(1, new ItemBuilder(Material.LIME_DYE).setDisplayName(playerInfo.getKey("flagwars.lobby.enabled")).addLoreArray(" ", "§7Votes§8: §b" + votesEnabled).setGlow(round.getItemDropVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), true)).setAmount(votesEnabled == 0 ? 1 : votesEnabled).build());
                    inventory.setItem(2, new ItemBuilder(Material.GRAY_DYE).setDisplayName(playerInfo.getKey("flagwars.lobby.disabled")).addLoreArray(" ", "§7Votes§8: §b" + votesDisabled).setGlow(round.getItemDropVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), false)).setAmount(votesDisabled == 0 ? 1 : votesDisabled).build());
                }
                break;
            }
            case COBWEB: {
                for (Player bukkitPlayer : round.getBukkitPlayers()) {
                    if (player != null && !bukkitPlayer.equals(player)) continue;
                    IPlayerInfo playerInfo = Verany.getPlayer(bukkitPlayer);
                    IFlagWarsPlayer lobbyPlayer = playerInfo.getPlayer(IFlagWarsPlayer.class);
                    if (lobbyPlayer.getVotingValue("cobweb") != null && !round.getCobwebVoting().hasVoted(bukkitPlayer.getUniqueId()))
                        round.getCobwebVoting().vote(bukkitPlayer.getUniqueId(), lobbyPlayer.getVotingValue("cobweb"));

                    Inventory inventory = InventoryBuilder.builder().title(playerInfo.getKey("flagwars.voting.cobweb.title")).inventoryType(InventoryType.HOPPER).onClick(event -> {
                        boolean value = event.getCurrentItem().getType().equals(Material.LIME_DYE);
                        if (round.getCobwebVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), value)) return;
                        round.getCobwebVoting().vote(bukkitPlayer.getUniqueId(), value);
                        bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.4F);
                        Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(player), 2);
                    }).onClose(event -> {
                        if (round.getTargetService() == null && bukkitPlayer.getOpenInventory().getTitle().equals(playerInfo.getKey("flagwars.voting.cobweb.title")))
                            Bukkit.getScheduler().runTaskLater(plugin, () -> createInventory(bukkitPlayer), 2);
                    }).build().createAndOpen(bukkitPlayer);
                    inventory.setItem(4, new ItemBuilder(Material.CLOCK).setAmount(currentTime).setDisplayName(playerInfo.getKey("flagwars.lobby.time_remaining")).build());

                    int votesEnabled = round.getCobwebVoting().getVotes(true);
                    int votesDisabled = round.getCobwebVoting().getVotes(false);

                    inventory.setItem(1, new ItemBuilder(Material.LIME_DYE).setDisplayName(playerInfo.getKey("flagwars.lobby.enabled")).addLoreArray(" ", "§7Votes§8: §b" + votesEnabled).setGlow(round.getCobwebVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), true)).setAmount(votesEnabled == 0 ? 1 : votesEnabled).build());
                    inventory.setItem(2, new ItemBuilder(Material.GRAY_DYE).setDisplayName(playerInfo.getKey("flagwars.lobby.disabled")).addLoreArray(" ", "§7Votes§8: §b" + votesDisabled).setGlow(round.getCobwebVoting().hasVotedForValue(bukkitPlayer.getUniqueId(), false)).setAmount(votesDisabled == 0 ? 1 : votesDisabled).build());


                }
                break;
            }
        }
    }

    public void run() {
        task.runTaskTimer(plugin, 0, 20);
    }

    private boolean hasAllVoted() {
        return hasAllVoted(currentCategory);
    }

    private boolean hasAllVoted(VotingCategory currentCategory) {
        boolean toReturn = true;
        for (Player bukkitPlayer : round.getBukkitPlayers()) {
            if (currentCategory.getVotingKey() == null) {
                if (round.getTeamObject().getTeam(bukkitPlayer.getUniqueId()) == null) {
                    toReturn = false;
                    break;
                }
                continue;
            }
            Optional<FlagWarsVoting<?>> voting = round.getVotings().stream().filter(flagWarsVoting -> flagWarsVoting.getKey().equals(currentCategory.getVotingKey())).findFirst();
            if (voting.isEmpty() || !voting.get().hasVoted(bukkitPlayer.getUniqueId())) {
                toReturn = false;
                break;
            }
        }
        return toReturn;
    }

    private VotingCategory getNextCategory() {
        return VotingCategory.valueOf(Verany.getNextEnumValue(VotingCategory.class, currentCategory));
    }

}