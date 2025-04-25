package top.mrxiaom.sweet.messages.nms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.server.v1_12_R1.BossBattleServer;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import org.bukkit.craftbukkit.v1_12_R1.boss.CraftBossBar;
import org.bukkit.boss.BossBar;

import java.lang.reflect.Field;

public class BossBar_v1_12_R1 implements IBossBar {
    GsonComponentSerializer serializer = GsonComponentSerializer.gson();
    private final Field handle;
    public BossBar_v1_12_R1() throws ReflectiveOperationException {
        handle = CraftBossBar.class.getDeclaredField("handle");
        handle.setAccessible(true);
    }
    public BossBattleServer getHandle(BossBar bossBar) {
        try {
            return (BossBattleServer) handle.get(bossBar);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
    @Override
    public void setTitle(BossBar bossBar, Component component) {
        BossBattleServer nms = getHandle(bossBar);
        if (nms == null) return;
        String json = serializer.serialize(component);
        IChatBaseComponent result = IChatBaseComponent.ChatSerializer.a(json);
        nms.a(result);
    }

    public Component getTitle(BossBar bossBar) {
        BossBattleServer nms = getHandle(bossBar);
        if (nms == null) return null;
        String json = IChatBaseComponent.ChatSerializer.a(nms.e());
        return serializer.deserialize(json);
    }
}
