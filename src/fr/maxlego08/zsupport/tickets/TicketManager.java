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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.ChannelInfo.ChannelType;
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
import net.dv8tion.jda.api.entities.ISnowflake;
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

	private static Map<Long, ChannelInfo> channels = new HashMap<>();

	// 48 hours
	private transient long ticketCloseAfterMilliseconds = 1000 * 60 * 60 * 24 * 2;

	public TicketManager(ZSupport support) {
		super();

		new Timer().scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				if (!support.getCommandListener().isRunning()) {
					this.cancel();
					return;
				}

				tickets.forEach(ticket -> {
					if (!ticket.isWaiting() && !ticket.isClose()) {
						checkTicketsActivity(support, ticket);
					}
				});

			}
		}, 60 * 1000, 60 * 1000); // Every 1 minute
	}

	/*
	 * Allows to check if the tickets are inactive
	 */
	private void checkTicketsActivity(ZSupport support, Ticket ticket) {

		if (ticket.getLastMessageAt() == 0) {
			ticket.setLastMessageAt(System.currentTimeMillis());
		}

		long diff = System.currentTimeMillis() - ticket.getLastMessageAt();

		if (diff > this.ticketCloseAfterMilliseconds) {

			ticket.setClose(true);
			Guild guild = support.getJda().getGuilds().stream().filter(e -> e.getIdLong() == ticket.getGuildId())
					.findFirst().get();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("GroupeZ - Support");
			builder.setColor(new Color(45, 45, 45));
			builder.setFooter("2023 - " + guild.getName(), guild.getIconUrl());
			builder.setTimestamp(OffsetDateTime.now());

			builder.setDescription("Closing your ticket for inactivity in **30 seconds**.");

			TextChannel channel = ticket.getTextChannel(guild);

			Member member = guild.getMember(support.getJda().getUserById(ticket.getUserId()));
			channel.sendMessage(member.getAsMention()).queue();

			channel.sendMessageEmbeds(builder.build()).queue(msg -> {

				builder.setDescription("Closing your ticket for inactivity in **15 seconds**.");
				msg.editMessageEmbeds(builder.build()).queueAfter(15, TimeUnit.SECONDS, e2 -> {

					builder.setDescription("Closing your ticket for inactivity in **now**.");
					msg.editMessageEmbeds(builder.build()).queueAfter(15, TimeUnit.SECONDS, e3 -> {
						PermissionOverrideAction permissionOverrideAction = channel.putPermissionOverride(member);
						permissionOverrideAction.clear(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();

						ChannelManager channelManager = channel.getManager();
						channelManager.setName(ticket.getName() + "-close").queue();
					});
				});
			});
		}
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
			builder.setFooter("2023 - " + guild.getName(), guild.getIconUrl());
			builder.setTimestamp(OffsetDateTime.now());

			builder.setDescription(this.getMessage(type, Message.TICKET_CREATE_WAIT));

			event.deferReply(true).addEmbeds(builder.build()).queue(message -> {

				manager.userIsLink(user, () -> {

					Ticket createdTicket = new Ticket(type, guild.getIdLong(), user.getIdLong(), this.getTicketName());
					createdTicket.setWaiting(true);

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

	public void stepMessage(MessageReceivedEvent event, User user, Guild guild, MessageChannel channel) {

		Optional<Ticket> optional = this.getByUser(user);
		if (optional.isPresent()) {

			Ticket ticket = optional.get();
			Step step = ticket.getStep();

			step.preMessage(this, event, user, guild, channel);

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
			ticket.setLastMessageAt(System.currentTimeMillis());

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

	public void sendTicketUse(MessageReceivedEvent event, TextChannel textChannel, ChannelType channelType) {

		ChannelInfo channelInfo = getInfo(textChannel);

		if (channelInfo.getMessageId() == 0) {

			sendTicketInformations(textChannel, channelType, channelInfo);

		} else {

			textChannel.getHistoryAround(channelInfo.getMessageId(), 99).queue(history -> {
				net.dv8tion.jda.api.entities.Message message = history.getMessageById(channelInfo.getMessageId());
				if (message != null) {
					if (System.currentTimeMillis() > channelInfo.getMessageAt()) {
						channelInfo.setMessageId(0);
						message.delete().queue(s -> {
							sendTicketInformations(textChannel, channelType, channelInfo);
						});
						return;
					}
				}
				sendTicketInformations(textChannel, channelType, channelInfo);
			});
		}
	}

	public void sendTicketInformations(TextChannel channel, ChannelType channelType, ChannelInfo channelInfo) {

		System.out.println("Je peux envoyer l'info ? " + channelType + " - "
				+ (channelInfo.getMessageAt() > System.currentTimeMillis()) + " -> " + channelInfo.getMessageAt() + " "
				+ System.currentTimeMillis());

		if (channelInfo.getMessageAt() > System.currentTimeMillis()) {
			return;
		}

		System.out.println("Je vais sauvegardé !");
		channelInfo.setMessageAt(System.currentTimeMillis() + (1000 * 60 * 10));

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(channelType.getTitle());
		builder.setColor(new Color(45, 200, 45));
		builder.setTimestamp(OffsetDateTime.now());
		builder.setFooter("2023 - " + channel.getGuild().getName(), channel.getGuild().getIconUrl());

		TextChannel ticketChannel = channel.getGuild().getTextChannelById(Config.ticketChannel);
		builder.setDescription(String.format(channelType.getDescription(), ticketChannel.getAsMention()));

		channel.sendMessageEmbeds(builder.build()).queue(message -> {
			channelInfo.setMessageId(message.getIdLong());
		});

	}

	private ChannelInfo getInfo(long channelId) {
		if (!channels.containsKey(channelId)) {
			channels.put(channelId, new ChannelInfo());
		}
		return channels.get(channelId);
	}

	private ChannelInfo getInfo(ISnowflake iSnowflake) {
		return getInfo(iSnowflake.getIdLong());
	}

}