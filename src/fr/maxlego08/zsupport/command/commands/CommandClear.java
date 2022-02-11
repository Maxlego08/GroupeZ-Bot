package fr.maxlego08.zsupport.command.commands;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageHistory;

public class CommandClear extends VCommand {

	public CommandClear(CommandManager commandManager) {
		super(commandManager);
		this.addRequireArg("nombre/all");
		this.permission = Permission.MESSAGE_MANAGE;
		this.deleteMessage = false;
	}

	@Override
	protected CommandType perform(ZSupport main) {

		String arg = argAsString(0);

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
				int messages = argAsInteger(0);
				history.retrievePast(messages > 100 ? 100 : messages).queue(msgs -> {
					msgs.forEach(msg -> msg.delete().queue());
				});
			} catch (NumberFormatException e) {
				return CommandType.SYNTAX_ERROR;
			}
		}

		return CommandType.SUCCESS;
	}

}
