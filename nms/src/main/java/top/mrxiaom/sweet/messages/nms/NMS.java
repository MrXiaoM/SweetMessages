package top.mrxiaom.sweet.messages.nms;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class NMS {
    private static IBossBar bossBar;
    private static boolean loaded;
    private static final Map<String, String> VERSION_TO_REVISION = new HashMap<String, String>() {{
        put("1.20", "v1_20_R1");
        put("1.20.1", "v1_20_R1");
        put("1.20.2", "v1_20_R2");
        put("1.20.3", "v1_20_R3");
        put("1.20.4", "v1_20_R3");
        put("1.20.5", "v1_20_R4");
        put("1.20.6", "v1_20_R4");
        put("1.21", "v1_21_R1");
        put("1.21.1", "v1_21_R1");
        put("1.21.2", "v1_21_R2");
        put("1.21.3", "v1_21_R2");
        put("1.21.4", "v1_21_R3");
        put("1.21.5", "v1_21_R4");
        put("1.21.6", "v1_21_R5");
        put("1.21.7", "v1_21_R5");
    }};

    @SuppressWarnings("UnusedReturnValue")
    public static boolean init(Logger logger) {
        if (loaded) return true;
        String nmsVersion;
        // Thanks https://github.com/tr7zw/Item-NBT-API
        try {
            String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            logger.info("Found Minecraft: " + ver + "! Trying to find NMS support");
            nmsVersion = ver;
        } catch (Throwable e) {
            logger.info("Found Minecraft: " + Bukkit.getServer().getBukkitVersion().split("-")[0] + "! Trying to find NMS support");
            String ver = Bukkit.getServer().getBukkitVersion().split("-")[0];
            nmsVersion = VERSION_TO_REVISION.getOrDefault(ver, "unknown");
        }
        try {
            Class<?> classLivingEntity = Class.forName("top.mrxiaom.sweet.messages.nms.BossBar_" + nmsVersion);
            bossBar = (IBossBar) classLivingEntity.getConstructor().newInstance();
            loaded = true;
        } catch (Throwable ignored) {
        }

        if (loaded) {
            logger.info("NMS support '" + nmsVersion + "' loaded!");
        } else {
            bossBar = null;
            logger.warning("This Server-Version(" + Bukkit.getServer().getBukkitVersion() + ", " + nmsVersion + ") is not supported by this plugin!");
        }

        return loaded;
    }

    @Nullable
    public static IBossBar getBossBar() {
        return bossBar;
    }
}
