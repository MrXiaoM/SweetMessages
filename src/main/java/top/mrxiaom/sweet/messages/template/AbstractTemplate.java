package top.mrxiaom.sweet.messages.template;

import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.Tips;
import top.mrxiaom.sweet.messages.api.EnumBroadcastMethod;
import top.mrxiaom.sweet.messages.commands.args.IArguments;
import top.mrxiaom.sweet.messages.commands.args.TextArguments;
import top.mrxiaom.sweet.messages.commands.receivers.BungeeAllReceivers;
import top.mrxiaom.sweet.messages.commands.receivers.IReceivers;
import top.mrxiaom.sweet.messages.func.BroadcastManager;

public abstract class AbstractTemplate {
    private final String id;
    protected AbstractTemplate(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public abstract IArguments arguments();
    public void execute(SweetMessages plugin, CommandSender sender, IReceivers receivers) {
        IArguments arguments = arguments();
        if (receivers instanceof BungeeAllReceivers) {
            BroadcastManager manager = BroadcastManager.inst();
            Player whoever = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            if (whoever == null && manager.getMethod().equals(EnumBroadcastMethod.BUNGEE_CORD)) {
                Tips.bungeecord__no_players.tm(sender);
                return;
            }
            if (arguments instanceof TextArguments) {
                arguments.execute(plugin, receivers.getList());
                manager.broadcastText(whoever, (TextArguments) arguments);
                return;
            }
            Tips.bungeecord__not_supported.tm(sender);
            return;
        }
        arguments.execute(plugin, receivers.getList());
    }
}
