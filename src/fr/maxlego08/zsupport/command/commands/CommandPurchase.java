package fr.maxlego08.zsupport.command.commands;

import java.awt.Color;
import java.time.OffsetDateTime;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.internal.interactions.ButtonImpl;

public class CommandPurchase extends VCommand {

	public CommandPurchase(CommandManager commandManager) {
		super(commandManager);
		this.permission = Permission.ADMINISTRATOR;
		this.description = "Display message on how to buy";
	}

	@Override
	protected CommandType perform(ZSupport main) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("How to buy ?");
		builder.setColor(new Color(45, 200, 45));
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter("2023 - " + this.guild.getName(), this.guild.getIconUrl());

		TextChannel channel = this.guild.getTextChannelById(Config.ticketChannel);

		String desc = "";

		desc += ":flag_fr: Pour acheter un plugin vous devez vous rendre sur https://groupez.dev/resources";
		desc += "\n";
		desc += "Vous pouvez payer par **Paypal** les plugins de GroupeZ en utilisant Spigot, mais vous aurez une différence de prix.";
		desc += "\n";
		desc += "Après l'achat vous pouvez créer un ticket dans " + channel.getAsMention()
				+ " pour demander l'accès sur spigot.";
		desc += "\n";
		desc += "\n";
		desc += ":flag_us: To purchase a plugin you must go to https://groupez.dev/resources";
		desc += "\n";
		desc += "You can pay by **Paypal** for GroupeZ plugins using Spigot, but you will have a price difference.";
		desc += "\n";
		desc += "After purchase you can create a ticket in " + channel.getAsMention() + " to request access on spigot.";

		builder.setDescription(desc);

		Emote emote = this.guild.getEmoteById(Config.groupezEmote);
		Button buttonUrl = new ButtonImpl("btn:url", "Click to access the marketplace", ButtonStyle.LINK,
				"https://groupez.dev/resources", false, Emoji.fromEmote(emote));

		this.textChannel.sendMessageEmbeds(builder.build()).setActionRow(buttonUrl).queue(message -> {
			this.event.deferReply(true).setContent("Envoie de la commande effectué avec succès.").queue();
		});

		return CommandType.SUCCESS;
	}

}
