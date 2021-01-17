package net.verany.hubsystem.utils.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.enumhelper.VeranyEnum;
import net.verany.api.setting.SettingWrapper;
import net.verany.api.settings.AbstractSetting;
import org.bukkit.Material;

public class HubSetting {

    public static final AbstractSetting<Boolean> LAST_LOCATION_TELEPORT = new SettingWrapper<>("last_location_teleport", "hubsystem", Boolean.class, true, true);
    public static final AbstractSetting<TimeType> TIME_TYPE = new SettingWrapper<>("time_type", "hubsystem", TimeType.class, TimeType.REAL_TIME, true);

    public enum TimeType {
        REAL_TIME,
        DAY,
        NIGHT;
    }

}
