package top.mrxiaom.sweet.messages.bossbar;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import top.mrxiaom.sweet.messages.api.EnumBarColor;
import top.mrxiaom.sweet.messages.api.EnumBarStyle;
import top.mrxiaom.sweet.messages.api.IBossBarFactory;
import top.mrxiaom.sweet.messages.api.IBossBarWrapper;
import top.mrxiaom.sweet.messages.nms.IBossBar;
import top.mrxiaom.sweet.messages.nms.NMS;
import top.mrxiaom.sweet.messages.utils.Utils;

public class BukkitBossBarFactory implements IBossBarFactory {
    @Override
    public IBossBarWrapper create(Component title, EnumBarColor color, EnumBarStyle style) {
        BarColor colorImpl = toBukkit(color);
        BarStyle styleImpl = toBukkit(style);
        IBossBar nms = NMS.getBossBar();
        if (nms != null) {
            BossBar impl = Bukkit.createBossBar("", colorImpl, styleImpl);
            nms.setTitle(impl, title);
            return new BukkitBossBar(impl);
        } else {
            return new BukkitBossBar(Bukkit.createBossBar(Utils.toString(title), colorImpl, styleImpl));
        }
    }

    public static BarColor toBukkit(EnumBarColor color) {
        switch (color) {
            case PINK:
                return BarColor.PINK;
            case BLUE:
                return BarColor.BLUE;
            case RED:
                return BarColor.RED;
            case GREEN:
                return BarColor.GREEN;
            case YELLOW:
                return BarColor.YELLOW;
            case PURPLE:
                return BarColor.PURPLE;
            case WHITE:
                return BarColor.WHITE;
            default:
                throw new IllegalArgumentException("未知的 BarColor: " + color.name());
        }
    }
    public static EnumBarColor toAPI(BarColor color) {
        switch (color) {
            case PINK:
                return EnumBarColor.PINK;
            case BLUE:
                return EnumBarColor.BLUE;
            case RED:
                return EnumBarColor.RED;
            case GREEN:
                return EnumBarColor.GREEN;
            case YELLOW:
                return EnumBarColor.YELLOW;
            case PURPLE:
                return EnumBarColor.PURPLE;
            case WHITE:
                return EnumBarColor.WHITE;
            default:
                throw new IllegalArgumentException("未知的 BarColor: " + color.name());
        }
    }
    public static BarStyle toBukkit(EnumBarStyle style) {
        switch (style) {
            case SOLID:
                return BarStyle.SOLID;
            case SEGMENTED_6:
                return BarStyle.SEGMENTED_6;
            case SEGMENTED_10:
                return BarStyle.SEGMENTED_10;
            case SEGMENTED_12:
                return BarStyle.SEGMENTED_12;
            case SEGMENTED_20:
                return BarStyle.SEGMENTED_20;
            default:
                throw new IllegalArgumentException("未知的 BarStyle: " + style.name());
        }
    }
    public static EnumBarStyle toAPI(BarStyle style) {
        switch (style) {
            case SOLID:
                return EnumBarStyle.SOLID;
            case SEGMENTED_6:
                return EnumBarStyle.SEGMENTED_6;
            case SEGMENTED_10:
                return EnumBarStyle.SEGMENTED_10;
            case SEGMENTED_12:
                return EnumBarStyle.SEGMENTED_12;
            case SEGMENTED_20:
                return EnumBarStyle.SEGMENTED_20;
            default:
                throw new IllegalArgumentException("未知的 BarStyle: " + style.name());
        }
    }
}
