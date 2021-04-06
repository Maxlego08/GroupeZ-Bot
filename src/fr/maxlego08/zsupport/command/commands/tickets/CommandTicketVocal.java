package fr.maxlego08.zsupport.command.commands.tickets;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;

public class CommandTicketVocal extends VCommand {

	public CommandTicketVocal(CommandManager commandManager) {
		super(commandManager);
		this.consoleCanUse = false;
	}

	@Override
	protected CommandType perform(ZSupport main) {
		main.getTicketManager().createVocal(player, textChannel, guild);
		return CommandType.SUCCESS;
	}

}
