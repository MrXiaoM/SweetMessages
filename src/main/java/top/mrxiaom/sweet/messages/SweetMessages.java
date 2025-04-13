package top.mrxiaom.sweet.messages;
        
import top.mrxiaom.pluginbase.BukkitPlugin;
import top.mrxiaom.pluginbase.func.LanguageManager;

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
