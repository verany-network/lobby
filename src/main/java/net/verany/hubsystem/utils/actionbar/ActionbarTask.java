package net.verany.hubsystem.utils.actionbar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.player.leveling.LevelCalculator;
import net.verany.api.setting.SettingWrapper;
import net.verany.api.settings.AbstractSetting;
import net.verany.api.task.AbstractTask;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.config.HubConfig;
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

    private final AbstractSetting<Long> timeSettings = new SettingWrapper.TempSettingWrapper<>("time", Long.class, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(7));
    private final AbstractSetting<Integer> countSetting = new SettingWrapper.TempSettingWrapper<>("count", Integer.class, 0);
    private final AbstractSetting<Integer> messageCountSetting = new SettingWrapper.TempSettingWrapper<>("messageCount", Integer.class, 0);

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
                int highscore = Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).getJumpAndRunHighScore();
                String progressBar = Verany.getProgressBar(Math.min(jumpAndRun.getCurrentScore(), highscore), highscore, 10, '|', jumpAndRun.getCurrentScore() < highscore ? ChatColor.GREEN : ChatColor.DARK_GREEN, ChatColor.RED);
                onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.jump_and_run.actionbar", new Placeholder("%current_score%", jumpAndRun.getCurrentScore()), new Placeholder("%highscore%", highscore + " §8(" + progressBar + "§8)")));
                continue;
            }

            List<ActionbarCategory> categories = getAvailableCategories(player);

            long time = onlinePlayer.getTempSetting(timeSettings);
            int count = onlinePlayer.getTempSetting(countSetting);
            int messageCount = onlinePlayer.getTempSetting(messageCountSetting);
            int maxMessages;

            if (count > categories.size())
                count = 0;
            ActionbarCategory category = categories.get(count);
            switch (category) {
                case SERVER:
                    maxMessages = 1;
                    try {
                        double tps = Double.parseDouble(Verany.round(Bukkit.getTPS()[0]));
                        String status = tps >= 18 ? "§2✔" : tps < 18 && tps >= 15 ? "§6✖" : tps < 15 && tps >= 11 ? "§c✖" : "§4✖";
                        onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar.server", new Placeholder("%tps%", tps + " §8(" + status + "§8)")));
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
                    int level = onlinePlayer.getLevelObject().getLevel();
                    int exp = onlinePlayer.getLevelObject().getExp();
                    int maxExp = onlinePlayer.getLevelObject().getMaxExp();
                    int newExp = (level == 1 ? exp : exp - LevelCalculator.fullTargetExp(level - 1));
                    int newMaxExp = (level == 1 ? maxExp : maxExp - LevelCalculator.fullTargetExp(level - 1));

                    String progressBar = Verany.getProgressBar(newExp, newMaxExp, 50, '|', ChatColor.GREEN, ChatColor.RED);
                    onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar." + category.name().toLowerCase(), new Placeholder("%current_level%", Verany.asDecimal(level)), new Placeholder("%progress_bar%", progressBar), new Placeholder("%next_level%", Verany.asDecimal(level + 1)), new Placeholder("%exp%", Verany.asDecimal(onlinePlayer.getLevelObject().getExp())), new Placeholder("%max_exp%", Verany.asDecimal(onlinePlayer.getLevelObject().getMaxExp()))));
                    break;
                default:
                    String[] messages = onlinePlayer.getKeyArray("hub.actionbar." + category.name().toLowerCase(), '~');
                    maxMessages = messages.length;
                    onlinePlayer.setDefaultActionbar(messages[messageCount]);
                    break;
            }

            if (System.currentTimeMillis() < time) continue;
            if (!onlinePlayer.getActionbarQueue().isEmpty()) continue;
            onlinePlayer.setTempSetting(timeSettings, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(HubConfig.ACTIONBAR_SECONDS.getValue()));

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
