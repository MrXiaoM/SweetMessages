package top.mrxiaom.sweet.messages.nms;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.server.level.BossBattleServer;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_18_R2.boss.CraftBossBar;

public class BossBar_v1_18_R2 implements IBossBar {
    GsonComponentSerializer serializer = GsonComponentSerializer.gson();
    @Override
    public void setTitle(BossBar bossBar, Component component) {
        BossBattleServer nms = ((CraftBossBar) bossBar).getHandle();
        JsonElement json = serializer.serializeToTree(component);
        IChatMutableComponent result = IChatBaseComponent.ChatSerializer.a(json);
        nms.a(result);
    }

    public Component getTitle(BossBar bossBar) {
        BossBattleServer nms = ((CraftBossBar) bossBar).getHandle();
        if (nms == null) return null;
        String json = IChatBaseComponent.ChatSerializer.a(nms.j());
        return serializer.deserialize(json);
    }
}
