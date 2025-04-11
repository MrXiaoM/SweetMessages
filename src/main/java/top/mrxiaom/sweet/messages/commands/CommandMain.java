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
import top.mrxiaom.sweet.messages.commands.args.TextArguments;
import top.mrxiaom.sweet.messages.func.AbstractModule;

import java.util.*;

import static top.mrxiaom.sweet.messages.commands.args.IArguments.parse;

@AutoRegister
public class CommandMain extends AbstractModule implements CommandExecutor, TabCompleter, Listener {
    public CommandMain(SweetMessages plugin) {
        super(plugin);
        registerCommand("sweetmessages", this);
        listOpArg0.addAll(argMessage);
        listOpArg0.addAll(argAction);
    }

    /**
     * 仅实现部分简单的目标选择器，参考资料来自
     * <a href="https://zh.minecraft.wiki/w/%E7%9B%AE%E6%A0%87%E9%80%89%E6%8B%A9%E5%99%A8">Minecraft Wiki</a>
     */
    @SuppressWarnings("IfCanBeSwitch")
    public List<CommandSender> parseReceivers(CommandSender sender, String s) {
        if (s.equals("@a") || s.equals("@e")) { // 所有在线玩家
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
                TextArguments arguments = parse(TextArguments::parser, args, 2);
                Runnable execute = () -> {
                    for (CommandSender receiver : receivers) {
                        for (String line : arguments.lines) {
                            AdventureUtil.sendMessage(receiver, line);
                        }
                    }
                };
                if (arguments.delay > 0)  {
                    Bukkit.getScheduler().runTaskLater(plugin, execute, arguments.delay);
                } else {
                    execute.run();
                }
                return true;
            }
            if (args.length >= 2 && argAction.contains(arg0)) {
                List<CommandSender> receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return t(sender, "&e输入的消息接收者 " + args[1] + " 无效");
                }
                TextArguments arguments = parse(TextArguments::parser, args, 2);
                Runnable execute = () -> {
                    String message = arguments.lines.get(0);
                    for (CommandSender receiver : receivers) {
                        if (receiver instanceof Player) {
                            AdventureUtil.sendActionBar((Player) receiver, message);
                        }
                    }
                };
                if (arguments.delay > 0)  {
                    Bukkit.getScheduler().runTaskLater(plugin, execute, arguments.delay);
                } else {
                    execute.run();
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

    private static final List<String> emptyList = Lists.newArrayList();
    private static final List<String> listOpArg0 = new ArrayList<>();
    private static final List<String> listTargetArg1 = Lists.newArrayList("@a", "@e", "@s", "@p", "@r");
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                return startsWith(listOpArg0, args[0]);
            }
            if (args.length == 2) {
                String arg0 = args[0].toLowerCase();
                if (argMessage.contains(arg0) || argAction.contains(arg0)) {
                    if (args[1].startsWith("@")) {
                        return startsWith(listTargetArg1, args[1]);
                    }
                    if (!args[1].contains(",")) {
                        List<String> list = new ArrayList<>(listTargetArg1);
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            list.add(player.getName());
                        }
                        return startsWith(list, args[1]);
                    }
                    String prefix = args[1].substring(0, args[1].lastIndexOf(',') + 1);
                    String input = args[1].substring(prefix.length()).toLowerCase();
                    List<String> list = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        String name = player.getName();
                        if (name.toLowerCase().startsWith(input)) {
                            list.add(prefix + name);
                        }
                    }
                    return list;
                }
            }
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
