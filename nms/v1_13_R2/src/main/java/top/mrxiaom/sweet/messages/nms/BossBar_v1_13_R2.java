package top.mrxiaom.sweet.messages.nms;

import com.google.gson.JsonElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.server.v1_13_R2.BossBattleServer;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import org.bukkit.craftbukkit.v1_13_R2.boss.CraftBossBar;
import org.bukkit.boss.BossBar;

public class BossBar_v1_13_R2 implements IBossBar {
    GsonComponentSerializer serializer = GsonComponentSerializer.gson();
    @Override
    public void setTitle(BossBar bossBar, Component component) {
        BossBattleServer nms = ((CraftBossBar) bossBar).getHandle();
        JsonElement json = serializer.serializeToTree(component);
        IChatBaseComponent result = IChatBaseComponent.ChatSerializer.a(json);
        nms.a(result);
    }
}
