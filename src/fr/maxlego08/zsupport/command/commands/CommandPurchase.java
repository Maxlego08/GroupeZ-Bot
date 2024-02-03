package fr.maxlego08.zsupport.command.commands;

import java.awt.Color;
import java.time.OffsetDateTime;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

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
		setEmbedFooter(this.guild, builder, new Color(45, 200, 45));

		TextChannel channel = this.guild.getTextChannelById(Config.ticketChannel);

		setDescription(builder,
				":flag_fr: Pour acheter un plugin vous devez vous rendre sur https://groupez.dev/resources",
				"Vous pouvez payer par **Paypal** les plugins de GroupeZ en utilisant Spigot, mais vous aurez une différence de prix.",
				"Après l'achat vous pouvez créer un ticket dans " + channel.getAsMention() + " pour demander l'accès sur spigot.",
				"",
				":flag_us: To purchase a plugin you must go to https://groupez.dev/resources",
				"You can pay by **Paypal** for GroupeZ plugins using Spigot, but you will have a price difference.",
				"After purchase you can create a ticket in " + channel.getAsMention() + " to request access on spigot."
		);

		Emoji emote = this.guild.getEmojiById(Config.groupezEmote);
		Button buttonUrl = new ButtonImpl("btn:url", "Click to access the marketplace", ButtonStyle.LINK,
				"https://groupez.dev/resources", false, emote);

		this.textChannel.sendMessageEmbeds(builder.build()).setActionRow(buttonUrl).queue(message -> {
			this.event.deferReply(true).setContent("Envoi de la commande effectuée avec succès.").queue();
		});

		return CommandType.SUCCESS;
	}

}
