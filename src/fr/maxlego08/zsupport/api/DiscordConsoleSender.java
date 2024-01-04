package fr.maxlego08.zsupport.api;

import fr.maxlego08.zsupport.lang.BasicMessage;
import fr.maxlego08.zsupport.utils.commands.ConsoleSender;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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
	public void sendMessage(BasicMessage message) {
		this.sendMessage(message.getMessage());
	}

	@Override
	public void sendMessage(BasicMessage message, boolean delete, Object... args) {
		this.sendMessage(String.format(message.getMessage(), args));
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return true;
	}

	@Override
	public void sendEmbed(BasicMessage message, boolean delete) {
		this.sendMessage(message, delete);
	}

	@Override
	public void sendMessage(SlashCommandInteractionEvent interaction, BasicMessage message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(SlashCommandInteractionEvent interaction, BasicMessage message, boolean delete, Object... args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEmbed(SlashCommandInteractionEvent interaction, BasicMessage message) {
		// TODO Auto-generated method stub
		
	}

}
