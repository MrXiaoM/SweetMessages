package top.mrxiaom.sweet.messages.bossbar;

import net.kyori.adventure.text.Component;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import top.mrxiaom.sweet.messages.api.EnumBarColor;
import top.mrxiaom.sweet.messages.api.EnumBarStyle;
import top.mrxiaom.sweet.messages.api.IBossBarWrapper;
import top.mrxiaom.sweet.messages.nms.IBossBar;
import top.mrxiaom.sweet.messages.nms.NMS;
import top.mrxiaom.sweet.messages.utils.Utils;

import java.util.List;

import static top.mrxiaom.sweet.messages.bossbar.BukkitBossBarFactory.toAPI;
import static top.mrxiaom.sweet.messages.bossbar.BukkitBossBarFactory.toBukkit;

public class BukkitBossBar implements IBossBarWrapper {
    BossBar impl;
    IBossBar nms = NMS.getBossBar();
    protected BukkitBossBar(BossBar impl) {
        this.impl = impl;
    }

    @Override
    public Component getTitle() {
        if (nms != null) {
            return nms.getTitle(impl);
        } else {
            return Utils.toComponent(impl.getTitle());
        }
    }

    @Override
    public EnumBarColor getColor() {
        return toAPI(impl.getColor());
    }

    @Override
    public EnumBarStyle getStyle() {
        return toAPI(impl.getStyle());
    }

    public void setTitle(Component component) {
        if (nms != null) {
            nms.setTitle(impl, component);
        } else {
            impl.setTitle(Utils.toString(component));
        }
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
