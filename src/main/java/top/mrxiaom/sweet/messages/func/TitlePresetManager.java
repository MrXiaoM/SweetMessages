package top.mrxiaom.sweet.messages.func;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.messages.SweetMessages;

import java.util.HashMap;
import java.util.Map;

@AutoRegister
public class TitlePresetManager extends AbstractModule {
    public static class Preset {
        public final int fadeIn, stay, fadeOut;
        public Preset(int fadeIn, int stay, int fadeOut) {
            this.fadeIn = fadeIn;
            this.stay = stay;
            this.fadeOut = fadeOut;
        }
    }
    private int defaultFadeIn, defaultStay, defaultFadeOut;
    private final Map<String, Preset> timePresets = new HashMap<>();
    public TitlePresetManager(SweetMessages plugin) {
        super(plugin);
    }

    public int getDefaultFadeIn() {
        return defaultFadeIn;
    }

    public int getDefaultStay() {
        return defaultStay;
    }

    public int getDefaultFadeOut() {
        return defaultFadeOut;
    }

    @Nullable
    public Preset getTimePreset(String id) {
        return timePresets.get(id);
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        defaultFadeIn = config.getInt("title-default.fade-in", 10);
        defaultStay = config.getInt("title-default.stay", 40);
        defaultFadeOut = config.getInt("title-default.fade-out", 10);
        timePresets.clear();
        ConfigurationSection section = config.getConfigurationSection("title-times");
        if (section != null) for (String key : section.getKeys(false)) {
            int fadeIn = section.getInt(key + ".fade-in");
            int stay = section.getInt(key + ".stay");
            int fadeOut = section.getInt(key + ".fade-out");
            timePresets.put(key, new Preset(fadeIn, stay, fadeOut));
        }
    }

    public static TitlePresetManager inst() {
        return instanceOf(TitlePresetManager.class);
    }
}
