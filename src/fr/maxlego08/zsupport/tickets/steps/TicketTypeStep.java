package fr.maxlego08.zsupport.tickets.steps;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.Step;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketStep;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.internal.interactions.ButtonImpl;

public class TicketTypeStep extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user) {

		Category category = guild.getCategoryById(Config.ticketCategoryId);

		// Create new channel
		category.createTextChannel(ticket.getName()).queue(channel -> {

			ticket.setTextChannel(channel);

			// Add read permission
			PermissionOverrideAction permissionOverrideAction1 = channel.createPermissionOverride(this.member);
			permissionOverrideAction1.setAllow(Permission.MESSAGE_READ).queue(e -> {

				this.runnable.run();
				String welcomeMessage = ticket.getMessage(Message.TICKET_WELCOME, user.getAsMention());
				channel.sendMessage(welcomeMessage).queue();

				EmbedBuilder builder = this.createEmbed();

				StringBuilder stringBuilder = this.createDescription();
				stringBuilder.append("Veuillez choisir le type de votre ticket:");
				stringBuilder.append("\n");
				stringBuilder.append("\n");
				stringBuilder.append(":dollar: Pour passer une commande");
				stringBuilder.append("\n");
				stringBuilder.append(":question: Pour demander de l'aide sur un plugin");
				stringBuilder.append("\n");
				stringBuilder.append("\n");

				builder.setDescription(stringBuilder.toString());

				MessageAction action = channel.sendMessageEmbeds(builder.build());

				Button buttonOrder = new ButtonImpl(BUTTON_CHOOSE_ORDER, "Passer une commande", ButtonStyle.SECONDARY,
						false, Emoji.fromUnicode("U+1F4B5"));
				Button buttonHelp = new ButtonImpl(BUTTON_CHOOSE_SUPPORT, "Support plugins", ButtonStyle.SECONDARY,
						false, Emoji.fromUnicode("U+2753"));

				action.setActionRow(buttonHelp, buttonOrder);
				action.queue();

			});

		});

	}

	@Override
	public TicketStep getStep() {
		return TicketStep.CHOOSE_TICKET_TYPE;
	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonClickEvent event) {

		String buttonId = button.getId();
		Step step = (buttonId.equals(BUTTON_CHOOSE_ORDER) ? TicketStep.ORDER : TicketStep.CHOOSE_PLUGIN).getStep();
		ticket.setStep(step);
		
		step.preProcess(this.manager, ticket, messageChannel, guild, user, event, null);

	}

}
