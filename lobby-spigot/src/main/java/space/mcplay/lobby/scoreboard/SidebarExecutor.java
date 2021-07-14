package space.mcplay.lobby.scoreboard;

import org.bukkit.entity.Player;
import space.mcplay.language.v2.LanguageAPI;
import space.mcplay.language.v2.message.LanguageMessageFactory;
import space.mcplay.language.v2.replacer.MessageReplacer;
import space.mcplay.scoreboard.sidebar.SidebarContent;
import space.mcplay.scoreboard.sidebar.collection.SidebarContentExecutor;

import java.util.Arrays;

/**
 * Created by Daniel Riethmüller on 29.07.2020
 */

public class SidebarExecutor implements SidebarContentExecutor {

  private int global, quickshot, skywarsffa, waterffa;

  public SidebarExecutor() {

  }

  @Override
  public SidebarContent withContent(SidebarContent sidebarContent, Player player) {
    final LanguageMessageFactory factory = LanguageAPI.getInstance().buildMessageFactory(player.getUniqueId());

    sidebarContent.withTitle(factory.getMessage("lobby/scoreboard/title"))
      .withLines(Arrays.asList(factory.getMessageArray("lobby/scoreboard/cloud", new MessageReplacer()
        .addReplacer("GLOBAL", this.convertPlayer(this.global))
        .addReplacer("QUICKSHOT", this.convertPlayer(this.quickshot))
        .addReplacer("SKYWARSFFA", this.convertPlayer(this.skywarsffa))
        .addReplacer("WATERFFA", this.convertPlayer(this.waterffa))
      )));

    return sidebarContent;
  }

  private String convertPlayer(int players) {
    if (players > 99)
      players = 99;

    StringBuilder placeholder = new StringBuilder(Integer.toString(players));

    while (placeholder.length() < 2)
      placeholder.insert(0, "0");

    return placeholder.toString();
  }

}
