package fr.maxlego08.zsupport.tickets.steps;

import java.util.concurrent.TimeUnit;

import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.plugins.PluginManager;
import fr.maxlego08.zsupport.tickets.Step;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketStep;
import fr.maxlego08.zsupport.utils.Plugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class TicketPluginVersion extends Step {

	private boolean isVerify = false;

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Interaction interaction) {

		Plugin plugin = ticket.getPlugin();
		/*
		 * String name = ticket.getName() + "-" + plugin.getName();
		 * 
		 * this.endQuestions(guild, name);
		 */

		TextChannel channel = this.ticket.getTextChannel(guild);
		PermissionOverrideAction permissionOverrideAction = channel.putPermissionOverride(member);
		permissionOverrideAction.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();

		EmbedBuilder builder = this.createEmbed();
		builder.setDescription(this.ticket.getMessage(
				plugin.hasCommand() ? Message.TICKET_PLUGIN_VERSION_COMMAND : Message.TICKET_PLUGIN_VERSION_CONSOLE,
				plugin.hasCommand() ? plugin.getCommand() : ""));
		builder.setImage(
				"https://img.groupez.dev/zauctionhouse/" + (plugin.hasCommand() ? "version" : "console") + ".png");

		this.event.editMessageEmbeds(builder.build()).setActionRow(this.createCloseButton()).queue();
	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonClickEvent event) {

	}

	@Override
	protected void onMessage(Ticket ticket, MessageChannel channel, Guild guild, User user,
			MessageReceivedEvent event) {

		if (this.isVerify) {
			return;
		}

		this.isVerify = true;

		TextChannel textChannel = this.ticket.getTextChannel(guild);
		PermissionOverrideAction permissionOverrideAction = textChannel.putPermissionOverride(event.getMember());
		permissionOverrideAction.setAllow(Permission.MESSAGE_READ).queue();

		net.dv8tion.jda.api.entities.Message message = event.getMessage();
		String content = message.getContentRaw();

		message.reply("Checking the current plugin version...").queue(msg -> {
			PluginManager.fetchResource(ticket.getPlugin(), resource -> {

				boolean isGoodVersion = content.equals(resource.getVersion().getVersion());

				if (isGoodVersion) {

					msg.editMessage("You are using the latest version of the plugin.").queueAfter(1, TimeUnit.SECONDS,
							m -> {
								Step step = TicketStep.PLUGIN.getStep();
								this.ticket.setStep(step);
								step.preProcess(this.manager, this.ticket, channel, guild, user, this.event, null);

								m.delete().queueAfter(20, TimeUnit.SECONDS);
							});

				} else {

					EmbedBuilder builder = this.createEmbed();
					builder.setDescription(this.ticket.getMessage(Message.TICKET_PLUGIN_VERSION_ERROR));

					msg.editMessageEmbeds(builder.build()).queue(m -> {
						ticket.setClose(true);
						m.delete().queueAfter(30, TimeUnit.SECONDS, v -> {
							PermissionOverrideAction a = textChannel.putPermissionOverride(event.getMember());
							a.clear(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue(e -> {
								ChannelManager channelManager = textChannel.getManager();
								channelManager.setName(ticket.getName() + "-close").queue();
							});
						});
					});
				}
			});
		});
	}

	@Override
	public TicketStep getStep() {
		return TicketStep.PLUGIN_VERSION;
	}

}
