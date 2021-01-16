package net.verany.hubsystem.commands;

import com.google.common.collect.Lists;
import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.BinaryClient;

import java.util.ArrayList;
import java.util.List;

public class SetupCommand implements CommandExecutor, TabCompleter {
    public SetupCommand(HubSystem hubSystem) {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();

        if (!player.hasPermission("verany.hub.setup")) {
            player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§cDazu hast du keine Berechtigung§8.");
            return false;
        }

        if (args.length == 0) {
            player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Setup");
            player.sendMessage("§a");
            player.sendMessage(" §8× §b/setup §8• §7Zeigt diese Liste");
            player.sendMessage(" §8× §b/setup build §8• §7Bringt dich in den Baumodus");
            player.sendMessage(" §8× §b/setup addbees §8• §7Fügt ein Bienen-Nest hinzu");
            player.sendMessage(" §8× §b/setup setnpc [NPC] §8• §7Setzt einen NPC");
            player.sendMessage(" §8× §b/setup setspawn §8• §7Setzt den Spawnpunkt");
            player.sendMessage(" §8× §b/setup setloc [Location] §8• §7Setzt eine Location");
            player.sendMessage("§b");
        } else if (args.length == 1) {
            String name = args[0];
            switch (name.toLowerCase()) {
                case "setspawn":
                    HubSystem.INSTANCE.getLocationManager().createLocation("spawn", player.getLocation());
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "Der §bSpawn §7wurde gesetzt§8.");
                    break;
                case "addbees":
                    Block targetBlock = player.getTargetBlock(5);
                    if (!targetBlock.getType().equals(Material.BEE_NEST)) {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§cBitte markiere ein Bienennest indem du es direkt ansiehst§8.");
                        return false;
                    }
                    String nestName = "beenest_" + targetBlock.getX() + "_" + targetBlock.getZ();
                    if (HubSystem.INSTANCE.getLocationManager().existLocation(nestName)) {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§cDieses Bienennest wurde bereits gesetzt§8.");
                        return false;
                    }
                    HubSystem.INSTANCE.getLocationManager().createLocation(nestName, targetBlock.getLocation());
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Du hast das §bBienennest §7erfolgreich gesetzt§8.");
                    break;
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("setloc") || args[0].equalsIgnoreCase("setnpc")) {
                String name = args[1];
                HubSystem.INSTANCE.getLocationManager().createLocation(name, player.getLocation());
                player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "Die Location §b" + name + " §7wurde gesetzt§.");
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (strings.length == 1) {
            List<String> arguments = Lists.newArrayList("build", "addbees", "setnpc", "setspawn", "setloc");
            return StringUtil.copyPartialMatches(strings[0], arguments, new ArrayList<>(arguments.size()));
        }
        return new ArrayList<>();
    }
}
