package fr.maxlego08.zsupport.command.commands;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

public class CommandCustomerVerify extends VCommand {

	public CommandCustomerVerify(CommandManager commandManager) {
		super(commandManager);
		this.consoleCanUse = false;
		this.onlyInCommandChannel = false;
		this.description = "Allows you to check your purchases on groupez.dev";
		this.permission = Permission.MESSAGE_MANAGE;
		this.addRequireArg("utilisateur");
		this.addRequireArg("plugin");
	}

	@Override
	protected CommandType perform(ZSupport main) {

		Member target = this.event.getOption("utilisateur").getAsMember();
		int pluginId = (int) this.event.getOption("plugin").getAsLong();

		User user = this.event.getUser();

		VerifyManager manager = VerifyManager.getInstance();
		manager.updateUserAsync(user, this.player, this.event, target, pluginId);

		return CommandType.SUCCESS;
	}

}
