package net.verany.lobbysystem.game.bossbar;

import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.settings.AbstractSetting;
import net.verany.api.settings.SettingWrapper;
import net.verany.api.task.AbstractTask;
import net.verany.lobbysystem.game.config.HubConfig;
import net.verany.lobbysystem.game.player.IHubPlayer;
import org.bukkit.boss.BossBar;

import java.util.concurrent.TimeUnit;

public class BossBarTask extends AbstractTask {

    public BossBarTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        for (IHubPlayer player : Verany.getPlayers(IHubPlayer.class)) {
            if (player.getBossBar() == null) continue;

            IPlayerInfo playerInfo = Verany.getPlayer(player.getUniqueId());
            if (playerInfo.getPrefixPattern() == null) continue;
            BossBar bossBar = player.getBossBar();

            String[] bossBars = playerInfo.getKeyArray("hub.bossbars", '~');

            long waiting = playerInfo.getTempSetting(BossBarSetting.WAITING);
            if (waiting >= System.currentTimeMillis()) {
                int count = Math.toIntExact(TimeUnit.MILLISECONDS.toSeconds(waiting - System.currentTimeMillis()));
                bossBar.setProgress(count / (double) HubConfig.BOSSBAR_WAIT_SECONDS.getValue());
                if (count <= 0) {
                    playerInfo.setTempSetting(BossBarSetting.BACK, true);
                }
                continue;
            }

            int currentText = playerInfo.getTempSetting(BossBarSetting.CURRENT_TEXT);
            int currentTextCharacter = playerInfo.getTempSetting(BossBarSetting.CURRENT_TEXT_CHARACTER);
            String currentKey = bossBars[currentText];
            StringBuilder message = new StringBuilder(playerInfo.getTempSetting(BossBarSetting.CURRENT_MESSAGE));

            if (playerInfo.getTempSetting(BossBarSetting.BACK)) {
                if (message.length() == 0) {
                    playerInfo.setTempSetting(BossBarSetting.BACK, false);

                    currentText++;
                    if (currentText >= bossBars.length)
                        currentText = 0;

                    playerInfo.setTempSetting(BossBarSetting.CURRENT_TEXT, currentText);
                    playerInfo.setTempSetting(BossBarSetting.CURRENT_TEXT_CHARACTER, 0);
                    playerInfo.setTempSetting(BossBarSetting.CURRENT_MESSAGE, "");
                    playerInfo.setTempSetting(BossBarSetting.LAST_COLOR, new StringBuilder());

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
                if (currentColor.toString().contains("§"))
                    playerInfo.setTempSetting(BossBarSetting.LAST_COLOR, currentColor);
                message = new StringBuilder(message.toString().replaceFirst(currentColor.toString(), ""));
                if (message.length() > 0)
                    message = new StringBuilder(message.toString().replaceFirst(String.valueOf(message.charAt(0)), ""));
                bossBar.setTitle(playerInfo.getTempSetting(BossBarSetting.LAST_COLOR) + message.toString());

                playerInfo.setTempSetting(BossBarSetting.CURRENT_MESSAGE, message.toString());

                double progress = (double) (currentKey.length() - message.length()) / currentKey.length();
                if (progress < 0 || progress > 1) continue;
                bossBar.setProgress(progress);

                continue;
            }

            char c = currentKey.charAt(currentTextCharacter);
            StringBuilder colorCode = new StringBuilder();
            while (c == '§') {
                colorCode.append(c);
                colorCode.append(currentKey.charAt(currentTextCharacter + 1));
                currentTextCharacter += 2;
                if (currentTextCharacter >= currentKey.length()) break;
                c = currentKey.charAt(currentTextCharacter);
            }
            playerInfo.setTempSetting(BossBarSetting.LAST_COLOR, colorCode);
            message.append(colorCode);
            message.append(c);

            bossBar.setTitle(playerInfo.getTempSetting(BossBarSetting.LAST_COLOR).toString() + message);

            currentTextCharacter++;

            if (currentTextCharacter >= currentKey.length()) {
                currentTextCharacter = 0;
                playerInfo.setTempSetting(BossBarSetting.WAITING, System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(HubConfig.BOSSBAR_WAIT_SECONDS.getValue()));
                playerInfo.setTempSetting(BossBarSetting.LAST_COLOR, new StringBuilder());
            }

            playerInfo.setTempSetting(BossBarSetting.CURRENT_TEXT_CHARACTER, currentTextCharacter);
            playerInfo.setTempSetting(BossBarSetting.CURRENT_MESSAGE, message.toString());
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
