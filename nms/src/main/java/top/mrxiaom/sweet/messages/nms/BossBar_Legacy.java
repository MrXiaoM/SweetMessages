package top.mrxiaom.sweet.messages.nms;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.boss.BossBar;

public class BossBar_Legacy implements IBossBar {
    LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();
    @Override
    public void setTitle(BossBar bossBar, Component component) {
        bossBar.setTitle(legacy.serialize(component));
    }

    @Override
    public Component getTitle(BossBar bossBar) {
        return legacy.deserialize(bossBar.getTitle());
    }
}
