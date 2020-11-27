package fr.maxlego08.zsupport.command.commands;

import java.util.List;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;

public class CommandClear extends VCommand {

	public CommandClear(CommandManager commandManager) {
		super(commandManager);
		this.addRequireArg("nombre/all");
		this.permission = Permission.MESSAGE_MANAGE;
	}

	@Override
	protected CommandType perform(ZSupport main) {

		String arg = argAsString(0);

		MessageHistory history = new MessageHistory(textChannel);
		if (arg.equalsIgnoreCase("all")) {
			try {
				while (true) {
					List<Message> msgs = history.retrievePast(1).complete();
					msgs.get(0).delete().complete();
				}
			} catch (Exception e) {
			}
		} else {

			try {
				int messages = argAsInteger(0);
				List<Message> msgs = history.retrievePast(messages > 100 ? 100 : messages).complete();
				msgs.forEach(msg -> msg.delete().complete());
			} catch (NumberFormatException e) {
				return CommandType.SYNTAX_ERROR;
			}
		}

		return CommandType.SUCCESS;
	}

}
