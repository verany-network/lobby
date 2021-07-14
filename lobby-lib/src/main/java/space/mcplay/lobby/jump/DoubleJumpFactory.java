package space.mcplay.lobby.jump;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class DoubleJumpFactory {

  private static final double HEIGHT = 1.2D, MULTIPLY = 1.6D;

  public static void chargeJump(Player player) {
    switch (player.getGameMode()) {
      case ADVENTURE:
      case SURVIVAL:

        if (((LivingEntity) player).isOnGround() && !player.getAllowFlight()) {
          player.setAllowFlight(true);
          player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.75F, 0.75F);
        }

        break;
      default:
        break;
    }
  }

  public static void executeJump(Player player) {
    player.setAllowFlight(false);
    player.setFlying(false);
    player.setVelocity(player.getLocation().getDirection().multiply(MULTIPLY).setY(HEIGHT));
    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0F, 5.0F);
  }

}
