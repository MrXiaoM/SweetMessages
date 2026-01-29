package top.mrxiaom.sweet.messages.commands.receivers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BungeeAllReceivers implements IReceivers {
    public static final BungeeAllReceivers INSTANCE = new BungeeAllReceivers(false);
    public static final BungeeAllReceivers ONLY_BROADCAST = new BungeeAllReceivers(true);
    private final boolean ignoreCurrentServer;
    private BungeeAllReceivers(boolean ignoreCurrentServer) {
        this.ignoreCurrentServer = ignoreCurrentServer;
    }

    public boolean isIgnoreCurrentServer() {
        return ignoreCurrentServer;
    }

    @Override
    public List<CommandSender> getList() {
        List<CommandSender> list = new ArrayList<>();
        list.add(Bukkit.getConsoleSender());
        list.addAll(Bukkit.getOnlinePlayers());
        return list;
    }
}
