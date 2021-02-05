package fr.maxlego08.zsupport.tickets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Saveable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

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

	public void createTicket(User user, Guild guild, LangType type) {

		Ticket ticket = getByUser(user);
		// Le joueur a déjà un ticket
		if (ticket != null) {

			ticket.message(fr.maxlego08.zsupport.lang.Message.TICKET_ALREADY_CREATE);

		} else {

			ticket = new Ticket(type, guild.getIdLong(), user.getIdLong());
			ticket.build(user, guild, getTicketFormat());
			tickets.add(ticket);
			ZSupport.instance.save();

		}

	}

	/**
	 * 
	 * @return
	 */
	private String getTicketFormat() {
		int ticket = Config.ticketNumber;
		String tickets = "";
		if (ticket < 10) {
			tickets = "000" + ticket;
		}
		if (ticket < 100 && ticket >= 10) {
			tickets = "00" + ticket;
		}
		if (ticket >= 100 && ticket < 1000) {
			tickets = "0" + ticket;
		}
		if (ticket > 1000) {
			tickets = "" + ticket;
		}
		Config.ticketNumber++;
		return "ticket-#" + tickets;
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

}