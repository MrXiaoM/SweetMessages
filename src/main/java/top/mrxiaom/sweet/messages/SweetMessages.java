package top.mrxiaom.sweet.messages;
        
import top.mrxiaom.pluginbase.BukkitPlugin;

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
                .disableDefaultConfig(true)
                .scanIgnore("top.mrxiaom.sweet.messages.libs")
        );
    }


    @Override
    protected void afterEnable() {
        getLogger().info("SweetMessages 加载完毕");
    }
}
