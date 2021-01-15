package net.verany.hubsystem.utils.settings;

import net.verany.api.setting.SettingWrapper;
import net.verany.api.settings.AbstractSetting;

public class HubSetting {

    public static final AbstractSetting<Boolean> LAST_LOCATION_TELEPORT = new SettingWrapper<>("last_location_teleport", "hubsystem", Boolean.class, true, true);

}
