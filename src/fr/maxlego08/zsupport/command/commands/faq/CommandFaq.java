package fr.maxlego08.zsupport.command.commands.faq;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.faq.FaqManager;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Objects;

public class CommandFaq extends VCommand {

    public CommandFaq(CommandManager commandManager, FaqManager faqManager) {
        super(commandManager);
        this.consoleCanUse = false;
        this.description = "Send a message present in the FAQ";
        this.addRequireArg(OptionType.STRING, "faq", "Name of the FAQ to display", faqManager.getChoices());
    }

    @Override
    protected CommandType perform(ZSupport instance) {

        String name = Objects.requireNonNull(this.event.getOption("faq")).getAsString();
        instance.getFaqManager().sendFaq(this.event, name);

        return CommandType.SUCCESS;
    }
}
