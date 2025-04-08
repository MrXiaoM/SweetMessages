package top.mrxiaom.sweet.messages.func;
        
import top.mrxiaom.sweet.messages.SweetMessages;

@SuppressWarnings({"unused"})
public abstract class AbstractPluginHolder extends top.mrxiaom.pluginbase.func.AbstractPluginHolder<SweetMessages> {
    public AbstractPluginHolder(SweetMessages plugin) {
        super(plugin);
    }

    public AbstractPluginHolder(SweetMessages plugin, boolean register) {
        super(plugin, register);
    }
}
