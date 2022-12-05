package fr.maxlego08.zsupport.command.commands.tickets;

import java.awt.Color;
import java.time.OffsetDateTime;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.internal.interactions.ButtonImpl;

public class CommandTicketSet extends VCommand {

	public CommandTicketSet(CommandManager commandManager) {
		super(commandManager);
		this.consoleCanUse = false;
		this.permission = Permission.ADMINISTRATOR;
		this.description = "Afficher le message des tickets";
	}

	@Override
	protected CommandType perform(ZSupport main) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("Create a ticket");
		builder.setColor(new Color(45, 45, 45));
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter("2022 - " + this.guild.getName(), this.guild.getIconUrl());

		String desc = "";

		desc += ":flag_fr: Clique pour créer un ticket";
		desc += "\n:warning:Veuillez vérifier que votre compte discord est bien relié sur le site.";
		desc += "\n";
		desc += "\n";
		desc += ":flag_us: Click for create a ticket";
		desc += "\n:warning: Please check that your discord account is linked to the site.";
		desc += "\n";
		desc += "\n";
		desc += "\n";
		desc += ":white_check_mark:  Link your discord account to the site https://groupez.dev/dashboard/account";

		builder.setDescription(desc);

		builder.setImage("https://img.groupez.dev/groupez/link-discord.gif");
		
		Button buttonFr = new ButtonImpl(BUTTON_FR, "Français", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("U+1F1EB U+1F1F7"));
		Button buttonEn = new ButtonImpl(BUTTON_EN, "English", ButtonStyle.SECONDARY, false, Emoji.fromUnicode("U+1F1FA U+1F1F8"));
		
		this.textChannel.sendMessageEmbeds(builder.build()).setActionRow(buttonFr, buttonEn).queue(message -> {
			this.event.deferReply(true).setContent("Envoie de la commande effectué avec succès.").queue();
		});

		return CommandType.SUCCESS;
	}

}
