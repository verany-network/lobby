package net.verany.lobbysystem.game.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;

import java.util.List;

public class HubSound {

    //public static final AbstractVeranySound INVENTORY_SETTING_CHANGE = new VeranySound("inventory_setting_change", Sound.BLOCK_NOTE_BLOCK_PLING, 1, 1.7F);


    @AllArgsConstructor
    @Getter
    @Setter
    public static class SoundSettingList {
        private final boolean enabled;
        private final List<Sound> sounds;
    }
}
