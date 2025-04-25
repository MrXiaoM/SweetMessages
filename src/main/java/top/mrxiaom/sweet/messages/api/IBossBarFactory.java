package top.mrxiaom.sweet.messages.api;

import net.kyori.adventure.text.Component;

public interface IBossBarFactory {
    IBossBarWrapper create(Component title, EnumBarColor color, EnumBarStyle style);
}
