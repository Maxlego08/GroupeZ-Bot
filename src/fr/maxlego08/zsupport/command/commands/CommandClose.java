package fr.maxlego08.zsupport.command.commands;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;

public class CommandClose extends VCommand {

    public CommandClose(CommandManager commandManager) {
        super(commandManager);
        this.consoleCanUse = false;
        this.onlyInCommandChannel = false;
        this.description = "Close a ticket";
    }

    @Override
    protected CommandType perform(ZSupport main) {

        if (event.getChannel() instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategoryIdLong() == Config.ticketCategoryId) {

            ZSupport.instance.getTicketManager().closeTicket(event, event.getGuild(), event.getMember());
        } else {
            event.reply(":x: You cannot use this command here.").setEphemeral(true).queue();
        }

        return CommandType.SUCCESS;
    }

}
