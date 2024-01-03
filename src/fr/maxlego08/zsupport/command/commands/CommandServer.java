package fr.maxlego08.zsupport.command.commands;

import java.awt.Color;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.EmbedBuilder;

public class CommandServer extends VCommand {

	public CommandServer(CommandManager commandManager) {
		super(commandManager);
		this.consoleCanUse = false;
		this.onlyInCommandChannel = true;
		this.description = "Display server information";
	}

	@Override
	protected CommandType perform(ZSupport main) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Server Info");
		builder.setColor(Color.getHSBColor(45, 45, 45));
		builder.setFooter(Constant.YEAR + " - " + guild.getName(), guild.getIconUrl());

		builder.addField("Name of discord", guild.getName(), true);
		builder.addField("Founder of discord", guild.getMemberById(522359210844094479l).getAsMention(), true);
		builder.addField("Creation date", guild.getTimeCreated().toString(), false);
		builder.addField("Number of categories", guild.getCategories().size() + "", true);
		builder.addField("Number of text channel", guild.getTextChannels().size() + "", true);
		builder.addField("Number of voice channel", guild.getVoiceChannels().size() + "", true);
		builder.addField("Membres", guild.getMemberCount() + "", false);
		
		builder.addField("SIRET", "887 617 496 00013", true);
		builder.addField("E-mail", "contact@groupez.dev", true);
		builder.addField("Phone", "+33 07.82.60.90.13", false);

		this.event.replyEmbeds(builder.build()).setEphemeral(true).queue();
		

		return CommandType.SUCCESS;
	}
}
