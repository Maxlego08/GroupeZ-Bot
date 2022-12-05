package fr.maxlego08.zsupport.command.commands;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;

public class CommandStop extends VCommand {

	public CommandStop(CommandManager commandManager) {
		super(commandManager);
		this.playerCanUse = false;
	}

	@Override
	protected CommandType perform(ZSupport main) {
		this.sender.sendMessage("Désactivation du bot.");
		main.getCommandListener().onDisable();
		return CommandType.SUCCESS;
	}

}
