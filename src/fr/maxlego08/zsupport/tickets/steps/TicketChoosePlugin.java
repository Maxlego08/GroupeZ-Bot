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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

public class TicketChoosePlugin extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Interaction interaction) {

		StringSelectMenu.Builder selectionMenu = StringSelectMenu.create(BUTTON_SELECT_PLUGIN);
		Config.plugins.forEach(plugin -> {

			Emoji emote = guild.getEmojiById(plugin.getEmoteId());
			selectionMenu.addOption(plugin.getName(), plugin.getName(), emote);

		});

		selectionMenu.addOption(this.ticket.getMessage(Message.OTHER), "other", Emoji.fromUnicode("U+1F6AB"));

		EmbedBuilder builder = this.createEmbed();

		StringBuilder stringBuilder = this.createDescription();
		stringBuilder.append(this.ticket.getMessage(Message.TICKET_OTHER_INFO));

		builder.setDescription(stringBuilder.toString());

		ticket.setFirstMessage(this.event.getMessage());
		this.event.editMessageEmbeds(builder.build()).setActionRow(selectionMenu.build()).queue();		
	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonInteractionEvent event) {
	}

	@Override
	public void selectionClick(TicketManager ticketManager, StringSelectInteractionEvent event, User user, Guild guild,
			MessageChannel messageChannel) {

		List<String> strings = event.getValues();
		if (strings.size() == 1) {

			String pluginName = strings.get(0);
			Plugin plugin = Config.plugins.stream().filter(l -> l.getName().equals(pluginName)).findAny()
					.orElse(new Plugin("Other", 0, 0, 0, 0, ""));
			this.ticket.setPlugin(plugin);

			if (plugin.isReal()) {

				Step step = TicketStep.PLUGIN_VERSION.getStep();
				this.ticket.setStep(step);
				step.preProcess(this.manager, this.ticket, messageChannel, guild, user, event, null);

			} else {

				Step step = TicketStep.PLUGIN.getStep();
				this.ticket.setStep(step);
				step.preProcess(this.manager, this.ticket, messageChannel, guild, user, event, null);

			}

		} else {

			event.reply(this.ticket.getMessage(Message.TICKET_PLUGIN_ERROR)).queue();

		}

	}

	@Override
	public TicketStep getStep() {
		return TicketStep.CHOOSE_PLUGIN;
	}

}
