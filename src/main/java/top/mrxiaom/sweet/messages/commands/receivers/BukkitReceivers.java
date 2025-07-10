package top.mrxiaom.sweet.messages.commands.receivers;

import com.google.common.collect.Lists;
import org.bukkit.command.CommandSender;

import java.util.List;

public class BukkitReceivers implements IReceivers {
    private final List<CommandSender> list;
    public BukkitReceivers(CommandSender... list) {
        this(Lists.newArrayList(list));
    }
    public BukkitReceivers(List<CommandSender> list) {
        this.list = list;
    }

    @Override
    public List<CommandSender> getList() {
        return list;
    }
}
