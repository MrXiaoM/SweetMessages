package top.mrxiaom.sweet.messages.commands;
        
import com.google.common.collect.Iterables;
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
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.Tips;
import top.mrxiaom.sweet.messages.commands.args.BossBarArguments;
import top.mrxiaom.sweet.messages.commands.args.IArguments;
import top.mrxiaom.sweet.messages.commands.args.TextArguments;
import top.mrxiaom.sweet.messages.commands.args.TitleArguments;
import top.mrxiaom.sweet.messages.commands.receivers.BukkitReceivers;
import top.mrxiaom.sweet.messages.commands.receivers.BungeeAllReceivers;
import top.mrxiaom.sweet.messages.commands.receivers.IReceivers;
import top.mrxiaom.sweet.messages.func.AbstractModule;
import top.mrxiaom.sweet.messages.func.BungeeBroadcastManager;
import top.mrxiaom.sweet.messages.func.TemplateManager;
import top.mrxiaom.sweet.messages.template.AbstractTemplate;

import java.util.*;

import static top.mrxiaom.sweet.messages.commands.args.IArguments.parse;

@AutoRegister
public class CommandMain extends AbstractModule implements CommandExecutor, TabCompleter, Listener {
    public CommandMain(SweetMessages plugin) {
        super(plugin);
        registerCommand("sweetmessages", this);
        listOpArg0.add("template");
        listOpArg0.addAll(argMessage);
        listOpArg0.addAll(argAction);
        listOpArg0.addAll(argTitle);
    }

