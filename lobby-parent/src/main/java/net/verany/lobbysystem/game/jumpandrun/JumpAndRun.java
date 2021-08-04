package net.verany.lobbysystem.game.jumpandrun;

import com.destroystokyo.paper.ParticleBuilder;
import lombok.Getter;
import net.minecraft.core.particles.Particles;
import net.verany.api.Verany;
import net.verany.api.actionbar.NumberActionbar;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.lobbysystem.LobbySystem;
import net.verany.lobbysystem.game.player.IHubPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.FallingBlock;
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
    private FallingBlock fallingBlock;

    public void start(Player player) {
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId(), IPlayerInfo.class).get();
        block = Material.valueOf("ORANGE_CONCRETE");
        woolBlock = Material.valueOf("ORANGE_WOOL");

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

        if(fallingBlock != null)
            fallingBlock.remove();

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

        fallingBlock = nextLocation.getWorld().spawnFallingBlock(nextLocation, nextLocation.getBlock().getBlockData());
        fallingBlock.setGlowing(true);
        fallingBlock.setGravity(false);
        /*ParticleManager particleManager = new ParticleManager(Particles.G, nextLocation.clone().clone().add(0.5, 1, 0.5), true, 0, .1F, 0, 0, 20);
        particleManager.sendPlayer(player);*//*
        new ParticleBuilder(Particle.VILLAGER_HAPPY).color*/
    }

    private Location getNextLocation(double x, double y, double z) {
        return new Location(this.currentLocation.getWorld(), this.currentLocation.getX() + x, this.currentLocation.getY() + y, this.currentLocation.getZ() + z);
    }

    public void stop(Player player) {
        player.teleport(LobbySystem.INSTANCE.getLocationManager().getLocation("hubgames"));
        LobbySystem.INSTANCE.removeMetadata(player, "jump_and_run");
        this.currentLocation.getBlock().setType(Material.AIR);
        this.nextLocation.getBlock().setType(Material.AIR);
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId(), IPlayerInfo.class).get();
        int highScore = Verany.getPlayer(player.getUniqueId(), IHubPlayer.class).getJumpAndRunHighScore();
        if (highScore < currentScore) {
            if (Verany.getPlayer(player.getUniqueId(), IHubPlayer.class).getJumpAndRunHighScore() != 0) {
                int exp = Verany.getRandomNumberBetween(10, 20);
                playerInfo.getLevelObject().addExp(exp);
                playerInfo.addActionbar(new NumberActionbar(playerInfo.getKey("hub.jump_and_run.new_highscore", new Placeholder("%current_score%", currentScore), new Placeholder("%highscore%", highScore)), 2000, highScore));
                playerInfo.addActionbar(new NumberActionbar(playerInfo.getKey("hub.add.exp", new Placeholder("%exp%", exp)), 2000, exp));
            }
            Verany.getPlayer(player.getUniqueId(), IHubPlayer.class).setJumpAndRunHighScore(currentScore);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
        } else
            player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_DESTROY, 1.0F, 1.0F);
        Verany.getPlayer(player.getUniqueId(), IHubPlayer.class).setItems();
    }

    private void removeBlock(Location location) {
        location.getBlock().setType(Material.AIR);
    }

}
