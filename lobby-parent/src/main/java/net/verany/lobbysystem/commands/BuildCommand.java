package net.verany.lobbysystem.commands;

import net.verany.api.Verany;
import net.verany.api.command.AbstractCommand;
import net.verany.api.command.CommandEntry;
import net.verany.api.module.VeranyPlugin;
import net.verany.api.module.VeranyProject;
import org.bukkit.Sound;

public class BuildCommand extends AbstractCommand {

    public BuildCommand(VeranyPlugin project) {
        super(project);

        Verany.registerCommand(project, new CommandEntry("build", "verany.build", null), (playerInfo, strings) -> {
            playerInfo.playSound(Sound.ENTITY_PLAYER_LEVELUP);
            playerInfo.sendOnServer("Build-1");
        });
    }
}
