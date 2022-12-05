package fr.maxlego08.zsupport.command.commands.tickets;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.Permission;

public class CommandTicketVocal extends VCommand {

	public CommandTicketVocal(CommandManager commandManager) {
		super(commandManager);
		this.consoleCanUse = false;
		this.permission = Permission.ADMINISTRATOR;
		this.description = "Create a voice room for a ticket";
	}

	@Override
	protected CommandType perform(ZSupport main) {
		main.getTicketManager().createVocal(this.player, this.textChannel, this.guild);
		this.event.deferReply(true).setContent("ToDo - Rework cette commande").queue();
		return CommandType.SUCCESS;
	}

}
