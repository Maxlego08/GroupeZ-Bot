package fr.maxlego08.zsupport.tickets;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.ZUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public abstract class Step extends ZUtils implements Constant, Cloneable {

	protected TicketManager manager;
	protected Ticket ticket;
	protected GenericComponentInteractionCreateEvent event;
	protected MessageReceivedEvent eventMessage;
	protected Member member;
	protected Guild guild;
	protected Runnable runnable;

	public abstract void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user,
			Interaction interaction);

	public abstract void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user,
			Button button, ButtonInteractionEvent event);

	public abstract TicketStep getStep();

	@Override
	protected Step clone() {
		try {
			return (Step) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected EmbedBuilder createEmbed() {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("GroupeZ - Support");
		builder.setColor(new Color(45, 150, 45));
		builder.setFooter("2022 - " + guild.getName(), guild.getIconUrl());
		builder.setTimestamp(OffsetDateTime.now());
		return builder;
	}

	protected StringBuilder createDescription() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Bienvenue sur le support de **GroupeZ**");
		stringBuilder.append("\n");
		stringBuilder.append("Pour une meilleur prise en charge de votre demande, merci de répondre aux questions:");
		stringBuilder.append("\n");
		stringBuilder.append("\n");
		return stringBuilder;
	}

	public void preButtonClick(TicketManager ticketManager, ButtonInteractionEvent event, User user, Guild guild,
			MessageChannel channel) {

		this.guild = guild;
		this.manager = ticketManager;
		this.event = event;
		this.member = guild.getMember(user);
		Button button = event.getButton();

		if (button.getId().equals(BUTTON_CLOSE)) {
			this.closeTicket(ticket, event);
		}

		this.buttonClick(ticket, channel, guild, user, button, event);
	}

	/**
	 * Call when selection menu event is trigger
	 * 
	 * @param ticketManager
	 * @param event
	 * @param user
	 * @param guild
	 * @param messageChannel
	 */
	public void selectionClick(TicketManager ticketManager, StringSelectInteractionEvent event, User user, Guild guild,
			MessageChannel messageChannel) {
	}

	/**
	 * Allows you to instantiate the parameters
	 * 
	 * @param ticketManager
	 * @param ticket
	 * @param messageChannel
	 * @param guild
	 * @param user
	 * @param event
	 * @param runnable
	 */
	public void preProcess(TicketManager ticketManager, Ticket ticket, MessageChannel messageChannel, Guild guild,
			User user, StringSelectInteractionEvent event, Runnable runnable) {

		this.manager = ticketManager;
		this.ticket = ticket;
		this.event = event;
		this.member = guild.getMember(user);
		this.runnable = runnable;
		this.guild = guild;

		this.process(ticket, messageChannel, guild, user, event);
	}

	public ItemComponent createCloseButton() {
		return Button.danger(BUTTON_CLOSE, this.ticket.getMessage(Message.TICKET_CLOSE_BUTTON));
	}

	/**
	 * Allows you to close a ticket with a timer.
	 * 
	 * @param ticket
	 * @param event
	 */
	protected void closeTicket(Ticket ticket, ButtonInteractionEvent event) {
		EmbedBuilder builder = this.createEmbed();
		builder.setDescription(ticket.getMessage(Message.TICKET_CLOSE, 10, "s"));

		ticket.setClose(true);

		event.replyEmbeds(builder.build()).queue(e -> {

			builder.setDescription(ticket.getMessage(Message.TICKET_CLOSE, 5, "s"));
			e.editOriginalEmbeds(builder.build()).queueAfter(5, TimeUnit.SECONDS, e2 -> {

				builder.setDescription(ticket.getMessage(Message.TICKET_CLOSE, 2, "s"));
				e.editOriginalEmbeds(builder.build()).queueAfter(3, TimeUnit.SECONDS, e3 -> {

					builder.setDescription(ticket.getMessage(Message.TICKET_CLOSE, 1, ""));
					e.editOriginalEmbeds(builder.build()).queueAfter(1, TimeUnit.SECONDS, e4 -> {

						TextChannel channel = (TextChannel) event.getChannel();
						PermissionOverrideAction permissionOverrideAction = channel.upsertPermissionOverride(this.member);
						permissionOverrideAction.clear(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL).queue();

						TextChannelManager channelManager = channel.getManager();
						channelManager.setName(ticket.getName() + "-close").queue();

						e4.delete().queue();

					});
				});
			});
		});
	}

	/**
	 * Allows to finish the questions and to give access to the user to write
	 * 
	 * @param guild
	 * @param ticketName
	 * @param member
	 */
	protected void endQuestions(Guild guild, String ticketName, Member member) {
		this.endQuestions(guild, ticketName, false, member, null);
	}

	/**
	 * Allows to finish the questions and to give access to the user to write
	 * 
	 * @param guild
	 * @param ticketName
	 * @param move
	 */
	protected void endQuestions(Guild guild, String ticketName, boolean move, Member member, Runnable after) {

		this.ticket.setWaiting(false);

		TextChannel channel = this.ticket.getTextChannel(guild);
		PermissionOverrideAction permissionOverrideAction = channel.upsertPermissionOverride(member);
		permissionOverrideAction.setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL).queue(z -> {
			
			if (after != null) after.run();
			
			TextChannelManager channelManager = channel.getManager();
			channelManager.setName(ticketName).queue(e -> {
				if (move) {
					Category category = guild.getCategoryById(Config.ticketOrderChannel);
					channelManager.setParent(category).queue();
				}
			});
		});
	}

	public void preMessage(TicketManager ticketManager, MessageReceivedEvent event, User user, Guild guild,
			MessageChannel channel) {

		this.guild = guild;
		this.manager = ticketManager;
		this.eventMessage = event;
		this.member = guild.getMember(user);

		this.onMessage(ticket, channel, guild, user, event);

	}

	protected void onMessage(Ticket ticket, MessageChannel channel, Guild guild, User user,
			MessageReceivedEvent event) {
	}

}
