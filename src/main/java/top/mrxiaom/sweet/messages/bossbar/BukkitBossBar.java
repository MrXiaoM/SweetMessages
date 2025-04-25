package top.mrxiaom.sweet.messages.bossbar;

import net.kyori.adventure.text.Component;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import top.mrxiaom.sweet.messages.api.EnumBarColor;
import top.mrxiaom.sweet.messages.api.EnumBarStyle;
import top.mrxiaom.sweet.messages.api.IBossBarWrapper;
import top.mrxiaom.sweet.messages.nms.NMS;

import java.util.List;

import static top.mrxiaom.sweet.messages.bossbar.BukkitBossBarFactory.toBukkit;

public class BukkitBossBar implements IBossBarWrapper {
    BossBar impl;
    protected BukkitBossBar(BossBar impl) {
        this.impl = impl;
    }

    public void setTitle(Component component) {
        NMS.getBossBar().setTitle(impl, component);
    }

    public void setColor(EnumBarColor color) {
        impl.setColor(toBukkit(color));
    }

    public void setStyle(EnumBarStyle style) {
        impl.setStyle(toBukkit(style));
    }

    public double getProgress() {
        return impl.getProgress();
    }

    public void setProgress(double v) {
        impl.setProgress(v);
    }

    public void addPlayer(Player player) {
        impl.addPlayer(player);
    }

    public void removePlayer(Player player) {
        impl.removePlayer(player);
    }

    public List<Player> getPlayers() {
        return impl.getPlayers();
    }

    public void removeAll() {
        impl.removeAll();
    }

    public void setVisible(boolean visible) {
        impl.setVisible(visible);
    }

    public boolean isVisible() {
        return impl.isVisible();
    }
}
