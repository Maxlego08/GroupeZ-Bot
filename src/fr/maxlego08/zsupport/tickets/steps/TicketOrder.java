package fr.maxlego08.zsupport.tickets.steps;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.tickets.Step;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketStep;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class TicketOrder extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user) {

		EmbedBuilder builder = this.createEmbed();

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder
				.append("Pour pouvoir passer une commande vous devez donner toutes les informations sur le projet.");

		stringBuilder.append("\n");
		stringBuilder.append("Comme par exemple:");
		stringBuilder.append("\n");
		stringBuilder.append("- Le nom de votre serveur");
		stringBuilder.append("\n");
		stringBuilder.append("- La version de votre serveur");
		stringBuilder.append("\n");
		stringBuilder.append("- Le délais de livraison");
		stringBuilder.append("\n");
		stringBuilder.append("- La description complète de votre plugin");
		stringBuilder.append("\n");
		stringBuilder.append("\n");
		stringBuilder.append("Informations:");
		stringBuilder.append("\n");
		stringBuilder.append("Tarif: **20€/h**");
		stringBuilder.append("\n");
		stringBuilder.append(
				"Achat du code source: **50%** du prix (par exemple, pour un plugin à 100€ les sources seront vendu à 50€)");
		stringBuilder.append("\n");
		stringBuilder.append("Siret: 88761749600013");

		builder.setDescription(stringBuilder);

		this.event.editMessageEmbeds(builder.build()).setActionRows().queue();

		this.ticket.setWaiting(false);

		TextChannel channel = ticket.getTextChannel();
		PermissionOverrideAction permissionOverrideAction = channel.putPermissionOverride(member);
		permissionOverrideAction.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();

		ChannelManager channelManager = channel.getManager();
		channelManager.setName(channel.getName() + "-custom").queue(e -> {
			Category category = guild.getCategoryById(Config.ticketOrderChannel);
			channelManager.setParent(category).queue();
		});
	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public TicketStep getStep() {
		return TicketStep.ORDER;
	}

}
