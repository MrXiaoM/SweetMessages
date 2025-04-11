package top.mrxiaom.sweet.messages.commands.args;

import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.func.TitlePresetManager;

import java.util.Map;

import static top.mrxiaom.sweet.messages.commands.args.IArguments.get;

public class TitleArguments implements IArguments {
    public final long delay;
    public final int fadeIn, stay, fadeOut;
    public final String title, subTitle;

    public TitleArguments(long delay, int fadeIn, int stay, int fadeOut, String title, String subTitle) {
        this.delay = delay;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.title = title;
        this.subTitle = subTitle;
    }

    public static TitleArguments parser(Map<String, String> arguments, String last) {
        long delay = Util.parseLong(get(arguments, "-d", "--delay")).orElse(0L);
        String title, subTitle;
        if (last.contains("\\n")) {
            int index = last.indexOf("\\n");
            title = last.substring(0, index);
            subTitle = last.substring(index + 2);
        } else {
            title = last;
            subTitle = "";
        }
        TitlePresetManager manager = TitlePresetManager.inst();
        String time = get(arguments, "time");
        int fadeIn = manager.getDefaultFadeIn();
        int stay = manager.getDefaultStay();
        int fadeOut = manager.getDefaultFadeOut();
        if (time != null) {
            if (time.contains(",")) {
                String[] split = time.split(",", 3);
                if (split.length >= 1) fadeIn = Util.parseInt(split[0]).orElse(fadeIn);
                if (split.length >= 2) stay = Util.parseInt(split[1]).orElse(stay);
                if (split.length >= 3) fadeOut = Util.parseInt(split[2]).orElse(fadeOut);
            } else {
                // TODO: 从 TitlePresetManager 获取预设时间
            }
        }
        fadeIn = Util.parseInt(get(arguments, "-i", "--fadeIn", "--fade-in", "--fade_in")).orElse(fadeIn);
        stay = Util.parseInt(get(arguments, "-s", "--stay")).orElse(stay);
        fadeOut = Util.parseInt(get(arguments, "-o", "--fadeOut", "--fade-out", "--fade_out")).orElse(fadeOut);
        return new TitleArguments(delay, fadeIn, stay, fadeOut, title, subTitle);
    }
}
