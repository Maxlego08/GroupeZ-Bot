package fr.maxlego08.zsupport.tickets;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Saveable;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class TicketManager extends ZUtils implements Constant, Saveable {

	private static List<Ticket> tickets = new ArrayList<Ticket>();
	private transient Map<Long, Long> cooldownMessages = new HashMap<Long, Long>();

	private static long messageTicketId;
	private static long lastSendTicketMessage;

	public TicketManager(ZSupport support) {
		super();
	}

	private void ticketIsValid() {
		Iterator<Ticket> iterator = tickets.iterator();
		while (iterator.hasNext()) {
			Ticket ticket = iterator.next();
			if (!ticket.isValid()) {
				System.out.println("Supression du ticket: " + ticket.getName());
				iterator.remove();
			}
		}
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	public Optional<Ticket> getByUser(User user) {
		ticketIsValid();
		return tickets.stream().filter(ticket -> ticket.getUserId() == user.getIdLong()).findAny();
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	public Optional<Ticket> getByChannel(TextChannel channel) {
		ticketIsValid();
		return tickets.stream().filter(ticket -> ticket.getChannelId() == channel.getIdLong()).findAny();
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Optional<Plugin> getById(long id) {
		return Config.plugins.stream().filter(l -> l.getEmoteId() == id).findAny();
	}

	/**
	 * Allows you to create a ticket When creating the ticket, the bot will
	 * check if the user has linked his account on the site If yes, then the
	 * user can create the ticket, if no, then he must link his discord account
	 * on the site.
	 * 
	 * @param user
	 * @param guild
	 * @param type
	 * @param messageChannel
	 * @param event
	 */
	public void createTicket(User user, Guild guild, LangType type, MessageChannel messageChannel,
			ButtonClickEvent event) {

		Optional<Ticket> optional = getByUser(user);
		if (optional.isPresent()) {

			Ticket ticket = optional.get();
			ticket.message(fr.maxlego08.zsupport.lang.Message.TICKET_ALREADY_CREATE);
						
			String content = this.getMessage(type, Message.TICKET_ALREADY_CREATE_REPLY,
					ticket.getTextChannel(guild).getAsMention());
			event.deferReply(true).setContent(content).queue();

		} else {

			VerifyManager manager = VerifyManager.getInstance();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("GroupeZ - Support");
			builder.setColor(Color.getHSBColor(45, 45, 45));
			builder.setFooter("2022 - " + guild.getName(), guild.getIconUrl());
			builder.setTimestamp(OffsetDateTime.now());

			builder.setDescription(this.getMessage(type, Message.TICKET_CREATE_WAIT));

			event.deferReply(true).addEmbeds(builder.build()).queue(message -> {			

				manager.userIsLink(user, () -> {

					Ticket createdTicket = new Ticket(type, guild.getIdLong(), user.getIdLong(), this.getTicketName());

					Step step = TicketStep.CHOOSE_TICKET_TYPE.getStep();
					createdTicket.setStep(step);

					step.preProcess(this, createdTicket, messageChannel, guild, user, event, () -> {

						TextChannel textChannel = createdTicket.getTextChannel();
						String stringMessage = this.getMessage(type, Message.TICKET_CREATE_SUCCESS)
								+ textChannel.getAsMention();

						builder.setDescription(stringMessage);

						builder.setColor(new Color(45, 250, 45));
						message.editOriginalEmbeds(builder.build()).queue();

					});

					tickets.add(createdTicket);
					ZSupport.instance.save();

				}, () -> {

					builder.setDescription(this.getMessage(type, Message.TICKET_CREATE_ERROR));
					builder.setColor(Color.RED);

					message.editOriginalEmbeds(builder.build()).queue();

				});

			});

		}

	}

	/**
	 * Return ticket name
	 * 
	 * @return ticket name
	 */
	public String getTicketName() {
		int ticket = Config.ticketNumber;
		String tickets = String.format("ticket-#%04d", ticket);
		Config.ticketNumber++;
		return tickets;
	}

	@Override
	public void save(Persist persist) {
		persist.save(this);
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, TicketManager.class);
		tickets.forEach(ticket -> {
			ticket.step(this);
		});
	}

	/**
	 * 
	 * @param guild
	 * @param user
	 * @param id
	 */
	public void choosePlugin(Guild guild, User user, long id) {

		/*
		 * Ticket ticket = getByUser(user); // Le joueur a déjà un ticket if
		 * (ticket != null && ticket.isWaiting()) {
		 * 
		 * Plugin plugin = getById(id); if (plugin != null) {
		 * 
		 * ticket.choose(plugin, guild, user);
		 * 
		 * }
		 * 
		 * }
		 */
	}

	public void createVocal(PlayerSender player, TextChannel textChannel, Guild guild) {
		if (player.hasPermission(Permission.MANAGE_CHANNEL)) {

			Optional<Ticket> optional = getByChannel(textChannel);
			if (!optional.isPresent()) {
				System.out.println("Impossible de trouver le ticket.");
				return;
			}

			Ticket ticket = optional.get();
			Member member = guild.getMemberById(ticket.getUserId());

			VoiceChannel channel = guild.getCategoryById(Config.ticketCategoryId)
					.createVoiceChannel("vocal-" + ticket.getName()).complete();
			PermissionOverrideAction permissionOverrideAction = channel.createPermissionOverride(member);
			permissionOverrideAction.setAllow(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.MESSAGE_READ)
					.complete();

		} else
			player.sendMessage("You don't have permission");
	}

	/**
	 * Call when user leave discord with a ticket
	 * 
	 * @param guild
	 * @param user
	 */
	public void userLeave(Guild guild, User user) {
		Optional<Ticket> optional = this.getByUser(user);
		if (optional.isPresent()) {

			Ticket ticket = optional.get();

			TextChannel channel = guild.getTextChannelById(ticket.getChannelId());
			ChannelManager channelManager = channel.getManager();
			channelManager.setName("user-leave").queue();

		}
	}

	/**
	 * 
	 * @param event
	 * @param user
	 * @param guild
	 * @param channel
	 */
	public void stepButton(ButtonClickEvent event, User user, Guild guild, MessageChannel channel) {

		Optional<Ticket> optional = this.getByUser(user);
		if (optional.isPresent()) {

			Ticket ticket = optional.get();
			Step step = ticket.getStep();

			step.preButtonClick(this, event, user, guild, channel);

		}

	}

	/**
	 * Allows you to manage the Selection Menu
	 * 
	 * @param event
	 * @param user
	 * @param guild
	 * @param channel
	 */
	public void stepSelectionMenu(SelectionMenuEvent event, User user, Guild guild, MessageChannel channel) {

		Optional<Ticket> optional = this.getByUser(user);
		if (optional.isPresent()) {

			Ticket ticket = optional.get();
			Step step = ticket.getStep();

			step.selectionClick(this, event, user, guild, channel);

		}
	}

	/**
	 * Allows to send information to say that there is no support at night
	 * 
	 * @param event
	 * @param textChannel
	 * @param author
	 */
	public void sendInformations(MessageReceivedEvent event, TextChannel textChannel, User user) {

		Optional<Ticket> optional = this.getByUser(user);
		if (optional.isPresent() && !event.getMember().hasPermission(Permission.MANAGE_CHANNEL)) {

			Ticket ticket = optional.get();

			Calendar calendar = Calendar.getInstance();
			int hour = calendar.get(Calendar.HOUR_OF_DAY);

			if (hour < 9 || hour >= 20) {

				if (this.cooldownMessages.getOrDefault(user.getIdLong(), 0l) > System.currentTimeMillis()) {
					return;
				}

				this.cooldownMessages.put(user.getIdLong(), System.currentTimeMillis() + (1000 * 60 * 10));

				String response = ticket.getMessage(Message.TICKET_HOUR, hour, calendar.get(Calendar.MINUTE));
				net.dv8tion.jda.api.entities.Message message = event.getMessage();
				message.reply(response).queue();

			}
		}

	}

	public void sendTicketUse(MessageReceivedEvent event, TextChannel textChannel) {

		if (messageTicketId == 0) {

			sendTicketInformations(textChannel);

		} else {

			textChannel.getHistoryAround(messageTicketId, 3).queue(history -> {
				net.dv8tion.jda.api.entities.Message message = history.getMessageById(messageTicketId);

				if (message != null) {

					if (System.currentTimeMillis() - lastSendTicketMessage > (1000 * (60 * 10))) {

						message.delete().queue(s -> {
							sendTicketInformations(textChannel);
						});

					}

				} else {
					sendTicketInformations(textChannel);
				}

			});

		}

	}

	public void sendTicketInformations(TextChannel channel) {

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("How to get help ?");
		builder.setColor(new Color(45, 200, 45));
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter("2022 - " + channel.getGuild().getName(), channel.getGuild().getIconUrl());

		TextChannel ticketChannel = channel.getGuild().getTextChannelById(Config.ticketChannel);

		String desc = "Do you need help? Please create a " + ticketChannel.getAsMention()
				+ " and do not request help here.";

		builder.setDescription(desc);

		channel.sendMessageEmbeds(builder.build()).queue(message -> {
			messageTicketId = message.getIdLong();
			lastSendTicketMessage = System.currentTimeMillis();
		});

	}

}