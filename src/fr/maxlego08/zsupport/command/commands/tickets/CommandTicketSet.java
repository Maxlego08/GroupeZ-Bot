package fr.maxlego08.zsupport.command.commands.tickets;

import java.awt.Color;
import java.time.OffsetDateTime;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class CommandTicketSet extends VCommand {

	public CommandTicketSet(CommandManager commandManager) {
		super(commandManager);
		this.consoleCanUse = false;
		this.permission = Permission.ADMINISTRATOR;
	}

	@Override
	protected CommandType perform(ZSupport main) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Create a ticket");
		builder.setColor(Color.getHSBColor(45, 45, 45));
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter("2020 - " + guild.getName(), guild.getIconUrl());

		String desc = "";

		desc += ":flag_fr: Clique pour créer un ticket";
		desc += "\n:warning:Veuillez vérifier que votre compte discord est bien relié sur le site.";
		desc += "\n";
		desc += "\n";
		desc += ":flag_us: Click for create a ticket";
		desc += "\n:warning: Please check that your discord account is linked to the site.";

		builder.setDescription(desc);

		builder.setImage("https://img.groupez.dev/groupez/link-discord.gif");
		
		textChannel.sendTyping().queue();
		Message message = textChannel.sendMessageEmbeds(builder.build()).complete();
		message.addReaction("🇺🇸").queue();
		message.addReaction("🇫🇷").queue();
		builder.clear();

		return CommandType.SUCCESS;
	}

}
