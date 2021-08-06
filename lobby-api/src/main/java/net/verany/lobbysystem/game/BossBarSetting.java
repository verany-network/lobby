package net.verany.lobbysystem.game;

import net.verany.api.settings.AbstractSetting;
import net.verany.api.settings.SettingWrapper;

public class BossBarSetting {
    public static final AbstractSetting<Integer> CURRENT_TEXT = new SettingWrapper.TempSettingWrapper<>("current_text", Integer.class, 0);
    public static final AbstractSetting<Integer> CURRENT_TEXT_CHARACTER = new SettingWrapper.TempSettingWrapper<>("current_text_character", Integer.class, 0);
    public static final AbstractSetting<String> CURRENT_MESSAGE = new SettingWrapper.TempSettingWrapper<>("current_message", String.class, "");
    public static final AbstractSetting<Long> WAITING = new SettingWrapper.TempSettingWrapper<>("waiting", Long.class, 0L);
    public static final AbstractSetting<Boolean> BACK = new SettingWrapper.TempSettingWrapper<>("back", Boolean.class, false);
    public static final AbstractSetting<StringBuilder> LAST_COLOR = new SettingWrapper.TempSettingWrapper<>("last_color", StringBuilder.class, new StringBuilder());
    public static final AbstractSetting<String[]> MESSAGES = new SettingWrapper.TempSettingWrapper<>("messages", String[].class, new String[]{});
}