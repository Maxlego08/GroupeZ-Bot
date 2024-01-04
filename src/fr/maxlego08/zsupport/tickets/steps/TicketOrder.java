package fr.maxlego08.zsupport.tickets.steps;

import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.Step;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketStep;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class TicketOrder extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Interaction interaction) {

		EmbedBuilder builder = this.createEmbed();

		builder.setDescription(this.ticket.getMessage(Message.TICKET_ORDER));

		this.event.editMessageEmbeds(builder.build()).setActionRow(this.createCloseButton()).queue();

		this.endQuestions(guild, ticket.getName() + "-custom", true, guild.getMember(user), null);
	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonInteractionEvent event) {
	}

	@Override
	public TicketStep getStep() {
		return TicketStep.ORDER;
	}

}
