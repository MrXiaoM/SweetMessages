package top.mrxiaom.sweet.messages.commands.receivers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class BungeeAllReceivers implements IReceivers {
    public static final BungeeAllReceivers INSTANCE = new BungeeAllReceivers();
    private BungeeAllReceivers() {}

    @Override
    public List<CommandSender> getList() {
        List<CommandSender> list = new ArrayList<>();
        list.add(Bukkit.getConsoleSender());
        list.addAll(Bukkit.getOnlinePlayers());
        return list;
    }
}
