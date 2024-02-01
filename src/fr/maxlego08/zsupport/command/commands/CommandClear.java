package fr.maxlego08.zsupport.command.commands;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandArgument;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class CommandClear extends VCommand {

    public CommandClear(CommandManager commandManager) {
        super(commandManager);
        this.consoleCanUse = false;
        this.addRequireArg(new CommandArgument(OptionType.STRING, "amount", "Number of messages to delete."));
        this.permission = Permission.MESSAGE_MANAGE;
        this.description = "Delete messages";
    }

    @Override
    protected CommandType perform(ZSupport main) {

        String arg = this.event.getOption("amount").getAsString();

        MessageHistory history = new MessageHistory(textChannel);
        if (arg.equalsIgnoreCase("all")) {
            try {
                while (true) {
                    history.retrievePast(1).queue(e -> {
                        e.get(0).delete().queue();
                    });
                }
            } catch (Exception e) {
            }
        } else {

            try {
                int messages = Integer.parseInt(arg);
                history.retrievePast(messages > 100 ? 100 : messages).queue(msgs -> {
                    msgs.forEach(msg -> msg.delete().queue());
                });
            } catch (NumberFormatException exception) {
                exception.printStackTrace();
                return CommandType.SYNTAX_ERROR;
            }
        }

        event.deferReply(true).setContent("Message being deleted.").queue();

        return CommandType.SUCCESS;
    }

}
