package net.verany.hubsystem.utils.bossbar;

import net.verany.api.Verany;
import net.verany.api.bossbar.AbstractBossBar;
import net.verany.api.bossbar.DefaultBossBar;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.settings.AbstractSetting;
import net.verany.api.settings.SettingWrapper;
import net.verany.api.task.AbstractTask;
import net.verany.hubsystem.HubSystem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.AbstractSet;
import java.util.concurrent.TimeUnit;

public class BossBarTask extends AbstractTask {

    public BossBarTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        for (IPlayerInfo onlinePlayer : Verany.getOnlinePlayers()) {
            BossBar bar = Bukkit.getBossBar(new NamespacedKey(HubSystem.INSTANCE, "bossbar_" + onlinePlayer.getName()));
            if (bar == null) continue;

            String[] bossBars = onlinePlayer.getKeyArray("hub.bossbars", '~');

            long waiting = onlinePlayer.getTempSetting(BossBarSetting.WAITING);
            if (waiting >= System.currentTimeMillis()) {
                int count = Math.toIntExact(TimeUnit.MILLISECONDS.toSeconds(waiting - System.currentTimeMillis()));
                bar.setProgress(count / 6D);
                if (count <= 0) {
                    onlinePlayer.setTempSetting(BossBarSetting.BACK, true);
                }
                continue;
            }

            int currentText = onlinePlayer.getTempSetting(BossBarSetting.CURRENT_TEXT);
            int currentTextCharacter = onlinePlayer.getTempSetting(BossBarSetting.CURRENT_TEXT_CHARACTER);
            String currentKey = bossBars[currentText];
            StringBuilder message = new StringBuilder(onlinePlayer.getTempSetting(BossBarSetting.CURRENT_MESSAGE));

            if (onlinePlayer.getTempSetting(BossBarSetting.BACK)) {
                if (message.length() == 0) {
                    onlinePlayer.setTempSetting(BossBarSetting.BACK, false);

                    currentText++;
                    if (currentText >= bossBars.length)
                        currentText = 0;

                    onlinePlayer.setTempSetting(BossBarSetting.CURRENT_TEXT, currentText);
                    onlinePlayer.setTempSetting(BossBarSetting.CURRENT_TEXT_CHARACTER, 0);
                    onlinePlayer.setTempSetting(BossBarSetting.CURRENT_MESSAGE, "");

                    continue;
                }

                StringBuilder currentColor = new StringBuilder();
                int id = 0;
                while (message.charAt(id) == '§') {
                    currentColor.append(message.charAt(id));
                    currentColor.append(message.charAt(id + 1));
                    id += 2;
                    if (id >= message.length()) break;
                }
                message = new StringBuilder(message.toString().replaceFirst(currentColor.toString(), ""));
                if (message.length() > 0)
                    message = new StringBuilder(message.toString().replaceFirst(String.valueOf(message.charAt(0)), ""));
                bar.setTitle(message.toString());

                onlinePlayer.setTempSetting(BossBarSetting.CURRENT_MESSAGE, message.toString());

                double progress = (double) (currentKey.length() - message.length()) / currentKey.length();
                if (progress < 0 || progress > 1) continue;
                bar.setProgress(progress);

                continue;
            }
            char c = currentKey.charAt(currentTextCharacter);
            StringBuilder colorCode = new StringBuilder();
            while ((c == '§' || (currentKey.length() > currentTextCharacter + 2 && currentKey.charAt(currentTextCharacter + 2) == '§')) && currentKey.length() > currentTextCharacter + 2) {
                for (int i = 0; i < 2; i++) {
                    colorCode.append(c);
                    currentTextCharacter++;
                    c = currentKey.charAt(currentTextCharacter);
                }
            }
            onlinePlayer.setTempSetting(BossBarSetting.LAST_COLOR, colorCode);
            message.append(colorCode);
            message.append(c);

            bar.setTitle(onlinePlayer.getTempSetting(BossBarSetting.LAST_COLOR).toString() + message);

            currentTextCharacter++;

            if (currentTextCharacter >= currentKey.length()) {
                currentTextCharacter = 0;
                onlinePlayer.setTempSetting(BossBarSetting.WAITING, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(6));
            }

            onlinePlayer.setTempSetting(BossBarSetting.CURRENT_TEXT_CHARACTER, currentTextCharacter);
            onlinePlayer.setTempSetting(BossBarSetting.CURRENT_MESSAGE, message.toString());
        }
    }

    public static class BossBarSetting {
        public static final AbstractSetting<Integer> CURRENT_TEXT = new SettingWrapper.TempSettingWrapper<>("current_text", Integer.class, 0);
        public static final AbstractSetting<Integer> CURRENT_TEXT_CHARACTER = new SettingWrapper.TempSettingWrapper<>("current_text_character", Integer.class, 0);
        public static final AbstractSetting<String> CURRENT_MESSAGE = new SettingWrapper.TempSettingWrapper<>("current_message", String.class, "");
        public static final AbstractSetting<Long> WAITING = new SettingWrapper.TempSettingWrapper<>("waiting", Long.class, 0L);
        public static final AbstractSetting<Boolean> BACK = new SettingWrapper.TempSettingWrapper<>("back", Boolean.class, false);
        public static final AbstractSetting<StringBuilder> LAST_COLOR = new SettingWrapper.TempSettingWrapper<>("last_color", StringBuilder.class, new StringBuilder());
    }
}
