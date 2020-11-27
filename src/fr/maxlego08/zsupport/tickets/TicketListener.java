package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.utils.Request;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class TicketListener extends ListenerAdapter {

	private final TicketManager manager;

	public TicketListener(TicketManager manager) {
		super();
		this.manager = manager;
	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		if (event.getChannel().getIdLong() == Config.ticketChannel && !event.getUser().isBot()) {
			if (event.getReactionEmote().toString().equals("RE:U+1f1faU+1f1f8"))
				manager.createTicket(event.getUser(), event.getGuild(), LangType.US);
			if (event.getReactionEmote().toString().equals("RE:U+1f1ebU+1f1f7"))
				manager.createTicket(event.getUser(), event.getGuild(), LangType.FR);
			event.getReaction().removeReaction(event.getUser()).queue();
		} else {

			if (event.getChannel().getName().contains("ticket-") && event.getReactionEmote().isEmote()
					&& !event.getUser().isBot()) {

				long id = event.getReactionEmote().getIdLong();
				manager.choosePlugin(event.getGuild(), event.getUser(), id);
				event.getReaction().removeReaction(event.getUser()).queue();

			} else if (event.getChannel().getName().contains("ticket-") && !event.getUser().isBot()) {

				event.getReaction().removeReaction(event.getUser()).queue();
				if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {

					Request request = event.getReactionEmote().toString().equals("RE:U+274c") ? Request.ERROR
							: event.getReactionEmote().toString().equals("RE:U+2705") ? Request.VALID : Request.NOP;

					if (request.equals(Request.NOP))
						return;

					manager.payment(event.getGuild(), event.getMember(), event.getChannel(), request);

				}

			}

		}
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		Message message = event.getMessage();
		if (message == null || message.getCategory() == null)
			return;

		if (message.getCategory().getIdLong() == Config.ticketCategoryId)
			manager.onPlayerMessage(event.getGuild(), event.getMember(), message, event.getTextChannel());

	}

}
