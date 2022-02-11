package fr.maxlego08.zsupport.tickets.steps;

import fr.maxlego08.zsupport.api.DiscordPlayer;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.Step;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketStep;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class TicketPlugin extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user) {

		Plugin plugin = ticket.getPlugin();

		TextChannel channel = (TextChannel) messageChannel;
		ChannelManager channelManager = channel.getManager();
		channelManager.setName(ticket.getName() + "-" + plugin.getName()).queue(e -> {

			PermissionOverrideAction permissionOverrideAction = channel.putPermissionOverride(member);
			permissionOverrideAction.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue(e2 -> {

				EmbedBuilder builder = this.createEmbed();
				VerifyManager manager = VerifyManager.getInstance();
				StringBuilder stringBuilder = new StringBuilder();

				if (plugin.isReal()) {
					String message = this.ticket.getMessage(Message.TICKET_PLUGIN, plugin.getName(),
							guild.getEmoteById(plugin.getEmoteId()).getAsMention());

					message += "\n";
					message += "\n";

					if (!hasRole(this.member, plugin.getRole()) && plugin.isPremium()) {
						message += this.ticket.getMessage(Message.TICKET_PLUGIN_ROLE_ERROR);
					}

					message += this.ticket.getMessage(Message.TICKET_PLUGIN_ROLE);

					stringBuilder.append(message);

				} else {

					String message = this.ticket.getMessage(Message.TICKET_OTHER);
					stringBuilder.append(message);

				}

				builder.setDescription(stringBuilder.toString());

				this.event.editMessageEmbeds(builder.build()).setActionRow(this.createCloseButton()).queue();
				this.ticket.setWaiting(true);

				if (!hasRole(this.member, plugin.getRole()) && plugin.isPremium()) {
					PlayerSender sender = new DiscordPlayer(user, this.member, channel);
					manager.submitData(user, channel, sender, false);
				}

				manager.getGUser(user.getIdLong(), gUser -> {

					EmbedBuilder embedBuilder = this.createEmbed();
					embedBuilder.setThumbnail(gUser.getAvatar());
					embedBuilder.setTitle(gUser.getName(), gUser.getDashboardURL());

					messageChannel.sendMessageEmbeds(embedBuilder.build()).queue();

				});
			});

		});

	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonClickEvent event) {

		if (button.getId().equals(BUTTON_CLOSE)) {
			this.closeTicket(ticket, event);
		}

	}

	@Override
	public TicketStep getStep() {
		return TicketStep.PLUGIN;
	}

}
