package top.mrxiaom.sweet.messages.commands.args;

import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.actions.ActionProviders;
import top.mrxiaom.pluginbase.api.IAction;
import top.mrxiaom.pluginbase.utils.AdventureUtil;
import top.mrxiaom.pluginbase.utils.depend.PAPI;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.api.EnumBarColor;
import top.mrxiaom.sweet.messages.api.EnumBarStyle;
import top.mrxiaom.sweet.messages.api.IBossBarWrapper;

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
    public final String postActionsRaw;

    public BossBarArguments(boolean papi, long delay, long duration, EnumBarColor color, EnumBarStyle style, String text, @Nullable IAction postActions, @Nullable String postActionsRaw) {
        this.papi = papi;
        this.delay = delay;
        this.duration = duration;
        this.color = color;
        this.style = style;
        this.title = text;
        this.postActions = postActions;
        this.postActionsRaw = postActionsRaw;
    }

    @Override
    public void execute(SweetMessages plugin, List<CommandSender> receivers) {
        Runnable execute = () -> {
            for (CommandSender receiver : receivers) {
                if (receiver instanceof Player) {
                    Player player = (Player) receiver;
                    String msg = papi ? PAPI.setPlaceholders(player, title) : title;

                    Component component = AdventureUtil.miniMessage(msg);
                    IBossBarWrapper bar = plugin.getBossBarFactory().create(component, color, style);
                    bar.addPlayer(player);
                    bar.setVisible(true);

                    plugin.getScheduler().runTaskLater(() -> {
                        bar.setVisible(false);
                        bar.removeAll();
                        if (postActions != null) {
                            postActions.run(player);
                        }
                    }, duration);
                }
            }
        };
        if (delay > 0)  {
            plugin.getScheduler().runTaskLater(execute, delay);
        } else {
            execute.run();
        }
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
        } else {
            postActionsStr = null;
        }
        EnumBarColor barColor = Util.valueOr(EnumBarColor.class, get(arguments, "-c", "--color"), EnumBarColor.WHITE);
        EnumBarStyle barStyle = Util.valueOr(EnumBarStyle.class, get(arguments, "-s", "--style"), EnumBarStyle.SOLID);
        return new BossBarArguments(papi, delay, (long)(duration / 20L), barColor, barStyle, last, postActions, postActionsStr);
    }
}
