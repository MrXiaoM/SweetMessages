package top.mrxiaom.sweet.messages.commands.args;

import java.util.HashMap;
import java.util.Map;

public interface IArguments {
    @FunctionalInterface
    interface Parser<T> {
        T parse(Map<String, String> arguments, String last);
    }
    static <T extends IArguments> T parse(Parser<T> parser, String[] args, int startIndex) {
        Map<String, String> arguments = new HashMap<>();
        int i = startIndex;
        for (; i < args.length; i++) {
            String s = args[i];
            if (s.startsWith("-") && s.contains("=")) {
                String[] split = s.split("=", 2);
                arguments.put(split[0], split[1]);
            } else break;
        }
        String last = consumeString(args, i);
        if (last.trim().isEmpty()) {
            return null;
        }
        return parser.parse(arguments, last);
    }
    static String consumeString(String[] args, int startIndex) {
        if (startIndex >= args.length) return "";
        StringBuilder sb = new StringBuilder(args[startIndex]);
        for (int i = startIndex + 1; i < args.length; i++) {
            sb.append(" ").append(args[i]);
        }
        return sb.toString();
    }
    static String get(Map<String, String> map, String... keys) {
        for (String key : keys) {
            String value = map.get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
