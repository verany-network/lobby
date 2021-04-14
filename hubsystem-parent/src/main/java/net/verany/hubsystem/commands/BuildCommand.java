package net.verany.hubsystem.commands;

import net.verany.api.Verany;
import net.verany.api.command.AbstractCommand;
import net.verany.api.command.CommandEntry;
import net.verany.api.module.VeranyProject;
import org.bukkit.Sound;

public class BuildCommand extends AbstractCommand {

    public BuildCommand(VeranyProject project) {
        super(project);

        Verany.registerCommand(project, new CommandEntry("build", "verany.build", null), (playerInfo, strings) -> {
            playerInfo.playSound(Sound.ENTITY_PLAYER_LEVELUP);
            playerInfo.sendOnServer("Build-1");
        });
    }
}
