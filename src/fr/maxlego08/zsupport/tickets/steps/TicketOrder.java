package fr.maxlego08.zsupport.tickets.steps;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.Message;
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

		builder.setDescription(this.ticket.getMessage(Message.TICKET_ORDER));

		this.event.editMessageEmbeds(builder.build()).setActionRow(this.createCloseButton()).queue();

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

		if (button.getId().equals(BUTTON_CLOSE)) {
			this.closeTicket(ticket, event);
		}

	}

	@Override
	public TicketStep getStep() {
		return TicketStep.ORDER;
	}

}
