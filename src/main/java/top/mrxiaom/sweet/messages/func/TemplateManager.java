package top.mrxiaom.sweet.messages.func;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;
import top.mrxiaom.pluginbase.func.AutoRegister;
import top.mrxiaom.pluginbase.utils.Util;
import top.mrxiaom.sweet.messages.SweetMessages;
import top.mrxiaom.sweet.messages.commands.args.BossBarArguments;
import top.mrxiaom.sweet.messages.commands.args.IArguments;
import top.mrxiaom.sweet.messages.commands.args.TextArguments;
import top.mrxiaom.sweet.messages.commands.args.TitleArguments;
import top.mrxiaom.sweet.messages.template.AbstractTemplate;
import top.mrxiaom.sweet.messages.template.BossBarTemplate;
import top.mrxiaom.sweet.messages.template.TextTemplate;
import top.mrxiaom.sweet.messages.template.TitleTemplate;

import java.io.File;
import java.util.*;

@AutoRegister
public class TemplateManager extends AbstractModule {
    Map<String, AbstractTemplate> templates = new HashMap<>();
    public TemplateManager(SweetMessages plugin) {
        super(plugin);
    }

    public Set<String> keys() {
        return templates.keySet();
    }

    @Nullable
    public AbstractTemplate get(String id) {
        return templates.get(id);
    }

    @Override
    public void reloadConfig(MemoryConfiguration cfg) {
        File folder = plugin.resolve("./templates");
        if (!folder.exists()) {
            Util.mkdirs(folder);
            plugin.saveResource("templates/example.yml", new File(folder, "example.yml"));
        }
        templates.clear();
        Util.reloadFolder(folder, false, (id, file) -> {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            String type = config.getString("type");
            Map<String, String> args = new HashMap<>();
            ConfigurationSection section = config.getConfigurationSection("arguments");
            if (section != null) for (String key : section.getKeys(false)) {
                args.put(key, section.getString(key));
            }
            List<String> messages = new ArrayList<>();
            if (config.contains("messages")) {
                if (config.isString("messages")) {
                    messages.add(config.getString("messages"));
                }
                if (config.isList("messages")) {
                    messages.addAll(config.getStringList("messages"));
                }
            }
            if (messages.isEmpty()) {
                warn("[templates/" + id + "] messages 为空");
                return;
            }
            if ("message".equals(type)) {
                TextArguments arguments = TextArguments.parser(args, messages);
                templates.put(id, new TextTemplate(id, arguments));
                return;
            }
            if ("actionbar".equals(type)) {
                TextArguments arguments = TextArguments.parser(args, messages);
                arguments.isActionMessage = true;
                templates.put(id, new TextTemplate(id, arguments));
                return;
            }
            if ("title".equals(type)) {
                String title = messages.get(0);
                String subTitle = messages.size() == 1 ? "" : messages.get(1);
                TitleArguments arguments = TitleArguments.parser(args, title, subTitle);
                templates.put(id, new TitleTemplate(id, arguments));
                return;
            }
            if ("bossbar".equals(type)) {
                String message = messages.get(0);
                BossBarArguments arguments = BossBarArguments.parser(args, message);
                templates.put(id, new BossBarTemplate(id, arguments));
                return;
            }
            warn("[template/" + id + "] 找不到类型 " + type);
        });
    }

    public static TemplateManager inst() {
        return instanceOf(TemplateManager.class);
    }
}
