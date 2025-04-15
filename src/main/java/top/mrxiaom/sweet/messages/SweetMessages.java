package top.mrxiaom.sweet.messages;
        
import org.bukkit.Bukkit;
import top.mrxiaom.sweet.messages.nms.NMS;
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.func.LanguageManager;
import top.mrxiaom.sweet.messages.utils.FoliaScheduler;

public class SweetMessages extends BukkitPlugin {
    public static SweetMessages getInstance() {
        return (SweetMessages) BukkitPlugin.getInstance();
    }

    public SweetMessages() {
        super(options()
                .bungee(false)
                .adventure(true)
                .database(false)
                .reconnectDatabaseWhenReloadConfig(false)
                .vaultEconomy(false)
                .scanIgnore("top.mrxiaom.sweet.messages.libs")
        );
        scheduler = new FoliaScheduler(this);
    }

    @Override
    @SuppressWarnings({"deprecation"})
    public void onEnable() {
        if (!NMS.init(getLogger())) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        super.onEnable();
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
