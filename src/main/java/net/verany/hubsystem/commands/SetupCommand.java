package net.verany.hubsystem.commands;

import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetupCommand implements CommandExecutor {
    public SetupCommand(HubSystem hubSystem) {

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
        if(args.length == 0) {
            if(player.hasPermission("verany.hub.setup")) {
                player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Setup");
                player.sendMessage("§a");
                player.sendMessage(" §8× §b/setup §8• §7Zeigt diese Liste");
                player.sendMessage(" §8× §b/setup build §8• §7Bringt dich in den Baumodus");
                player.sendMessage(" §8× §b/setup addbees §8• §7Fügt ein Bienen-Nest hinzu");
                player.sendMessage(" §8× §b/setup setnpc [NPC] §8• §7Setzt einen NPC");
                player.sendMessage(" §8× §b/setup setspawn §8• §7Setzt den Spawnpunkt");
                player.sendMessage(" §8× §b/setup setloc [Location] §8• §7Setzt eine Location");
                player.sendMessage("§b");
            } else {
                player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§cDazu hast du keine Berechtigung§8.");
            }
        } else if(args.length == 1) {
            String name = args[0];
            switch (name.toLowerCase()) {
                case "setspawn":
                    HubSystem.INSTANCE.getLocationManager().createLocation("spawn", player.getLocation());
                    player.sendMessage("spawn ist gesetzt bro");
                    break;
            }
        } else if(args.length == 2) {
            if (args[0].equalsIgnoreCase("setloc")) {
                String name = args[1];
                HubSystem.INSTANCE.getLocationManager().createLocation(name, player.getLocation());
                player.sendMessage(name + " ist gesetzt bro");
            }
        }
        return false;
    }
}
