package top.mrxiaom.sweet.messages.nms;

import net.kyori.adventure.text.Component;
import org.bukkit.boss.BossBar;

public interface IBossBar {
    void setTitle(BossBar bossBar, Component component);
}
