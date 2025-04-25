package top.mrxiaom.sweet.messages.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Utils {
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();

    public static String toString(Component component) {
        return legacy.serialize(component);
    }

    public static Component toComponent(String legacyText) {
        return legacy.deserialize(legacyText);
    }
}
