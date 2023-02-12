package fr.maxlego08.zsupport.tickets.steps;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.api.DiscordPlayer;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.Step;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketStep;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.utils.image.ImageHelper;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.Button;

public class TicketPlugin extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Interaction interaction) {

		Plugin plugin = ticket.getPlugin();
		String name = ticket.getName() + "-" + plugin.getName();

		this.endQuestions(guild, name);

		TextChannel channel = ticket.getTextChannel(guild);

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
			message += this.ticket.getMessage(Message.TICKET_PLUGIN_RULES);

			stringBuilder.append(message);

		} else {

			String message = this.ticket.getMessage(Message.TICKET_OTHER);
			stringBuilder.append(message);

		}

		builder.setDescription(stringBuilder.toString());

		ticket.getFirstMessage().editMessageEmbeds(builder.build()).setActionRow(this.createCloseButton()).queue();
		ticket.setWaiting(false);

		if (!hasRole(this.member, plugin.getRole()) && plugin.isPremium()) {
			PlayerSender sender = new DiscordPlayer(user, this.member, channel);
			manager.submitData(user, channel, sender, false, gUser -> {

				List<Role> roles = gUser.getPlugins().stream().map(e -> {
					Optional<Plugin> optional = Config.getPlugin(e.getPluginId());
					if (optional.isPresent()) {
						Plugin localPlugin = optional.get();
						Role role = guild.getRoleById(localPlugin.getRole());
						if (hasRole(member, localPlugin.getRole())) {
							return null;
						}
						return role;
					}
					return null;
				}).filter(e -> e != null).collect(Collectors.toList());

				if (roles.isEmpty()) {

					EmbedBuilder localBuilder = new EmbedBuilder();

					localBuilder.setColor(new Color(240, 10, 10));
					localBuilder.setFooter("2023 - " + guild.getName(), guild.getIconUrl());

					localBuilder.setDescription("Impossible to check if you have purchased the plugin." + "\n" + "\n"
							+ "To get access to the support you need to provide a **proof of purchase**, a screen of your payment or a screen of the spigot page for example.");

					localBuilder.setTitle("GroupeZ - " + user.getAsTag(), gUser.getDashboardURL());
					localBuilder.setThumbnail(gUser.getAvatar());

					channel.sendMessageEmbeds(localBuilder.build()).queue();

				} else {

					EmbedBuilder localBuilder = this.createEmbed();

					if (roles.size() == 1) {
						localBuilder.setDescription("You just got the role: " + roles.get(0).getAsMention());
					} else {
						String str = toList(roles.stream().map(e -> e.getAsMention()).collect(Collectors.toList()));
						localBuilder.setDescription("You just got the roles: " + str);
					}

					channel.sendMessageEmbeds(localBuilder.build()).queue();

				}

			}, interaction);
		}

		manager.getGUser(user.getIdLong(), gUser -> {

			EmbedBuilder embedBuilder = this.createEmbed();

			try {
				int rgb[] = ImageHelper.getHexColor(gUser.getAvatar());

				if (rgb.length == 3) {
					embedBuilder.setColor(new Color(rgb[0], rgb[1], rgb[2]));
				}
				embedBuilder.setThumbnail(gUser.getAvatar());
				embedBuilder.setTitle(gUser.getName(), gUser.getDashboardURL());
				messageChannel.sendMessageEmbeds(embedBuilder.build()).queue();

			} catch (IOException e1) {
				e1.printStackTrace();
			}

		});
		
		EmbedBuilder builderInfo = this.createEmbed();
		builderInfo.setDescription(this.getMessage(ticket.getLang(), Message.TICKET_PLUGIN_INFO));
		
		channel.sendMessageEmbeds(builderInfo.build()).queue();

	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonClickEvent event) {
	}

	@Override
	public TicketStep getStep() {
		return TicketStep.PLUGIN;
	}

}
