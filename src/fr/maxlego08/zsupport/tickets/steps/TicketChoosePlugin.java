package fr.maxlego08.zsupport.tickets.steps;

import java.util.List;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.Step;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketManager;
import fr.maxlego08.zsupport.tickets.TicketStep;
import fr.maxlego08.zsupport.utils.Plugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu.Builder;

public class TicketChoosePlugin extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user) {

		Builder selectionMenu = SelectionMenu.create(BUTTON_SELECT_PLUGIN);
		Config.plugins.forEach(plugin -> {

			Emote emote = guild.getEmoteById(plugin.getEmoteId());
			selectionMenu.addOption(plugin.getName(), plugin.getName(), Emoji.fromEmote(emote));

		});

		selectionMenu.addOption("Autre", "other", Emoji.fromMarkdown("U+1F6AB"));

		EmbedBuilder builder = this.createEmbed();

		StringBuilder stringBuilder = this.createDescription();
		stringBuilder.append("Veuillez choisir le plugin pour lequelle vous avez besoin d'aide.");

		builder.setDescription(stringBuilder.toString());

		this.event.editMessageEmbeds(builder.build()).setActionRow(selectionMenu.build()).queue();

	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonClickEvent event) {
	}

	@Override
	public void selectionClick(TicketManager ticketManager, SelectionMenuEvent event, User user, Guild guild,
			MessageChannel messageChannel) {

		List<String> strings = event.getValues();
		if (strings.size() == 1) {

			String pluginName = strings.get(0);
			Step step = TicketStep.PLUGIN.getStep();
			this.ticket.setStep(step);
			Plugin plugin = Config.plugins.stream().filter(l -> l.getName().equals(pluginName)).findAny()
					.orElse(new Plugin("Other", 0, 0, 0));
			this.ticket.setPlugin(plugin);

			step.preProcess(this.manager, this.ticket, messageChannel, guild, user, event, null);

		} else {

			event.reply(this.ticket.getMessage(Message.TICKET_PLUGIN_ERROR)).queue();

		}

	}

	@Override
	public TicketStep getStep() {
		return TicketStep.CHOOSE_PLUGIN;
	}

}
