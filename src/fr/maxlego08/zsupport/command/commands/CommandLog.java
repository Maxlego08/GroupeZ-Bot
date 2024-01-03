package fr.maxlego08.zsupport.command.commands;

import java.awt.Color;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.EmbedBuilder;

public class CommandLog extends VCommand {

	public CommandLog(CommandManager commandManager) {
		super(commandManager);
		this.consoleCanUse = false;
		this.onlyInCommandChannel = true;
		this.description = "Send log informations";
	}

	@Override
	protected CommandType perform(ZSupport main) {
		
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Server Info");
		builder.setColor(Color.getHSBColor(45, 45, 45));
		builder.setFooter(Constant.YEAR + " - " + this.guild.getName(), this.guild.getIconUrl());
		builder.setDescription("Paste your logs on https://mclo.gs/");
		event.replyEmbeds(builder.build()).queue();

		return CommandType.SUCCESS;
	}

}
