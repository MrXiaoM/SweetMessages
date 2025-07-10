package top.mrxiaom.sweet.messages;

import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.pluginbase.utils.scheduler.FoliaLibScheduler;
import top.mrxiaom.sweet.messages.api.IBossBarFactory;
import top.mrxiaom.sweet.messages.bossbar.BukkitBossBarFactory;
import top.mrxiaom.sweet.messages.nms.NMS;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.func.LanguageManager;

public class SweetMessages extends BukkitPlugin {
    public static SweetMessages getInstance() {
        return (SweetMessages) BukkitPlugin.getInstance();
    }
    private IBossBarFactory bossBarFactory;
    public SweetMessages() {
        super(options()
                .bungee(false)
                .adventure(true)
                .database(false)
                .reconnectDatabaseWhenReloadConfig(false)
                .scanIgnore("top.mrxiaom.sweet.messages.libs")
        );
        scheduler = new FoliaLibScheduler(this);
    }

    public IBossBarFactory getBossBarFactory() {
        return bossBarFactory;
    }

    @Override
    protected void beforeLoad() {
        NMS.init(getLogger());
        // 1.9+ Bukkit 添加 BOSS 血条接口
        if (Util.isPresent("org.bukkit.boss.BossBar")) {
            bossBarFactory = new BukkitBossBarFactory();
        } else {
            warn("当前版本不支持显示 BOSS 血条");
        }
    }

    @Override
    protected void beforeEnable() {
        LanguageManager.inst()
                .setLangFile("messages.yml")
                .register(Tips.class, Tips::holder);
    }

    @Override
    protected void afterEnable() {
        getLogger().info("SweetMessages 加载完毕");
    }
}
