package fr.maxlego08.zsupport.api;

import fr.maxlego08.zsupport.utils.Message;
import fr.maxlego08.zsupport.utils.commands.ConsoleSender;
import net.dv8tion.jda.api.Permission;

public class DiscordConsoleSender implements ConsoleSender {

	@Override
	public long getID() {
		return 0;
	}
	
	@Override
	public String getName() {
		return "CONSOLE";
	}

	@Override
	public void sendMessage(String message) {
		System.out.println(PREFIX_CONSOLE + message);
	}

	@Override
	public void sendMessage(Message message) {
		this.sendMessage(message.getMessage());
	}

	@Override
	public void sendMessage(Message message, boolean delete, Object... args) {
		this.sendMessage(String.format(message.getMessage(), args));
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return true;
	}

	@Override
	public void sendEmbed(Message message, boolean delete) {
		this.sendMessage(message, delete);
	}

}
