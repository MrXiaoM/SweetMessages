package top.mrxiaom.sweet.messages;
        
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
        if (NMS.init(getLogger())) { // 1.9+ Bukkit 添加 BOSS 血条接口
            bossBarFactory = new BukkitBossBarFactory();
        } else {
            // TODO: 添加一个 LegacyBossBarFactory，可能要找一些 1.7、1.8 时代用末影龙、凋灵实体做 BOSS 血条的库
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
