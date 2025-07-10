package top.mrxiaom.sweet.messages.commands;
        
import com.google.common.collect.Lists;
import net.kyori.adventure.text.Component;
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
import top.mrxiaom.pluginbase.utils.PAPI;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.Tips;
import top.mrxiaom.sweet.messages.api.EnumBarColor;
import top.mrxiaom.sweet.messages.api.EnumBarStyle;
import top.mrxiaom.sweet.messages.api.IBossBarWrapper;
import top.mrxiaom.sweet.messages.commands.args.BossBarArguments;
import top.mrxiaom.sweet.messages.commands.args.TextArguments;
import top.mrxiaom.sweet.messages.commands.args.TitleArguments;
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
        listOpArg0.addAll(argTitle);
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
        if (s.startsWith("@")) { // 其它选择器均不支持
            return null;
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
    List<String> argTitle = Lists.newArrayList("title", "t");
    List<String> argBossBar = Lists.newArrayList("bossbar", "bar", "b");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            String arg0 = args[0].toLowerCase();
            if (args.length >= 2 && argMessage.contains(arg0)) {
                List<CommandSender> receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return Tips.invalid_selector.tm(sender, args[1]);
                }
                TextArguments arguments = parse(TextArguments::parser, args, 2);
                if (arguments == null) {
                    return Tips.invalid_content.tm(sender);
                }
                Runnable execute = () -> {
                    for (CommandSender receiver : receivers) {
                        List<String> lines = arguments.papi && receiver instanceof Player
                                ? PAPI.setPlaceholders((Player) receiver, arguments.lines)
                                : arguments.lines;
                        for (String line : lines) {
                            AdventureUtil.sendMessage(receiver, line);
                        }
                    }
                };
                if (arguments.delay > 0)  {
                    plugin.getScheduler().runTaskLater(execute, arguments.delay);
                } else {
                    execute.run();
                }
                return true;
            }
            if (args.length >= 2 && argAction.contains(arg0)) {
                List<CommandSender> receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return Tips.invalid_selector.tm(sender, args[1]);
                }
                TextArguments arguments = parse(TextArguments::parser, args, 2);
                if (arguments == null) {
                    return Tips.invalid_content.tm(sender);
                }
                Runnable execute = () -> {
                    boolean papi = arguments.papi;
                    String message = arguments.lines.get(0);
                    for (CommandSender receiver : receivers) {
                        if (receiver instanceof Player) {
                            Player player = (Player) receiver;
                            String msg = papi ? PAPI.setPlaceholders(player, message) : message;
                            AdventureUtil.sendActionBar(player, msg);
                        }
                    }
                };
                if (arguments.delay > 0)  {
                    plugin.getScheduler().runTaskLater(execute, arguments.delay);
                } else {
                    execute.run();
                }
                return true;
            }
            if (args.length >= 2 && argTitle.contains(arg0)) {
                List<CommandSender> receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return Tips.invalid_selector.tm(sender, args[1]);
                }
                TitleArguments arguments = parse(TitleArguments::parser, args, 2);
                if (arguments == null) {
                    return Tips.invalid_title.tm(sender);
                }
                Runnable execute = () -> {
                    boolean papi = arguments.papi;
                    String title = arguments.title;
                    String subTitle = arguments.subTitle;
                    int fadeIn = arguments.fadeIn;
                    int stay = arguments.stay;
                    int fadeOut = arguments.fadeOut;
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
                if (arguments.delay > 0)  {
                    plugin.getScheduler().runTaskLater(execute, arguments.delay);
                } else {
                    execute.run();
                }
                return true;
            }
            if (args.length >= 2 && argBossBar.contains(arg0)) {
                List<CommandSender> receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return Tips.invalid_selector.tm(sender, args[1]);
                }
                BossBarArguments arguments = parse(BossBarArguments::parser, args, 2);
                if (arguments == null) {
                    return Tips.invalid_bossbar.tm(sender);
                }
                Runnable execute = () -> {
                    boolean papi = arguments.papi;
                    String title = arguments.title;
                    for (CommandSender receiver : receivers) {
                        if (receiver instanceof Player) {
                            Player player = (Player) receiver;
                            String msg = papi ? PAPI.setPlaceholders(player, title) : title;

                            Component component = AdventureUtil.miniMessage(msg);
                            IBossBarWrapper bar = plugin.getBossBarFactory().create(component, arguments.color, arguments.style);
                            bar.addPlayer(player);
                            bar.setVisible(true);

                            plugin.getScheduler().runTaskLater(() -> {
                                bar.setVisible(false);
                                bar.removeAll();
                                if (arguments.postActions != null) {
                                    arguments.postActions.run(player);
                                }
                            }, arguments.duration);
                        }
                    }
                };
                if (arguments.delay > 0)  {
                    plugin.getScheduler().runTaskLater(execute, arguments.delay);
                } else {
                    execute.run();
                }
                return true;
            }
            if (args.length == 1 && "reload".equals(arg0)) {
                plugin.reloadConfig();
                return Tips.reload.tm(sender);
            }
            return Tips.help.tm(sender);
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
                if (argMessage.contains(arg0) || argAction.contains(arg0) || argTitle.contains(arg0)) {
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
