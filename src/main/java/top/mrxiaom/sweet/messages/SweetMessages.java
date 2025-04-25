package top.mrxiaom.sweet.messages;
        
import top.mrxiaom.sweet.messages.nms.NMS;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.func.LanguageManager;
import top.mrxiaom.sweet.messages.utils.FoliaScheduler;

public class SweetMessages extends BukkitPlugin {
    public static SweetMessages getInstance() {
        return (SweetMessages) BukkitPlugin.getInstance();
    }
    private boolean supportBossBar;
    public SweetMessages() {
        super(options()
                .bungee(false)
                .adventure(true)
                .database(false)
                .reconnectDatabaseWhenReloadConfig(false)
                .scanIgnore("top.mrxiaom.sweet.messages.libs")
        );
        scheduler = new FoliaScheduler(this);
    }

    public boolean isSupportBossBar() {
        return supportBossBar;
    }

    @Override
    protected void beforeLoad() {
        supportBossBar = NMS.init(getLogger());
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
