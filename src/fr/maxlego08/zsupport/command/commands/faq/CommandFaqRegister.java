package fr.maxlego08.zsupport.command.commands.faq;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.faq.FaqManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Objects;

public class CommandFaqRegister extends VCommand {

    private final FaqManager faqManager;

    public CommandFaqRegister(CommandManager commandManager, FaqManager faqManager) {
        super(commandManager);
        this.faqManager = faqManager;
        this.consoleCanUse = false;
        this.permission = Permission.MESSAGE_MANAGE;
        this.description = "Create new FAQ";
        this.addRequireArg(OptionType.STRING, "name", "Faq name");
        this.addRequireArg(OptionType.STRING, "title", "Faq title");
        this.addRequireArg(OptionType.STRING, "content", "Faq description");
    }

    @Override
    protected CommandType perform(ZSupport instance) {

        String name = Objects.requireNonNull(this.event.getOption("name")).getAsString();
        String title = Objects.requireNonNull(this.event.getOption("title")).getAsString();
        String description = Objects.requireNonNull(this.event.getOption("content")).getAsString();

        faqManager.createFaq(event, name, title, description);

        return CommandType.SUCCESS;
    }
}
