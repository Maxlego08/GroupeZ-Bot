package fr.maxlego08.zsupport.tickets.steps;

import java.awt.Color;
import java.io.IOException;

import fr.maxlego08.zsupport.api.DiscordPlayer;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.Step;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketStep;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.utils.image.ImageHelper;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;

public class TicketSpigot extends Step {

	@Override
	public void process(Ticket ticket, MessageChannel messageChannel, Guild guild, User user) {

		EmbedBuilder builder = this.createEmbed();

		builder.setDescription(this.ticket.getMessage(Message.TICKET_SPIGOT));

		this.event.editMessageEmbeds(builder.build()).setActionRow(this.createCloseButton()).queue();

		VerifyManager manager = VerifyManager.getInstance();
		TextChannel channel = ticket.getTextChannel(guild);
		PlayerSender sender = new DiscordPlayer(user, this.member, channel);
		manager.submitData(user, channel, sender, false);
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
		
		this.endQuestions(guild, ticket.getName() + "-spigot");
	}

	@Override
	public void buttonClick(Ticket ticket, MessageChannel messageChannel, Guild guild, User user, Button button,
			ButtonClickEvent event) {
	}

	@Override
	public TicketStep getStep() {
		return TicketStep.CHOOSE_SPIGOT;
	}

}
