package top.mrxiaom.sweet.messages.commands.args;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.mrxiaom.pluginbase.utils.AdventureUtil;
import top.mrxiaom.pluginbase.utils.depend.PAPI;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.func.TitlePresetManager;

import java.util.List;
import java.util.Map;

import static top.mrxiaom.sweet.messages.commands.args.IArguments.get;

public class TitleArguments implements IArguments {
    public final boolean papi;
    public final long delay;
    public final int fadeIn, stay, fadeOut;
    public final String title, subTitle;

    public TitleArguments(boolean papi, long delay, int fadeIn, int stay, int fadeOut, String title, String subTitle) {
        this.papi = papi;
        this.delay = delay;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.title = title;
        this.subTitle = subTitle;
    }

    @Override
    public void execute(SweetMessages plugin, List<CommandSender> receivers) {
        Runnable execute = () -> {
            for (CommandSender receiver : receivers) {
                if (receiver instanceof Player) {
                    Player player = (Player) receiver;
                    AdventureUtil.sendTitle(player,
                            papi ? PAPI.setPlaceholders(player, title) : title,
                            papi ? PAPI.setPlaceholders(player, subTitle) : subTitle,
                            fadeIn, stay, fadeOut);
                }
            }
        };
        if (delay > 0)  {
            plugin.getScheduler().runTaskLater(execute, delay);
        } else {
            execute.run();
        }
    }

    public static TitleArguments parser(Map<String, String> arguments, String last) {
        String title, subTitle;
        if (last.contains("\\n")) {
            int index = last.indexOf("\\n");
            title = last.substring(0, index);
            subTitle = last.substring(index + 2);
        } else {
            title = last;
            subTitle = "";
        }
        return parser(arguments, title, subTitle);
    }

    public static TitleArguments parser(Map<String, String> arguments, String title, String subTitle) {
        String papiStr = get(arguments, "-p", "--papi", "--placeholder", "--placeholders");
        boolean papi = "true".equals(papiStr) || "yes".equals(papiStr);
        long delay = Util.parseLong(get(arguments, "-d", "--delay")).orElse(0L);
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
                TitlePresetManager.Preset preset = manager.getTimePreset(time);
                if (preset != null) {
                    fadeIn = preset.fadeIn;
                    stay = preset.stay;
                    fadeOut = preset.fadeOut;
                }
            }
        }
        fadeIn = Util.parseInt(get(arguments, "-i", "--fadeIn", "--fade-in", "--fade_in")).orElse(fadeIn);
        stay = Util.parseInt(get(arguments, "-s", "--stay")).orElse(stay);
        fadeOut = Util.parseInt(get(arguments, "-o", "--fadeOut", "--fade-out", "--fade_out")).orElse(fadeOut);
        return new TitleArguments(papi, delay, fadeIn, stay, fadeOut, title, subTitle);
    }
}
