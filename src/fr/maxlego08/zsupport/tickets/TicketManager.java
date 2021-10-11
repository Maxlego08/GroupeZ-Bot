package fr.maxlego08.zsupport.tickets;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Saveable;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class TicketManager extends ZUtils implements Constant, Saveable {

	private static List<Ticket> tickets = new ArrayList<Ticket>();

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
	public Ticket getByUser(User user) {
		ticketIsValid();
		return tickets.stream().filter(ticket -> ticket.getUserId() == user.getIdLong()).findAny().orElse(null);
	}

	/**
	 * 
	 * @param user
	 * @return
	 */
	public Ticket getByChannel(TextChannel channel) {
		ticketIsValid();
		return tickets.stream().filter(ticket -> ticket.getChannelId() == channel.getIdLong()).findAny().orElse(null);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Plugin getById(long id) {
		return Config.plugins.stream().filter(l -> l.getEmoteId() == id).findAny().orElse(null);
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
	 */
	public void createTicket(User user, Guild guild, LangType type, TextChannel channel) {

		Ticket ticket = getByUser(user);
		if (ticket != null) {

			ticket.message(fr.maxlego08.zsupport.lang.Message.TICKET_ALREADY_CREATE);

		} else {

			VerifyManager manager = VerifyManager.getInstance();

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("GroupeZ - Support");
			builder.setColor(Color.getHSBColor(45, 45, 45));
			builder.setFooter("2021 - " + guild.getName(), guild.getIconUrl());
			builder.setTimestamp(OffsetDateTime.now());

			builder.setDescription(this.getMessage(type, Message.TICKET_CREATE_WAIT));

			channel.sendMessage(builder.build()).queue(message -> {

				manager.userIsLink(user, () -> {

					Ticket createdTicket = new Ticket(type, guild.getIdLong(), user.getIdLong());
					createdTicket.build(user, guild, getTicketFormat(), textChannel -> {

						String stringMessage = this.getMessage(type, Message.TICKET_CREATE_SUCCESS)
								+ textChannel.getAsMention();
						builder.setDescription(stringMessage);
						builder.setColor(Color.getHSBColor(45, 250, 45));

						message.editMessage(builder.build()).queue(messageEdit -> {
							schedule(1000 * 10, () -> messageEdit.delete().queue());
						});
					});
					tickets.add(createdTicket);
					ZSupport.instance.save();

				}, () -> {

					builder.setDescription(this.getMessage(type, Message.TICKET_CREATE_ERROR));
					builder.setColor(Color.getHSBColor(250, 45, 45));

					message.editMessage(builder.build()).queue(messageEdit -> {
						schedule(1000 * 10, () -> messageEdit.delete().queue());
					});

				});

			});

		}

	}

	/**
	 * 
	 * @return
	 */
	private String getTicketFormat() {
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
	}

	/**
	 * 
	 * @param guild
	 * @param user
	 * @param id
	 */
	public void choosePlugin(Guild guild, User user, long id) {

		Ticket ticket = getByUser(user);
		// Le joueur a déjà un ticket
		if (ticket != null && ticket.isWaiting()) {

			Plugin plugin = getById(id);
			if (plugin != null) {

				ticket.choose(plugin, guild, user);

			}

		}
	}

	public void createVocal(PlayerSender player, TextChannel textChannel, Guild guild) {
		if (player.hasPermission(Permission.MANAGE_CHANNEL)) {

			Ticket ticket = getByChannel(textChannel);
			if (ticket == null) {
				System.out.println("Impossible de trouver le ticket.");
				return;
			}

			Member member = guild.getMemberById(ticket.getUserId());

			VoiceChannel channel = guild.getCategoryById(Config.ticketCategoryId)
					.createVoiceChannel("vocal-" + ticket.getName()).complete();
			PermissionOverrideAction permissionOverrideAction = channel.createPermissionOverride(member);
			permissionOverrideAction.setAllow(Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.MESSAGE_READ)
					.complete();

		} else
			player.sendMessage("You don't have permission");
	}

}