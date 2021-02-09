package net.verany.hubsystem.utils.actionbar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.setting.SettingWrapper;
import net.verany.api.settings.AbstractSetting;
import net.verany.api.task.AbstractTask;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.player.HubPlayer;
import net.verany.hubsystem.utils.player.jump.JumpAndRun;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActionbarTask extends AbstractTask {

    private final AbstractSetting<Long> timeSettings = new SettingWrapper<>("time", "temp_hub", Long.class, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(7), Material.AIR);
    private final AbstractSetting<Integer> countSetting = new SettingWrapper<>("count", "temp_hub", Integer.class, 0, Material.AIR);
    private final AbstractSetting<Integer> messageCountSetting = new SettingWrapper<>("messageCount", "temp_hub", Integer.class, 0, Material.AIR);

    public ActionbarTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        for (IPlayerInfo onlinePlayer : Verany.getOnlinePlayers()) {
            Player player = onlinePlayer.getPlayer();
            if (!player.isOnline()) continue;

            if (player.hasMetadata("jump_and_run")) {
                JumpAndRun jumpAndRun = (JumpAndRun) player.getMetadata("jump_and_run").get(0).value();
                onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.jump_and_run.actionbar", new Placeholder("%current_score%", jumpAndRun.getCurrentScore()), new Placeholder("%highscore%", Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).getJumpAndRunHighScore())));
                continue;
            }

            List<ActionbarCategory> categories = getAvailableCategories(player);

            long time = onlinePlayer.getTempSetting(timeSettings);
            int count = onlinePlayer.getTempSetting(countSetting);
            int messageCount = onlinePlayer.getTempSetting(messageCountSetting);
            int maxMessages;

            ActionbarCategory category = categories.get(count);
            switch (category) {
                case SERVER:
                    maxMessages = 1;
                    try {
                        // "§7TPS§8: §b" + Verany.round(Bukkit.getTPS()[0]) + " §7Memory§8: §b" + -1 + " / " + -1 + " " + "MB §7CPU§8: §b" + -1 + "%"
                        onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar.server", new Placeholder("%tps%", Verany.round(Bukkit.getTPS()[0]))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case REPORTS:
                    maxMessages = 1;
                    int reports = Verany.getRandomNumberBetween(0, 124);
                    onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar.reports", new Placeholder("%amount%", (reports < 5 ? "§a" + reports : reports < 10 ? "§e" + reports : "§c" + reports))));
                    break;
                case SUPPORT:
                    maxMessages = 1;
                    int support = -1;
                    onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar.support", new Placeholder("%amount%", (support < 5 ? "§a" + support : support < 10 ? "§e" + support : "§c" + support))));
                    break;
                case LEVEL:
                    maxMessages = 1;

                    onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar." + category.name().toLowerCase(), new Placeholder("%current_level%", 0), new Placeholder("%progress_bar%", ""), new Placeholder("%next_level%", 1), new Placeholder("%exp%", 0), new Placeholder("%max_exp%", 100)));
                    break;
                default:
                    String[] messages = onlinePlayer.getKeyArray("hub.actionbar." + category.name().toLowerCase(), '~');
                    maxMessages = messages.length;
                    onlinePlayer.setDefaultActionbar(messages[messageCount]);
                    break;
            }

            if (System.currentTimeMillis() < time) continue;
            if (!onlinePlayer.getActionbarQueue().isEmpty()) continue;
            onlinePlayer.setTempSetting(timeSettings, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(7));

            messageCount++;
            if (messageCount == maxMessages) {
                messageCount = 0;
                count++;
                if (count == categories.size())
                    count = 0;
                onlinePlayer.setTempSetting(countSetting, count);
            }
            onlinePlayer.setTempSetting(messageCountSetting, messageCount);
        }
    }

    private List<ActionbarCategory> getAvailableCategories(Player player) {
        List<ActionbarCategory> toReturn = new ArrayList<>();
        for (ActionbarCategory value : ActionbarCategory.values()) {
            boolean hasAnyPermission = false;
            for (String permission : value.getPermissions()) {
                if (permission.equals("") || player.hasPermission(permission)) {
                    hasAnyPermission = true;
                    break;
                }
            }
            if (hasAnyPermission)
                toReturn.add(value);
        }
        return toReturn;
    }

    @Getter
    @AllArgsConstructor
    private enum ActionbarCategory {
        ADS("verany.player"),
        UPDATES("verany.player"),
        LEVEL(""),
        REPORTS("verany.reports"),
        SUPPORT("verany.support"),
        SERVER("*");

        private final List<String> permissions;

        ActionbarCategory(String... permissions) {
            this.permissions = Arrays.asList(permissions);
        }

    }
}
