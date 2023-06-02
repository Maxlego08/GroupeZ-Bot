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
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.internal.interactions.ButtonImpl;

public class TicketTypeStep extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Interaction interaction) {

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

				Emote emote = guild.getEmoteById(Config.spigotEmote);
				EmbedBuilder builder = this.createEmbed();

				builder.setDescription(this.ticket.getMessage(Message.TICKET_CHOOSE, emote.getAsMention()));

				MessageAction action = channel.sendMessageEmbeds(builder.build());

				Button buttonOrder = new ButtonImpl(BUTTON_CHOOSE_ORDER,
						this.ticket.getMessage(Message.TICKET_CHOOSE_ORDER), ButtonStyle.SECONDARY, false,
						Emoji.fromUnicode("U+1F4B5"));

				Button buttonHelp = new ButtonImpl(BUTTON_CHOOSE_SUPPORT,
						this.ticket.getMessage(Message.TICKET_CHOOSE_PLUGIN), ButtonStyle.PRIMARY, false,
						Emoji.fromUnicode("U+2753"));

				Button buttonSpigot = new ButtonImpl(BUTTON_CHOOSE_SPIGOT,
						this.ticket.getMessage(Message.TICKET_CHOOSE_SPIGOT), ButtonStyle.SECONDARY, false,
						Emoji.fromEmote(emote));

				Button buttonBeforePurchase = new ButtonImpl(BUTTON_CHOOSE_BEFORE_PURCHASE,
						this.ticket.getMessage(Message.TICKET_CHOOSE_BEFORE_PURCHASE), ButtonStyle.SECONDARY, false,
						Emoji.fromUnicode("U+1F44B"));

				action.setActionRow(buttonOrder, buttonHelp, buttonSpigot, buttonBeforePurchase);
				action.queue(e2 -> {
					permissionOverrideAction1.setAllow(Permission.MESSAGE_READ).queue();
				});

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
		Step step = (buttonId.equals(BUTTON_CHOOSE_ORDER) ? TicketStep.ORDER
				: buttonId.equals(BUTTON_CHOOSE_SPIGOT) ? TicketStep.CHOOSE_SPIGOT
						: buttonId.equals(BUTTON_CHOOSE_BEFORE_PURCHASE) ? TicketStep.QUESTION
								: TicketStep.CHOOSE_PLUGIN).getStep();
		ticket.setStep(step);

		step.preProcess(this.manager, ticket, messageChannel, guild, user, event, null);

	}

}
