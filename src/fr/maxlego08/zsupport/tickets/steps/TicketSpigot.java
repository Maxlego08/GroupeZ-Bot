package fr.maxlego08.zsupport.tickets.steps;

import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.Step;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketStep;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class TicketSpigot extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user) {

		EmbedBuilder builder = this.createEmbed();

		builder.setDescription(this.ticket.getMessage(Message.TICKET_SPIGOT));

		this.event.editMessageEmbeds(builder.build()).setActionRow(this.createCloseButton()).queue();
		
		this.endQuestions(guild, ticket.getName() + "-spigot");
	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonClickEvent event) {
	}

	@Override
	public TicketStep getStep() {
		return TicketStep.CHOOSE_SPIGOT;
	}

}
