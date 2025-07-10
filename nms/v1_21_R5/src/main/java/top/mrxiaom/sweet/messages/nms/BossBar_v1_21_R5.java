package top.mrxiaom.sweet.messages.nms;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.server.level.BossBattleServer;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_21_R5.boss.CraftBossBar;

import java.util.Optional;

public class BossBar_v1_21_R5 implements IBossBar {
    GsonComponentSerializer serializer = GsonComponentSerializer.gson();
    @Override
    public void setTitle(BossBar bossBar, Component component) {
        BossBattleServer nms = ((CraftBossBar) bossBar).getHandle();
        JsonElement json = serializer.serializeToTree(component);
        Optional<IChatBaseComponent> result = ComponentSerialization.a.parse(JsonOps.INSTANCE, json).result();
        result.ifPresent(nms::a);
    }

    public Component getTitle(BossBar bossBar) {
        BossBattleServer nms = ((CraftBossBar) bossBar).getHandle();
        if (nms == null) return null;
        Optional<JsonElement> result = ComponentSerialization.a.encodeStart(JsonOps.INSTANCE, nms.j()).result();
        return result.map(serializer::deserializeFromTree).orElse(null);
    }
}
