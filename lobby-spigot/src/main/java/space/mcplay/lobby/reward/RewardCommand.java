package space.mcplay.lobby.reward;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import space.mcplay.Core;
import space.mcplay.lobby.LobbyPlugin;
import space.mcplay.plugin.spigot.command.SpigotCommand;

import java.util.List;

public class RewardCommand extends SpigotCommand {

  public RewardCommand() {
    super("reward", "Command to get daily rewards", "", "", "", "");
  }


  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    if (!(commandSender instanceof Player)) return true;

    Player player = (Player) commandSender;
    Core.async(() -> RewardInventory.build(LobbyPlugin.getInstance(), player));
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
    return null;
  }
}
