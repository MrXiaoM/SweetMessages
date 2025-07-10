package top.mrxiaom.sweet.messages.template;

import top.mrxiaom.sweet.messages.commands.args.IArguments;
import top.mrxiaom.sweet.messages.commands.args.TextArguments;

public class TextTemplate extends AbstractTemplate {
    private final TextArguments arguments;
    public TextTemplate(String id, TextArguments arguments) {
        super(id);
        this.arguments = arguments;
    }

    @Override
    public IArguments arguments() {
        return arguments;
    }
}
