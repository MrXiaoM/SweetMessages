package top.mrxiaom.sweet.messages.commands.args;

import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.actions.ActionProviders;
import top.mrxiaom.pluginbase.api.IAction;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.api.EnumBarColor;
import top.mrxiaom.sweet.messages.api.EnumBarStyle;

import java.util.List;
import java.util.Map;

import static top.mrxiaom.sweet.messages.commands.args.IArguments.get;

public class BossBarArguments implements IArguments {
    public final boolean papi;
    public final long delay;
    public final long duration;
    public final EnumBarColor color;
    public final EnumBarStyle style;
    public final String title;
    public final @Nullable IAction postActions;

    public BossBarArguments(boolean papi, long delay, long duration, EnumBarColor color, EnumBarStyle style, String text, @Nullable IAction postActions) {
        this.papi = papi;
        this.delay = delay;
        this.duration = duration;
        this.color = color;
        this.style = style;
        this.title = text;
        this.postActions = postActions;
    }

    public static BossBarArguments parser(Map<String, String> arguments, String last) {
        String papiStr = get(arguments, "-p", "--papi", "--placeholder", "--placeholders");
        boolean papi = "true".equals(papiStr) || "yes".equals(papiStr);
        long delay = Util.parseLong(get(arguments, "-d", "--delay")).orElse(0L);
        double duration = Util.parseDouble(get(arguments, "-k", "--keep", "--duration")).orElse(3.0);
        IAction postActions = null;
        String postActionsStr = get(arguments, "-a", "--post-actions");
        if (postActionsStr != null && !postActionsStr.isEmpty()) {
            List<IAction> actions = ActionProviders.loadActions(Lists.newArrayList(postActionsStr));
            if (!actions.isEmpty()) {
                postActions = actions.get(0);
            }
        }
        EnumBarColor barColor = Util.valueOr(EnumBarColor.class, get(arguments, "-c", "--color"), EnumBarColor.WHITE);
        EnumBarStyle barStyle = Util.valueOr(EnumBarStyle.class, get(arguments, "-s", "--style"), EnumBarStyle.SOLID);
        return new BossBarArguments(papi, delay, (long)(duration / 20L), barColor, barStyle, last, postActions);
    }
}
