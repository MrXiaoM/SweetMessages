package top.mrxiaom.sweet.messages.commands.args;

import com.google.common.collect.Lists;
import top.mrxiaom.pluginbase.utils.Util;

import java.util.List;
import java.util.Map;

import static top.mrxiaom.sweet.messages.commands.args.IArguments.get;

public class TextArguments implements IArguments {
    public final boolean papi;
    public final long delay;
    public final List<String> lines;

    public TextArguments(boolean papi, long delay, List<String> lines) {
        this.papi = papi;
        this.delay = delay;
        this.lines = lines;
    }

    public static TextArguments parser(Map<String, String> arguments, String last) {
        String papiStr = get(arguments, "-papi", "--papi", "--placeholder", "--placeholders");
        boolean papi = "true".equals(papiStr) || "yes".equals(papiStr);
        Long delay = Util.parseLong(get(arguments, "-d", "--delay")).orElse(0L);
        List<String> lines = last.contains("\\n")
            ? Lists.newArrayList(last.split("\\n"))
            : Lists.newArrayList(last);
        return new TextArguments(papi, delay, lines);
    }
}
