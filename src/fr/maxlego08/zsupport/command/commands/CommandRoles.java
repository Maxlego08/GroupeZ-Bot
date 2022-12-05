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
		this.consoleCanUse = false;
		this.onlyInCommandChannel = true;
		this.description = "Display the number of users by roles";
	}

	@Override
	protected CommandType perform(ZSupport main) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Roles");
		builder.setColor(Color.getHSBColor(5, 255, 5));
		builder.setFooter("2022 - " + this.guild.getName(), this.guild.getIconUrl());
		Config.plugins.forEach(plugin -> {
			Role role = this.guild.getRoleById(plugin.getRole());
			if (role != null) {
				builder.addField(plugin.getName(), String.valueOf(getMember(role)), true);
			}
		});

		this.event.deferReply(true).addEmbeds(builder.build()).queue();

		return CommandType.SUCCESS;
	}

	private long getMember(Role role) {
		return guild.getMembersWithRoles(role).size();
	}

}
