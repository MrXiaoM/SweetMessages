package top.mrxiaom.sweet.messages.utils;

import net.kyori.adventure.text.Component;
import top.mrxiaom.pluginbase.utils.adventure.serializer.legacy.LegacyComponentSerializer;

public class Utils {
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();

    public static String toString(Component component) {
        return legacy.serialize(component);
    }

    public static Component toComponent(String legacyText) {
        return legacy.deserialize(legacyText);
    }
}
