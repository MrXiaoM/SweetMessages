package top.mrxiaom.sweet.messages.commands;
        
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.AdventureUtil;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.func.AbstractModule;

import java.util.*;

@AutoRegister
public class CommandMain extends AbstractModule implements CommandExecutor, TabCompleter, Listener {
    public CommandMain(SweetMessages plugin) {
        super(plugin);
        registerCommand("sweetmessages", this);
    }

    /**
     * 仅实现部分简单的目标选择器，参考资料来自
     * <a href="https://zh.minecraft.wiki/w/%E7%9B%AE%E6%A0%87%E9%80%89%E6%8B%A9%E5%99%A8">Minecraft Wiki</a>
     */
    @SuppressWarnings("IfCanBeSwitch")
    public List<CommandSender> parseReceivers(CommandSender sender, String s) {
        if (s.equals("@a")) { // 所有在线玩家
            List<CommandSender> receivers = new ArrayList<>();
            receivers.add(Bukkit.getConsoleSender()); // 为了后台也能收到，把它也加进去，留个底
            receivers.addAll(Bukkit.getOnlinePlayers());
            return receivers;
        }
        if (s.equals("@s")) { // 自己
            return Lists.newArrayList(sender);
        }
        if (s.equals("@p")) { // 距离自己最近的玩家
            if (sender instanceof Player) {
                Player player = (Player) sender;
                Location loc = player.getLocation();
                List<Player> players = player.getWorld().getPlayers();
                Double lastDistance = null;
                Player target = player; // 默认是自己
                for (Player p : players) {
                    // 遍历中，屏蔽自己和NPC
                    if (p.getName().equals(player.getName()) || p.hasMetadata("NPC")) continue;
                    double distance = p.getLocation().distance(loc);
                    if (lastDistance == null || distance < lastDistance) {
                        lastDistance = distance;
                        target = p;
                    }
                }
                return Lists.newArrayList(target);
            }
        }
        if (s.equals("@r")) { // 随机玩家
            List<CommandSender> list = Lists.newArrayList(Bukkit.getOnlinePlayers());
            if (list.isEmpty()) return null;
            if (list.size() == 1) return list;
            int index = new Random().nextInt(list.size());
            return Lists.newArrayList(list.get(index));
        }
        if (s.contains(",")) { // 多个玩家
            String[] split = s.split(",");
            List<Player> players = Util.getOnlinePlayersByName(Arrays.asList(split));
            if (players.isEmpty()) return null;
            return new ArrayList<>(players);
        }
        return Util.getOnlinePlayer(s)
                .map(it -> (CommandSender) it)
                .map(Lists::newArrayList)
                .orElse(null);
    }

    List<String> argMessage = Lists.newArrayList("message", "msg", "m");
    List<String> argAction = Lists.newArrayList("actionbar", "action", "a");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            String arg0 = args[0].toLowerCase();
            if (args.length >= 2 && argMessage.contains(arg0)) {
                List<CommandSender> receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return t(sender, "&e输入的消息接收者 " + args[1] + " 无效");
                }
                String message = consumeString(args, 2);
                String[] lines = message.contains("\\n")
                        ? message.split("\\n")
                        : new String[] { message };
                for (CommandSender receiver : receivers) {
                    for (String line : lines) {
                        AdventureUtil.sendMessage(receiver, line);
                    }
                }
                return true;
            }
            if (args.length >= 2 && argAction.contains(arg0)) {
                List<CommandSender> receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return t(sender, "&e输入的消息接收者 " + args[1] + " 无效");
                }
                String message = consumeString(args, 2);
                for (CommandSender receiver : receivers) {
                    if (receiver instanceof Player) {
                        AdventureUtil.sendActionBar((Player) receiver, message);
                    }
                }
                return true;
            }
            if (args.length == 1 && "reload".equals(arg0)) {
                plugin.reloadConfig();
                return t(sender, "&a配置文件已重载");
            }
        }
        return true;
    }

    @SuppressWarnings("SameParameterValue")
    private static String consumeString(String[] args, int startIndex) {
        if (startIndex >= args.length) return "";
        StringBuilder sb = new StringBuilder(args[startIndex]);
        for (int i = startIndex + 1; i < args.length; i++) {
            sb.append(" ").append(args[i]);
        }
        return sb.toString();
    }

    private static final List<String> emptyList = Lists.newArrayList();
    private static final List<String> listArg0 = Lists.newArrayList(
            "hello");
    private static final List<String> listOpArg0 = Lists.newArrayList(
            "hello", "reload");
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return startsWith(sender.isOp() ? listOpArg0 : listArg0, args[0]);
        }
        return emptyList;
    }

    public List<String> startsWith(Collection<String> list, String s) {
        return startsWith(null, list, s);
    }
    public List<String> startsWith(String[] addition, Collection<String> list, String s) {
        String s1 = s.toLowerCase();
        List<String> stringList = new ArrayList<>(list);
        if (addition != null) stringList.addAll(0, Lists.newArrayList(addition));
        stringList.removeIf(it -> !it.toLowerCase().startsWith(s1));
        return stringList;
    }
}
