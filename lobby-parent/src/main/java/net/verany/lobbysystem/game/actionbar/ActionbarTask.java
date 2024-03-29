package net.verany.lobbysystem.game.actionbar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.player.leveling.LevelCalculator;
import net.verany.api.settings.AbstractSetting;
import net.verany.api.settings.SettingWrapper;
import net.verany.api.task.AbstractTask;
import net.verany.lobbysystem.LobbySystem;
import net.verany.lobbysystem.game.config.HubConfig;
import net.verany.lobbysystem.game.jumpandrun.JumpAndRun;
import net.verany.lobbysystem.game.player.IHubPlayer;
import net.verany.lobbysystem.game.settings.HubSetting;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ActionbarTask extends AbstractTask {

    private final double SECONDS_TO_TICKS_FACTOR = 1_000d / Math.pow(60d, 2d);
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

            /*HubSetting.TimeType timeType = onlinePlayer.getSettingValue(HubSetting.TIME_TYPE);
            switch (timeType) {
                case DAY -> player.setPlayerTime(1000, false);
                case NIGHT -> player.setPlayerTime(18000, false);
            }*/

            if (player.hasMetadata("jump_and_run")) {
                JumpAndRun jumpAndRun = (JumpAndRun) player.getMetadata("jump_and_run").get(0).value();
                int highscore = onlinePlayer.getPlayer(IHubPlayer.class).getJumpAndRunHighScore();
                String progressBar = Verany.getProgressBar(Math.min(jumpAndRun.getCurrentScore(), highscore), highscore, 10, '|', jumpAndRun.getCurrentScore() < highscore ? ChatColor.GREEN : ChatColor.DARK_GREEN, ChatColor.RED);
                onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.jump_and_run.actionbar", new Placeholder("%current_score%", jumpAndRun.getCurrentScore()), new Placeholder("%highscore%", highscore + " §8(" + progressBar + "§8)")));
                continue;
            }

            List<ActionbarCategory> categories = getAvailableCategories(player);

            long time = onlinePlayer.getTempSetting(timeSettings);
            int count = onlinePlayer.getTempSetting(countSetting);
            int messageCount = onlinePlayer.getTempSetting(messageCountSetting);
            int maxMessages;

            if (count >= categories.size())
                count = 0;
            ActionbarCategory category = categories.get(count);
            switch (category) {
                case SERVER -> {
                    maxMessages = 1;
                    try {
                        double tps = Double.parseDouble(Verany.round(Bukkit.getTPS()[0]));
                        String status = tps >= 18 ? "§2✔" : tps < 18 && tps >= 15 ? "§6✖" : tps < 15 && tps >= 11 ? "§c✖" : "§4✖";
                        onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar.server", new Placeholder("%tps%", tps + " §8(" + status + "§8)")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                case REPORTS -> {
                    maxMessages = 1;
                    int reports = 0;
                    onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar.reports", new Placeholder("%amount%", (reports < 5 ? "§a" + reports : reports < 10 ? "§e" + reports : "§c" + reports))));
                }
                case SUPPORT -> {
                    maxMessages = 1;
                    int support = LobbySystem.INSTANCE.getPlayersInSupport();
                    onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar.support", new Placeholder("%amount%", (support < 5 ? "§a" + support : support < 10 ? "§e" + support : "§c" + support))));
                }
                case LEVEL -> {
                    maxMessages = 1;
                    int level = onlinePlayer.getLevelObject().getLevel();
                    int exp = onlinePlayer.getLevelObject().getExp();
                    int maxExp = onlinePlayer.getLevelObject().getMaxExp();
                    int newExp = (level == 1 ? exp : exp - LevelCalculator.fullTargetExp(level - 1));
                    int newMaxExp = (level == 1 ? maxExp : maxExp - LevelCalculator.fullTargetExp(level - 1));
                    String progressBar = Verany.getProgressBar(newExp, newMaxExp, 50, '|', ChatColor.GREEN, ChatColor.RED);
                    onlinePlayer.setDefaultActionbar(onlinePlayer.getKey("hub.actionbar." + category.name().toLowerCase(), new Placeholder("%current_level%", Verany.asDecimal(level)), new Placeholder("%progress_bar%", progressBar), new Placeholder("%next_level%", Verany.asDecimal(level + 1)), new Placeholder("%exp%", Verany.asDecimal(onlinePlayer.getLevelObject().getExp())), new Placeholder("%max_exp%", Verany.asDecimal(onlinePlayer.getLevelObject().getMaxExp()))));
                }
                default -> {
                    String[] messages = onlinePlayer.getKeyArray("hub.actionbar." + category.name().toLowerCase(), '~');
                    maxMessages = messages.length;
                    if (messageCount >= messages.length)
                        messageCount = 0;
                    onlinePlayer.setDefaultActionbar(messages[messageCount]);
                }
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

    /*private long getWorldTime() {
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Europe/Berlin"));
        int secondsInDay = dateTime.getHour() * 3600 + dateTime.getMinute() * 60 + dateTime.getSecond();
        return overflow(18_000 + (int) (secondsInDay * SECONDS_TO_TICKS_FACTOR), 24_000);
    }*/

    public int overflow(int value, int at) {
        while (value > at) {
            value -= at;
        }
        return value;
    }
}
