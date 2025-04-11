package top.mrxiaom.sweet.messages.func;

import org.bukkit.configuration.MemoryConfiguration;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.sweet.messages.SweetMessages;

@AutoRegister
public class TitlePresetManager extends AbstractModule {
    private int defaultFadeIn, defaultStay, defaultFadeOut;
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

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        defaultFadeIn = config.getInt("title-default.fade-in", 10);
        defaultStay = config.getInt("title-default.stay", 40);
        defaultFadeOut = config.getInt("title-default.fade-out", 10);
    }

    public static TitlePresetManager inst() {
        return instanceOf(TitlePresetManager.class);
    }
}
