package fr.maxlego08.zsupport.command.commands;

import java.awt.Color;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;

public class CommandServer extends VCommand {

	public CommandServer(CommandManager commandManager) {
		super(commandManager);
		consoleCanUse = false;
	}

	@Override
	protected CommandType perform(ZSupport main) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Server Info");
		builder.setColor(Color.getHSBColor(45, 45, 45));
		builder.setFooter("2020 - " + guild.getName(), guild.getIconUrl());

		builder.addField("Nom du discord", guild.getName(), true);
		builder.addField("Fondateur du discord", "Maxlego08#2020", true);
		builder.addField("Date de création", guild.getTimeCreated().toString(), false);
		builder.addField("Nombre de catégories", guild.getCategories().size() + "", true);
		builder.addField("Nombre de salons textuels", guild.getTextChannels().size() + "", true);
		builder.addField("Nombre de salons vocaux", guild.getVoiceChannels().size() + "", true);
		builder.addField("Membres", guild.getMemberCount() + "", true);

		textChannel.sendTyping().queue();
		textChannel.sendMessage(builder.build()).complete();
		builder.clear();

		return CommandType.SUCCESS;
	}
}
