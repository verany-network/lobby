package net.verany.hubsystem.listener;

import net.verany.api.Verany;
import net.verany.api.event.AbstractListener;
import net.verany.api.module.VeranyProject;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;

public final class PlayerDeathListener extends AbstractListener {

  public PlayerDeathListener(VeranyProject project) {
    super(project);

    Verany.registerListener(project, EntityDeathEvent.class, event -> {
      if (event.getEntity() instanceof Player player) {
        player.spigot().respawn();
      }
    });
  }
}
