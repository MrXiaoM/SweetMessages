package top.mrxiaom.sweet.messages.template;

import top.mrxiaom.sweet.messages.commands.args.IArguments;
import top.mrxiaom.sweet.messages.commands.args.TitleArguments;

public class TitleTemplate extends AbstractTemplate {
    private final TitleArguments arguments;
    public TitleTemplate(String id, TitleArguments arguments) {
        super(id);
        this.arguments = arguments;
    }

    @Override
    public IArguments arguments() {
        return arguments;
    }
}
