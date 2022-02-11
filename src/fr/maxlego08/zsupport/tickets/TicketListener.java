package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

public class TicketListener extends ListenerAdapter implements Constant {

	private final TicketManager manager;

	public TicketListener(TicketManager manager) {
		super();
		this.manager = manager;
	}

	@Override
	public void onSelectionMenu(SelectionMenuEvent event) {
		if (event.getChannel().getName().contains("ticket-") && !event.getUser().isBot()) {

			this.manager.stepSelectionMenu(event, event.getUser(), event.getGuild(), event.getChannel());
			
		}
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event) {

		Button button = event.getButton();

		if (event.getChannel().getIdLong() == Config.ticketChannel && !event.getUser().isBot()) {

			LangType langType = button.getId().equals(BUTTON_FR) ? LangType.FR : LangType.US;
			this.manager.createTicket(event.getUser(), event.getGuild(), langType, event.getChannel(), event);

		} else if (event.getChannel().getName().contains("ticket-") && !event.getUser().isBot()) {

			this.manager.stepButton(event, event.getUser(), event.getGuild(), event.getChannel());

		}

	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event) {

		User user = event.getUser();
		Guild guild = event.getGuild();
		this.manager.userLeave(guild, user);
		
	}

}
