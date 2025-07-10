package top.mrxiaom.sweet.messages.template;

import top.mrxiaom.sweet.messages.commands.args.BossBarArguments;
import top.mrxiaom.sweet.messages.commands.args.IArguments;

public class BossBarTemplate extends AbstractTemplate {
    private final BossBarArguments arguments;
    public BossBarTemplate(String id, BossBarArguments arguments) {
        super(id);
        this.arguments = arguments;
    }

    @Override
    public IArguments arguments() {
        return arguments;
    }
}
