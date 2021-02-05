package net.verany.hubsystem.commands;

import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ToggleRankCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) sender;
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
        if(player.hasPermission("verany.hub.togglerank")) {
            if(args.length == 0) {
                    player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Verwende bitte §b/togglerank [VIP_PLUS/VIP/PLAYER]§8.");
                } else {
                    if(args.length == 1) {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§7Das ändern deines Ranges ist derzeit leider nicht möglich§8.");
                    } else {
                        player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§cDeine Eingabe ist leider falsch gewesen§8.");
                    }
                }
            } else {
            player.sendMessage(playerInfo.getPrefix(HubSystem.INSTANCE.getModule()) + "§cDazu hast du keine Berechtigung§8.");
        }
        return false;
    }
}