    /**
     * 仅实现部分简单的目标选择器，参考资料来自
     * <a href="https://zh.minecraft.wiki/w/%E7%9B%AE%E6%A0%87%E9%80%89%E6%8B%A9%E5%99%A8">Minecraft Wiki</a>
     */
    @SuppressWarnings("IfCanBeSwitch")
    public IReceivers parseReceivers(CommandSender sender, String s) {
        if (s.equals("@bc") || s.equals("@bungeecord") || s.equals("@broadcast")) {
            return BungeeAllReceivers.INSTANCE;
        }
        if (s.equals("@a") || s.equals("@all") || s.equals("@e")) { // 所有在线玩家
            List<CommandSender> receivers = new ArrayList<>();
            receivers.add(Bukkit.getConsoleSender()); // 为了后台也能收到，把它也加进去，留个底
            receivers.addAll(Bukkit.getOnlinePlayers());
            return new BukkitReceivers(receivers);
        }
        if (s.equals("@c") || s.equals("@console")) {
            return new BukkitReceivers(Bukkit.getConsoleSender());
        }
        if (s.equals("@s") || s.equals("@self")) { // 自己
            return new BukkitReceivers(sender);
        }
        if (s.equals("@p") || s.equals("@player")) { // 距离自己最近的玩家
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
                return new BukkitReceivers(target);
            }
        }
        if (s.equals("@r") || s.equals("@random")) { // 随机玩家
            List<CommandSender> list = Lists.newArrayList(Bukkit.getOnlinePlayers());
            if (list.isEmpty()) return null;
            if (list.size() == 1) return new BukkitReceivers(list);
            int index = new Random().nextInt(list.size());
            return new BukkitReceivers(list.get(index));
        }
        if (s.startsWith("@")) { // 其它选择器均不支持
            return null;
        }
        if (s.contains(",")) { // 多个玩家
            String[] split = s.split(",");
            List<Player> players = Util.getOnlinePlayersByName(Arrays.asList(split));
            if (players.isEmpty()) return null;
            return new BukkitReceivers(new ArrayList<>(players));
        }
        return Util.getOnlinePlayer(s)
                .map(it -> (CommandSender) it)
                .map(Lists::newArrayList)
                .map(BukkitReceivers::new)
                .orElse(null);
    }

    List<String> argMessage = Lists.newArrayList("message", "msg", "m");
    List<String> argAction = Lists.newArrayList("actionbar", "action", "a");
    List<String> argTitle = Lists.newArrayList("title", "t");
    List<String> argBossBar = Lists.newArrayList("bossbar", "bar", "b");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.isOp()) {
            String arg0 = args.length > 0 ? args[0].toLowerCase() : "";
            if (args.length >= 3 && "template".equals(arg0)) {
                IReceivers receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return Tips.invalid_selector.tm(sender, args[1]);
                }
                AbstractTemplate template = TemplateManager.inst().get(args[2]);
                if (template == null) {
                    return Tips.invalid_template.tm(sender, args[2]);
                }
                template.execute(plugin, sender, receivers);
                return true;
            }
            if (args.length >= 2 && argMessage.contains(arg0)) {
                IReceivers receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return Tips.invalid_selector.tm(sender, args[1]);
                }
                TextArguments arguments = parse(TextArguments::parser, args, 2);
                if (arguments == null) {
                    return Tips.invalid_content.tm(sender);
                }
                execute(sender, receivers, arguments);
                return true;
            }
            if (args.length >= 2 && argAction.contains(arg0)) {
                IReceivers receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return Tips.invalid_selector.tm(sender, args[1]);
                }
                TextArguments arguments = parse(TextArguments::parser, args, 2);
                if (arguments == null) {
                    return Tips.invalid_content.tm(sender);
                }
                arguments.isActionMessage = true;
                execute(sender, receivers, arguments);
                return true;
            }
            if (args.length >= 2 && argTitle.contains(arg0)) {
                IReceivers receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return Tips.invalid_selector.tm(sender, args[1]);
                }
                TitleArguments arguments = parse(TitleArguments::parser, args, 2);
                if (arguments == null) {
                    return Tips.invalid_title.tm(sender);
                }
                execute(sender, receivers, arguments);
                return true;
            }
            if (args.length >= 2 && argBossBar.contains(arg0)) {
                IReceivers receivers = parseReceivers(sender, args[1]);
                if (receivers == null) {
                    return Tips.invalid_selector.tm(sender, args[1]);
                }
                BossBarArguments arguments = parse(BossBarArguments::parser, args, 2);
                if (arguments == null) {
                    return Tips.invalid_bossbar.tm(sender);
                }
                execute(sender, receivers, arguments);
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

    private void execute(CommandSender sender, IReceivers receivers, IArguments arguments) {
        if (receivers instanceof BungeeAllReceivers) {
            BungeeBroadcastManager manager = BungeeBroadcastManager.inst();
            Player whoever = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            if (whoever == null) {
                Tips.bungeecord__no_players.tm(sender);
                return;
            }
            if (arguments instanceof TextArguments) {
                arguments.execute(plugin, receivers.getList());
                manager.broadcastText(whoever, (TextArguments) arguments);
                return;
            }
            if (arguments instanceof TitleArguments) {
                arguments.execute(plugin, receivers.getList());
                manager.broadcastTitle(whoever, (TitleArguments) arguments);
                return;
            }
            if (arguments instanceof BossBarArguments) {
                arguments.execute(plugin, receivers.getList());
                manager.broadcastBossBar(whoever, (BossBarArguments) arguments);
                return;
            }
            Tips.bungeecord__not_supported.tm(sender);
            return;
        }
        arguments.execute(plugin, receivers.getList());
    }

    private static final List<String> emptyList = Lists.newArrayList();
    private static final List<String> listOpArg0 = new ArrayList<>();
    private static final List<String> listTargetArg1 = Lists.newArrayList("@bc", "@a", "@e", "@s", "@p", "@r");
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (sender.isOp()) {
            if (args.length == 1) {
                return startsWith(listOpArg0, args[0]);
            }
            if (args.length == 2) {
                String arg0 = args[0].toLowerCase();
                if ("template".equals(arg0) || argMessage.contains(arg0) || argAction.contains(arg0) || argTitle.contains(arg0)) {
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
            if (args.length == 3) {
                String arg0 = args[0].toLowerCase();
                if ("template".equals(arg0)) {
                    return startsWith(TemplateManager.inst().keys(), args[2]);
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
