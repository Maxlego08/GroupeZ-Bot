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
		channelManager.setName(channel.getName() + "-" + plugin.getName()).queue(e -> {

			PermissionOverrideAction permissionOverrideAction = channel.putPermissionOverride(member);
			permissionOverrideAction.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue(e2 -> {

				EmbedBuilder builder = this.createEmbed();
				
				StringBuilder stringBuilder = new StringBuilder();

				String message = this.ticket.getMessage(Message.TICKET_PLUGIN, plugin.getName(), guild.getEmoteById(plugin.getEmoteId()).getAsMention());

				message += "\n";
				message += "\n";

				if (!hasRole(member, plugin.getRole())) {
					message += this.ticket.getMessage(Message.TICKET_PLUGIN_ROLE_ERROR);

					PlayerSender sender = new DiscordPlayer(user, member, channel);
					VerifyManager manager = VerifyManager.getInstance();
					manager.submitData(user, channel, sender, false);

				}

				message += this.ticket.getMessage(Message.TICKET_PLUGIN_ROLE);

				stringBuilder.append(message);

				builder.setDescription(stringBuilder.toString());

				this.event.editMessageEmbeds(builder.build()).setActionRow(this.createCloseButton()).queue();
				
				this.ticket.setWaiting(true);

			});

		});

	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonClickEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public TicketStep getStep() {
		return TicketStep.PLUGIN;
	}

}
