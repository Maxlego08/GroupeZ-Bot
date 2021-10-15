package fr.maxlego08.zsupport.command.commands;

import java.awt.Color;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;

public class CommandRoles extends VCommand {

	public CommandRoles(CommandManager commandManager) {
		super(commandManager);
		consoleCanUse = false;
		onlyInCommandChannel = true;
	}

	@Override
	protected CommandType perform(ZSupport main) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Roles");
		builder.setColor(Color.getHSBColor(5, 255, 5));
		builder.setFooter("2020 - " + guild.getName(), guild.getIconUrl());
		Config.plugins.forEach(plugin -> {
			Role role = guild.getRoleById(plugin.getRole());
			builder.addField(plugin.getName(), String.valueOf(getMember(role)), true);
		});

		textChannel.sendTyping().queue();
		textChannel.sendMessageEmbeds(builder.build()).complete();
		builder.clear();

		return CommandType.SUCCESS;
	}

	private long getMember(Role role) {
		return guild.getMembersWithRoles(role).size();
	}

}
