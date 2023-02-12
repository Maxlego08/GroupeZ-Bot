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
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.GenericComponentInteractionCreateEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.managers.ChannelManager;
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
			Button button, ButtonClickEvent event);

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

	public void preButtonClick(TicketManager ticketManager, ButtonClickEvent event, User user, Guild guild,
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
	public void selectionClick(TicketManager ticketManager, SelectionMenuEvent event, User user, Guild guild,
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
			User user, GenericComponentInteractionCreateEvent event, Runnable runnable) {

		this.manager = ticketManager;
		this.ticket = ticket;
		this.event = event;
		this.member = guild.getMember(user);
		this.runnable = runnable;
		this.guild = guild;

		this.process(ticket, messageChannel, guild, user, event);
	}

	public Component createCloseButton() {
		return Button.danger(BUTTON_CLOSE, this.ticket.getMessage(Message.TICKET_CLOSE_BUTTON));
	}

	/**
	 * Allows you to close a ticket with a timer.
	 * 
	 * @param ticket
	 * @param event
	 */
	protected void closeTicket(Ticket ticket, ButtonClickEvent event) {
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

						TextChannel channel = event.getTextChannel();
						PermissionOverrideAction permissionOverrideAction = channel.putPermissionOverride(this.member);
						permissionOverrideAction.clear(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();

						ChannelManager channelManager = channel.getManager();
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
	 */
	protected void endQuestions(Guild guild, String ticketName) {
		this.endQuestions(guild, ticketName, false);
	}

	/**
	 * Allows to finish the questions and to give access to the user to write
	 * 
	 * @param guild
	 * @param ticketName
	 * @param move
	 */
	protected void endQuestions(Guild guild, String ticketName, boolean move) {

		this.ticket.setWaiting(false);

		TextChannel channel = this.ticket.getTextChannel(guild);
		PermissionOverrideAction permissionOverrideAction = channel.putPermissionOverride(member);
		permissionOverrideAction.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();

		ChannelManager channelManager = channel.getManager();
		channelManager.setName(ticketName).queue(e -> {
			if (move) {
				Category category = guild.getCategoryById(Config.ticketOrderChannel);
				channelManager.setParent(category).queue();
			}
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
