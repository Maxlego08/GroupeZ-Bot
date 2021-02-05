package fr.maxlego08.zsupport.command.commands;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.entities.User;

public class CommandVerify extends VCommand {

	public CommandVerify(CommandManager commandManager) {
		super(commandManager);
		this.consoleCanUse = false;
		onlyInCommandChannel = true;
	}

	@Override
	protected CommandType perform(ZSupport main) {

		PlayerSender playerSender = super.getPlayer();

		User user = playerSender.getUser();

		VerifyManager manager = VerifyManager.getInstance();
		manager.submitData(user, this.textChannel, this.player, true);

		return CommandType.SUCCESS;
	}

}
