package net.verany.hubsystem.utils.settings;

import lombok.Getter;
import net.verany.api.settings.AbstractSetting;
import net.verany.api.settings.Settings;
import net.verany.hubsystem.utils.inventories.ProfileInventory;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@Getter
public class HubSetting<T> extends AbstractSetting<T> {

    private final ProfileInventory.ProfileCategory.SettingCategory settingCategory;

    public static final AbstractSetting<Boolean> LAST_LOCATION_TELEPORT = new HubSetting<>("last_location_teleport", "HUB", Boolean.class, true, true, ProfileInventory.ProfileCategory.SettingCategory.HUB, Material.BEACON);
    public static final AbstractSetting<TimeType> TIME_TYPE = new HubSetting<>("time_type", "HUB", TimeType.class, TimeType.REAL_TIME, true, ProfileInventory.ProfileCategory.SettingCategory.HUB, Material.CLOCK);

    public HubSetting(String key, String category, Class<T> tClass, T defaultValue, boolean inInventory, ProfileInventory.ProfileCategory.SettingCategory settingCategory, Material material) {
        super(key, category, tClass, defaultValue, material, inInventory);
        this.settingCategory = settingCategory;
        Settings.VALUES.add(this);
    }

    public static List<AbstractSetting<?>> getSettings(ProfileInventory.ProfileCategory.SettingCategory category) {
        List<AbstractSetting<?>> toReturn = new ArrayList<>();
        for (AbstractSetting<?> hubsystem : Settings.getSettingByCategory("HUB")) {
            HubSetting<?> hubSetting = (HubSetting<?>) hubsystem;
            if (hubSetting.getSettingCategory().equals(category))
                toReturn.add(hubSetting);
        }
        return toReturn;
    }

    public static <E> HubSetting<E> toHubSetting(AbstractSetting<E> setting) {
        return new HubSetting<>(setting.getKey(), "HUB", setting.getTClass(), setting.getDefaultValue(), setting.isInInventory(), ProfileInventory.ProfileCategory.SettingCategory.valueOf(setting.getCategory()), setting.getMaterial());
    }

    public enum TimeType {
        REAL_TIME,
        DAY,
        NIGHT;
    }

}
