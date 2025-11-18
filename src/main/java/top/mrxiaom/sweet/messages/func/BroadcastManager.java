package top.mrxiaom.sweet.messages.func;

import com.google.common.collect.Lists;
import com.google.common.io.ByteArrayDataOutput;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.actions.ActionProviders;
import top.mrxiaom.pluginbase.api.IAction;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Bytes;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.api.EnumBarColor;
import top.mrxiaom.sweet.messages.api.EnumBarStyle;
import top.mrxiaom.sweet.messages.api.EnumBroadcastMethod;
import top.mrxiaom.sweet.messages.commands.args.BossBarArguments;
import top.mrxiaom.sweet.messages.commands.args.TextArguments;
import top.mrxiaom.sweet.messages.commands.args.TitleArguments;
import top.mrxiaom.sweet.messages.commands.receivers.BungeeAllReceivers;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AutoRegister
public class BroadcastManager extends AbstractModule {
    private EnumBroadcastMethod method;
    public BroadcastManager(SweetMessages plugin) {
        super(plugin);
    }

    @Override
    public void receiveBungee(String subChannel, DataInputStream in) throws IOException {
        if (subChannel.equals("SweetMessages")) {
            onReceiveMessage(in);
        }
    }

    public EnumBroadcastMethod getMethod() {
        return method;
    }

    @Override
    public void reloadConfig(MemoryConfiguration config) {
        method = Util.valueOr(EnumBroadcastMethod.class, config.getString("broadcast.method"), EnumBroadcastMethod.BUNGEE_CORD);
    }

    public void onReceiveMessage(DataInputStream in) throws IOException {
        long outdate = in.readLong();
        if (System.currentTimeMillis() > outdate) return;
        String group = in.readUTF();
        if (!group.equals(plugin.getBroadcastGroup())) return;
        String type = in.readUTF();
        switch (type) {
            case "text": {
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
                return;
            }
            case "title": {
                boolean papi = in.readBoolean();
                long delay = in.readLong();
                int fadeIn = in.readInt();
                int stay = in.readInt();
                int fadeOut = in.readInt();
                String title = in.readUTF();
                String subTitle = in.readUTF();
                TitleArguments arguments = new TitleArguments(papi, delay, fadeIn, stay, fadeOut, title, subTitle);
                arguments.execute(plugin, BungeeAllReceivers.INSTANCE.getList());
                return;
            }
            case "bossbar": {
                boolean papi = in.readBoolean();
                long delay = in.readLong();
                long duration = in.readLong();
                EnumBarColor color = Util.valueOr(EnumBarColor.class, in.readUTF(), EnumBarColor.WHITE);
                EnumBarStyle style = Util.valueOr(EnumBarStyle.class, in.readUTF(), EnumBarStyle.SOLID);
                String title = in.readUTF();
                IAction postActions = null;
                String postActionsStr = in.readUTF();
                if (!postActionsStr.isEmpty()) {
                    List<IAction> actions = ActionProviders.loadActions(Lists.newArrayList(postActionsStr));
                    if (!actions.isEmpty()) {
                        postActions = actions.get(0);
                    }
                } else {
                    postActionsStr = null;
                }
                BossBarArguments arguments = new BossBarArguments(papi, delay, duration, color, style, title, postActions, postActionsStr);
                arguments.execute(plugin, BungeeAllReceivers.INSTANCE.getList());
                return;
            }
        }
        warn("[bungee] 接收到未知的广播类型 " + type + "，请检查插件是否已更新到最新版");
    }

    private void initBroadcast(DataOutputStream out, String type) throws IOException {
        // 设定超时时间，如果3秒内没有接收，就不接收。
        // 以免很久都没人上过线的子服一有人上线就涌入一堆消息。
        out.writeLong(System.currentTimeMillis() + 3000);
        out.writeUTF(plugin.getBroadcastGroup());
        out.writeUTF(type);
    }

    public void broadcastText(Player player, TextArguments arguments) {
        byte[] packet = build(out -> {
            initBroadcast(out, "text");
            out.writeBoolean(arguments.papi);
            out.writeLong(arguments.delay);
            out.writeBoolean(arguments.isActionMessage);
            out.writeInt(arguments.lines.size());
            for (String line : arguments.lines) {
                out.writeUTF(line);
            }
        });
        switch (method) {
            case DATABASE:
                plugin.getMessageBroadcastDatabase().insert(packet);
                break;
            case BUNGEE_CORD:
                player.sendPluginMessage(plugin, "BungeeCord", buildBungee(packet, "Forward", "ALL", "SweetMessages"));
                break;
        }
    }

    public void broadcastTitle(Player player, TitleArguments arguments) {
        byte[] packet = build(out -> {
            initBroadcast(out, "title");
            out.writeBoolean(arguments.papi);
            out.writeLong(arguments.delay);
            out.writeInt(arguments.fadeIn);
            out.writeInt(arguments.stay);
            out.writeInt(arguments.fadeOut);
            out.writeUTF(arguments.title);
            out.writeUTF(arguments.subTitle);
        });
        switch (method) {
            case DATABASE:
                plugin.getMessageBroadcastDatabase().insert(packet);
                break;
            case BUNGEE_CORD:
                player.sendPluginMessage(plugin, "BungeeCord", buildBungee(packet, "Forward", "ALL", "SweetMessages"));
                break;
        }
    }

    public void broadcastBossBar(Player player, BossBarArguments arguments) {
        byte[] packet = build(out -> {
            initBroadcast(out, "bossbar");
            out.writeBoolean(arguments.papi);
            out.writeLong(arguments.delay);
            out.writeLong(arguments.duration);
            out.writeUTF(arguments.color.name());
            out.writeUTF(arguments.style.name());
            out.writeUTF(arguments.title);
            out.writeUTF(arguments.postActionsRaw == null ? "" : arguments.postActionsRaw);
        });
        switch (method) {
            case DATABASE:
                plugin.getMessageBroadcastDatabase().insert(packet);
                break;
            case BUNGEE_CORD:
                player.sendPluginMessage(plugin, "BungeeCord", buildBungee(packet, "Forward", "ALL", "SweetMessages"));
                break;
        }
    }

    private static byte @Nullable [] build(Bytes.DataConsumer data) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             DataOutputStream msg = new DataOutputStream(output)) {
            data.accept(msg);
            return output.toByteArray();
        } catch (IOException e) {
            BukkitPlugin.getInstance().warn("构建 BungeeCord 插件消息时出现一个异常", e);
        }
        return null;
    }

    @SuppressWarnings("SameParameterValue")
    private static byte[] buildBungee(byte[] data, String subChannel, String... arguments) {
        ByteArrayDataOutput out = Bytes.newDataOutput();
        out.writeUTF(subChannel);
        for (String argument : arguments) {
            out.writeUTF(argument);
        }
        if (data != null) {
            out.write(data.length);
            out.write(data);
        }
        return out.toByteArray();
    }

    public static BroadcastManager inst() {
        return instanceOf(BroadcastManager.class);
    }
}
