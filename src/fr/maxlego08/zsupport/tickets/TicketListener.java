package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class TicketListener extends ListenerAdapter implements Constant {

	private final TicketManager manager;

	public TicketListener(TicketManager manager) {
		super();
		this.manager = manager;
	}	

	@Override
	public void onButtonClick(ButtonClickEvent event) {

		Button button = event.getButton();

		if (event.getChannel().getIdLong() == Config.ticketChannel && !event.getUser().isBot()) {

			LangType langType = button.getId().equals(BUTTON_FR) ? LangType.FR : LangType.US;
			this.manager.createTicket(event.getUser(), event.getGuild(), langType, event.getChannel(),
					event);

		}

	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {

		if (event.getChannel().getName().contains("ticket-") && event.getReactionEmote().isEmote()
				&& !event.getUser().isBot()) {

			long id = event.getReactionEmote().getIdLong();
			event.getReaction().removeReaction(event.getUser()).queue();

			// Permet de choisir le plugin
			manager.choosePlugin(event.getGuild(), event.getUser(), id);

		}
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

		User user = event.getUser();
		Guild guild = event.getGuild();
		manager.userLeave(guild, user);
	}

}
