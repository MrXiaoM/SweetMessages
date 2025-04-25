package top.mrxiaom.sweet.messages.api;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.List;

public interface IBossBarWrapper {
    void setTitle(Component component);
    void setColor(EnumBarColor color);
    void setStyle(EnumBarStyle style);
    double getProgress();
    void setProgress(double v);
    void addPlayer(Player player);
    void removePlayer(Player player);
    List<Player> getPlayers();
    void removeAll();
    void setVisible(boolean visible);
    boolean isVisible();
}
