package top.mrxiaom.sweet.messages.nms;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.level.ServerBossEvent;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.boss.CraftBossBar;

import java.util.Optional;

public class BossBar_26_1 implements IBossBar {
    GsonComponentSerializer serializer = GsonComponentSerializer.gson();
    @Override
    public void setTitle(BossBar bossBar, Component component) {
        ServerBossEvent nms = ((CraftBossBar) bossBar).getHandle();
        JsonElement json = serializer.serializeToTree(component);
        Optional<net.minecraft.network.chat.Component> result = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, json).result();
        result.ifPresent(nms::setName);
    }

    public Component getTitle(BossBar bossBar) {
        ServerBossEvent nms = ((CraftBossBar) bossBar).getHandle();
        if (nms == null) return null;
        Optional<JsonElement> result = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, nms.getName()).result();
        return result.map(serializer::deserializeFromTree).orElse(null);
    }
}
