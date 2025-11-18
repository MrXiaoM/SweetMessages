package top.mrxiaom.sweet.messages;

import org.bukkit.configuration.file.FileConfiguration;
import top.mrxiaom.pluginbase.resolver.DefaultLibraryResolver;
import top.mrxiaom.pluginbase.utils.ClassLoaderWrapper;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.pluginbase.utils.scheduler.FoliaLibScheduler;
import top.mrxiaom.sweet.messages.api.IBossBarFactory;
import top.mrxiaom.sweet.messages.bossbar.BukkitBossBarFactory;
import top.mrxiaom.sweet.messages.database.MessageBroadcastDatabase;
import top.mrxiaom.sweet.messages.nms.NMS;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.func.LanguageManager;

import java.io.File;
import java.net.URL;
import java.util.List;

public class SweetMessages extends BukkitPlugin {
    public static SweetMessages getInstance() {
        return (SweetMessages) BukkitPlugin.getInstance();
    }
    private IBossBarFactory bossBarFactory;
    private MessageBroadcastDatabase messageBroadcastDatabase;
    private String broadcastGroup;
    public SweetMessages() throws Exception {
        super(options()
                .bungee(true)
                .adventure(true)
                .database(true)
                .reconnectDatabaseWhenReloadConfig(false)
                .scanIgnore("top.mrxiaom.sweet.messages.libs")
        );
        scheduler = new FoliaLibScheduler(this);

        info("正在检查依赖库状态");
        File librariesDir = ClassLoaderWrapper.isSupportLibraryLoader
                ? new File("libraries")
                : new File(this.getDataFolder(), "libraries");
        DefaultLibraryResolver resolver = new DefaultLibraryResolver(getLogger(), librariesDir);

        resolver.addResolvedLibrary(BuildConstants.RESOLVED_LIBRARIES);

        List<URL> libraries = resolver.doResolve();
        info("正在添加 " + libraries.size() + " 个依赖库到类加载器");
        for (URL library : libraries) {
            this.classLoader.addURL(library);
        }
    }

    public IBossBarFactory getBossBarFactory() {
        return bossBarFactory;
    }

    public MessageBroadcastDatabase getMessageBroadcastDatabase() {
        return messageBroadcastDatabase;
    }

    public String getBroadcastGroup() {
        return broadcastGroup;
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
        this.options.registerDatabase(
                this.messageBroadcastDatabase = new MessageBroadcastDatabase(this)
        );
    }

    @Override
    protected void beforeReloadConfig(FileConfiguration config) {
        broadcastGroup = config.getString("broadcast.group", "global");
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
