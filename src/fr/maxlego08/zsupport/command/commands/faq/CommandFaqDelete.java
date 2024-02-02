package fr.maxlego08.zsupport.command.commands.faq;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.faq.FaqManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Objects;

public class CommandFaqDelete extends VCommand {

    private final FaqManager faqManager;

    public CommandFaqDelete(CommandManager commandManager, FaqManager faqManager) {
        super(commandManager);
        this.faqManager = faqManager;
        this.consoleCanUse = false;
        this.permission = Permission.ADMINISTRATOR;
        this.description = "Delete a FAQ";
        this.addRequireArg(OptionType.STRING, "name", "Faq name");
    }

    @Override
    protected CommandType perform(ZSupport instance) {

        String name = Objects.requireNonNull(this.event.getOption("name")).getAsString();

        faqManager.deleteFaq(event, name);

        return CommandType.SUCCESS;
    }
}
