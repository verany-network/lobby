package space.mcplay.lobby;

import org.bukkit.Bukkit;
import org.bukkit.World;
import space.mcplay.Core;
import space.mcplay.configuration.JsonConfig;
import space.mcplay.hologram.HologramAPI;
import space.mcplay.lobby.holograms.online.OnlineHologram;
import space.mcplay.lobby.holograms.online.TwitterHologram;
import space.mcplay.lobby.item.listener.LobbyItemListener;
import space.mcplay.lobby.listener.*;
import space.mcplay.lobby.reward.RewardCommand;
import space.mcplay.lobby.scoreboard.SidebarExecutor;
import space.mcplay.lobby.time.DaytimeSchedule;
import space.mcplay.location.list.SpigotDataLocationPool;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.scoreboard.ScoreboardAPI;
import team.dotspace.dolphin.api.NodeAPI;

public class LobbyPlugin extends SpigotPlugin {

  private static LobbyPlugin instance;

  public static LobbyPlugin getInstance() {
    return instance;
  }

  private SpigotDataLocationPool locationPool;

  @Override
  public void bootstrap() {
    instance = this;
  }

  @Override
  public void startup() {
    try (JsonConfig jsonConfig = new JsonConfig().readFromFilePath("plugins/locations", "locations.json")) {
      this.locationPool = new SpigotDataLocationPool().fromJsonConfig(jsonConfig);
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    final World world = Bukkit.getWorld("world");

    if (world != null)
      world.setSpawnLocation(this.locationPool.get("spawn"));

    this.getHook().registerListeners(
      new PlayerJoinListener(this), new PlayerQuitListener(this),
      new PlayerMoveListener(this), new EntityListener(this),
      new EntityFoodListener(this), new WorldListener(this),
      new PlayerDoubleJumpListener(this), new PlayerTeleportListener(this),
      new PlayerDropAndPickUpListener(this), new PlayerInteractListener(this),
      new LobbyItemListener(this)
    );

    this.getHook().registerCommands(new RewardCommand());

    //Timer for daytime
    Core.getTimer().schedule(DaytimeSchedule.buildTask(this), 1000, 2000);

    HologramAPI.getInstance().registerHolograms(
      new OnlineHologram(this), new TwitterHologram(this));

    ScoreboardAPI.getInstance().withSidebarContent(new SidebarExecutor());

    NodeAPI.getInstance().updateNode(nodeInfo -> nodeInfo.withMaxClients(128));
    NodeAPI.getInstance().getNodeInfoGroup(NodeAPI.getInstance().getNodeInfo().getDisplayName().toLowerCase());

  }

  @Override
  public void shutdown() {
    instance = null;
  }

  public SpigotDataLocationPool getLocationPool() {
    return this.locationPool;
  }
}
