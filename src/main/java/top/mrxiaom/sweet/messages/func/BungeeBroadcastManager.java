package top.mrxiaom.sweet.messages.func;

import org.bukkit.entity.Player;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Bytes;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.commands.args.TextArguments;
import top.mrxiaom.sweet.messages.commands.receivers.BungeeAllReceivers;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AutoRegister
public class BungeeBroadcastManager extends AbstractModule {
    public BungeeBroadcastManager(SweetMessages plugin) {
        super(plugin);
    }

    @Override
    public void receiveBungee(String subChannel, DataInputStream in) throws IOException {
        if (subChannel.equals("SweetMessages")) {
            long outdate = in.readLong();
            if (System.currentTimeMillis() > outdate) return;
            String type = in.readUTF();
            if ("text".equals(type)) {
                boolean papi = in.readBoolean();
                long delay = in.readLong();
                boolean isActionMessage = in.readBoolean();
                List<String> lines = new ArrayList<>();
                int size = in.readInt();
                for (int i = 0; i < size; i++) {
                    lines.add(in.readUTF());
                }
                TextArguments arguments = new TextArguments(papi, delay, lines);
                arguments.isActionMessage = isActionMessage;
                arguments.execute(plugin, BungeeAllReceivers.INSTANCE.getList());
            }
        }
    }

    public void broadcastText(Player player, TextArguments arguments) {
        player.sendPluginMessage(plugin, "BungeeCord", Bytes.build(out -> {
            // 设定超时时间，如果3秒内没有接收，就不接收。
            // 以免很久都没人上过线的子服一有人上线就涌入一堆消息。
            out.writeLong(System.currentTimeMillis() + 3000);
            out.writeUTF("text");
            out.writeBoolean(arguments.papi);
            out.writeLong(arguments.delay);
            out.writeBoolean(arguments.isActionMessage);
            out.writeInt(arguments.lines.size());
            for (String line : arguments.lines) {
                out.writeUTF(line);
            }
        }, "Forward", "ALL", "SweetMessages"));
    }

    public static BungeeBroadcastManager inst() {
        return instanceOf(BungeeBroadcastManager.class);
    }
}
