package net.verany.hubsystem.game.jumpandrun;

import lombok.Getter;
import net.minecraft.server.v1_16_R3.Particles;
import net.verany.api.Verany;
import net.verany.api.actionbar.NumberActionbar;
import net.verany.api.particle.ParticleManager;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.game.player.HubPlayer;
import net.verany.hubsystem.game.player.IHubPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Random;

@Getter
public class JumpAndRun {

    private Location currentLocation;
    private Location nextLocation;

    private final Random random = new Random();

    private int currentScore = 0;
    private boolean freeze = true;

    private Material block;
    private Material woolBlock;

    public void start(Player player) {
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
        block = Material.valueOf(Verany.toDyeColor(playerInfo.getPrefixPattern().getColor().getFirstColor()) + "_CONCRETE");
        woolBlock = Material.valueOf(Verany.toDyeColor(playerInfo.getPrefixPattern().getColor().getFirstColor()) + "_WOOL");

        double x = this.random.nextInt(10);
        double z = this.random.nextInt(10);
        this.currentLocation = new Location(player.getWorld(), player.getLocation().getX() + x, 120, player.getLocation().getZ() + z);
        player.teleport(this.currentLocation.clone().add(0.5D, 1.0D, 0.5D));
        nextBlock(player, true);

        freeze = false;
    }

    public void nextBlock(Player player, boolean isStarted) {
        if (!isStarted) {
            currentScore++;
            player.playSound(player.getLocation(), Sound.BLOCK_COMPOSTER_EMPTY, 2F, 0.5F);
            removeBlock(this.currentLocation);
        } else
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);

        this.currentLocation = player.getLocation();
        final int radius = this.random.nextInt(1) + (this.random.nextInt(1) + 2) + (Verany.getRandomNumberBetween(0, 100) < 25 ? 2 : 1);
        final double angle = Math.random() * 3.141592653589793 * 2.0;
        final double x = Math.cos(angle) * radius;
        double y = this.random.nextInt(1);
        if (radius >= 5) {
            y = 0.0;
        }
        final double z = Math.sin(angle) * radius;
        this.nextLocation = getNextLocation(x, y, z);
        double down = nextLocation.getBlockY() < 200 ? 0 : (new Random().nextInt(100) <= 40 || nextLocation.getBlockY() >= 255 ? 3 : 2);

        this.nextLocation.getWorld().refreshChunk(currentLocation.getBlockX(), currentLocation.getBlockZ());
        this.nextLocation.getWorld().refreshChunk(nextLocation.getBlockX(), nextLocation.getBlockZ());

        this.currentLocation.subtract(0.0D, 1.0D, 0.0D).getBlock().setType(block, true);
        this.nextLocation.subtract(0.0D, down, 0.0D).getBlock().setType(woolBlock, true);

        nextLocation.setX(nextLocation.getBlockX());
        nextLocation.setZ(nextLocation.getBlockZ());
        ParticleManager particleManager = new ParticleManager(Particles.HAPPY_VILLAGER, nextLocation.clone().clone().add(0.5, 1, 0.5), true, 0, .1F, 0, 0, 20);
        particleManager.sendPlayer(player);
    }

    private Location getNextLocation(double x, double y, double z) {
        return new Location(this.currentLocation.getWorld(), this.currentLocation.getX() + x, this.currentLocation.getY() + y, this.currentLocation.getZ() + z);
    }

    public void stop(Player player) {
        player.teleport(HubSystem.INSTANCE.getLocationManager().getLocation("hubgames"));
        HubSystem.INSTANCE.removeMetadata(player, "jump_and_run");
        this.currentLocation.getBlock().setType(Material.AIR);
        this.nextLocation.getBlock().setType(Material.AIR);
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
        int highScore = Verany.getPlayer(player.getUniqueId().toString(), IHubPlayer.class).getJumpAndRunHighScore();
        if (highScore < currentScore) {
            if (Verany.getPlayer(player.getUniqueId().toString(), IHubPlayer.class).getJumpAndRunHighScore() != 0) {
                int exp = Verany.getRandomNumberBetween(10, 20);
                playerInfo.getLevelObject().addExp(exp);
                playerInfo.addActionbar(new NumberActionbar(playerInfo.getKey("hub.jump_and_run.new_highscore", new Placeholder("%current_score%", currentScore), new Placeholder("%highscore%", highScore)), 2000, highScore));
                playerInfo.addActionbar(new NumberActionbar(playerInfo.getKey("hub.add.exp", new Placeholder("%exp%", exp)), 2000, exp));
            }
            Verany.getPlayer(player.getUniqueId().toString(), IHubPlayer.class).setJumpAndRunHighScore(currentScore);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        } else
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1.0F, 1.0F);
        Verany.getPlayer(player.getUniqueId().toString(), IHubPlayer.class).setItems();
    }

    private void removeBlock(Location location) {
        location.getBlock().setType(Material.AIR);
    }

}
