package top.mrxiaom.sweet.messages.commands.args;

import com.google.common.collect.Lists;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.List;
import java.util.Map;

import static top.mrxiaom.sweet.messages.commands.args.IArguments.get;

public class TextArguments implements IArguments {
    public final long delay;
    public final List<String> lines;

    public TextArguments(long delay, List<String> lines) {
        this.delay = delay;
        this.lines = lines;
    }

    public static TextArguments parser(Map<String, String> arguments, String last) {
        Long delay = Util.parseLong(get(arguments, "-d", "--delay")).orElse(0L);
        List<String> lines = last.contains("\\n")
            ? Lists.newArrayList(last.split("\\n"))
            : Lists.newArrayList(last);
        return new TextArguments(delay, lines);
    }
}
