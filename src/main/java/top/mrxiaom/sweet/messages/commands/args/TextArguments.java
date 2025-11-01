package top.mrxiaom.sweet.messages.commands.args;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.mrxiaom.pluginbase.utils.AdventureUtil;
import top.mrxiaom.pluginbase.utils.depend.PAPI;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.SweetMessages;

import java.util.List;
import java.util.Map;

import static top.mrxiaom.sweet.messages.commands.args.IArguments.get;

public class TextArguments implements IArguments {
    public final boolean papi;
    public final long delay;
    public final List<String> lines;
    public boolean isActionMessage = false;

    public TextArguments(boolean papi, long delay, List<String> lines) {
        this.papi = papi;
        this.delay = delay;
        this.lines = lines;
    }

    @Override
    public void execute(SweetMessages plugin, List<CommandSender> receivers) {
        Runnable execute = () -> {
            for (CommandSender receiver : receivers) {
                if (isActionMessage) {
                    if (receiver instanceof Player) {
                        Player player = (Player) receiver;
                        String message = lines.get(0);
                        String msg = papi ? PAPI.setPlaceholders(player, message) : message;
                        AdventureUtil.sendActionBar(player, msg);
                    }
                } else {
                    List<String> lines = papi && receiver instanceof Player
                            ? PAPI.setPlaceholders((Player) receiver, this.lines)
                            : this.lines;
                    for (String line : lines) {
                        AdventureUtil.sendMessage(receiver, line);
                    }
                }
            }
        };
        if (delay > 0)  {
            plugin.getScheduler().runTaskLater(execute, delay);
        } else {
            execute.run();
        }
    }

    public static TextArguments parser(Map<String, String> arguments, String last) {
        List<String> lines = last.contains("\\n")
                ? Lists.newArrayList(last.split("\\n"))
                : Lists.newArrayList(last);
        return parser(arguments, lines);
    }

    public static TextArguments parser(Map<String, String> arguments, List<String> lines) {
        String papiStr = get(arguments, "-p", "--papi", "--placeholder", "--placeholders");
        boolean papi = "true".equals(papiStr) || "yes".equals(papiStr);
        Long delay = Util.parseLong(get(arguments, "-d", "--delay")).orElse(0L);
        return new TextArguments(papi, delay, lines);
    }
}
