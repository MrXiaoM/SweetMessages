package top.mrxiaom.sweet.messages;

import top.mrxiaom.pluginbase.func.language.IHolderAccessor;
import top.mrxiaom.pluginbase.func.language.Language;
import top.mrxiaom.pluginbase.func.language.LanguageEnumAutoHolder;

import java.util.List;

import static top.mrxiaom.pluginbase.func.language.LanguageEnumAutoHolder.wrap;

@Language(prefix = "tips.")
public enum Tips implements IHolderAccessor {
    reload("&a配置文件已重载"),
    help("&b&lSweetMessages 帮助&r",
            "&f/smsgs message <接收者> [选项] <消息> &e发送聊天消息",
            "&f/smsgs actionbar <接收者> [选项] <消息> &e发送 ActionBar 消息",
            "&f/smsgs title <接收者> [选项] <标题\\n副标题> &e发送标题消息",
            "&7关于命令选项的用法，请前往<u><hover:show_text:打开文档网址><click:open_url:https://plugins.mcio.dev/docs/messages/commands>插件文档</click></hover></u>查看",
            "&f/smsgs reload &e重载配置文件"
    ),
    invalid_selector("&e输入的消息接收者 %s&r &e无效"),
    invalid_content("&e请输入消息内容"),
    invalid_title("&e请输入标题内容"),
    ;

    Tips(String defaultValue) {
        holder = wrap(this, defaultValue);
    }
    Tips(String... defaultValue) {
        holder = wrap(this, defaultValue);
    }
    Tips(List<String> defaultValue) {
        holder = wrap(this, defaultValue);
    }
    // 4. 添加字段 holder 以及它的 getter
    private final LanguageEnumAutoHolder<Tips> holder;
    public LanguageEnumAutoHolder<Tips> holder() {
        return holder;
    }
}
