package fr.maxlego08.zsupport.utils.commands;

import fr.maxlego08.zsupport.lang.BasicMessage;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface Sender extends Constant {

	long getID();

	String getName();

	void sendMessage(String message);

	void sendMessage(BasicMessage message);

	void sendMessage(BasicMessage message, boolean delete, Object... args);

	void sendMessage(SlashCommandInteractionEvent interaction, BasicMessage message);

	void sendMessage(SlashCommandInteractionEvent interaction, BasicMessage message, boolean delete, Object... args);

	boolean hasPermission(Permission permission);

	void sendEmbed(BasicMessage message, boolean delete);

	void sendEmbed(SlashCommandInteractionEvent interaction, BasicMessage message);

}
