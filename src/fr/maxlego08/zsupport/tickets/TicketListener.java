package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.LangType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
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

			ReactionEmote emote = event.getReactionEmote();

			LangType langType = LangType.US;
			if (emote.toString().equals("RE:U+1f1faU+1f1f8"))
				langType = LangType.US;
			if (emote.toString().equals("RE:U+1f1ebU+1f1f7"))
				langType = LangType.FR;

			manager.createTicket(event.getUser(), event.getGuild(), langType, event.getChannel());
			event.getReaction().removeReaction(event.getUser()).queue();

		} else {

			if (event.getChannel().getName().contains("ticket-") && event.getReactionEmote().isEmote()
					&& !event.getUser().isBot()) {

				long id = event.getReactionEmote().getIdLong();
				event.getReaction().removeReaction(event.getUser()).queue();
				
				//Permet de choisir le plugin
				manager.choosePlugin(event.getGuild(), event.getUser(), id);

			} 
		}
	}
	
	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

		User user = event.getUser();
		Guild guild = event.getGuild();
		manager.userLeave(guild, user);
	}
	
}
