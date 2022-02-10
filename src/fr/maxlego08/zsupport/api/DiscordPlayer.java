package fr.maxlego08.zsupport.api;

import java.awt.Color;

import fr.maxlego08.zsupport.exception.ChannelNullException;
import fr.maxlego08.zsupport.lang.BasicMessage;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class DiscordPlayer extends ZUtils implements PlayerSender {

	private final User user;
	private final Member member;
	private final TextChannel channel;

	public DiscordPlayer(User user, Member member, TextChannel channel) {
		super();
		this.user = user;
		this.member = member;
		this.channel = channel;
	}

	@Override
	public long getID() {
		return user.getIdLong();
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public Member getMember() {
		return member;
	}

	@Override
	public TextChannel getTextChannel() {
		return channel;
	}

	@Override
	public String getName() {
		return user.getName();
	}

	@Override
	public void sendMessage(String message) {
		user.openPrivateChannel().complete().sendMessage(message).complete();
	}

	@Override
	public void sendMessage(BasicMessage message) {
		user.openPrivateChannel().complete().sendMessage(message.getMessage()).complete();
	}

	@Override
	public void sendMessage(BasicMessage message, boolean delete, Object... args) {
		if (channel == null)
			throw new ChannelNullException("Le channel est null, impossible d'envoyer un message");
		channel.sendMessage(String.format(message.getMessage(), args)).queue(discordMessage -> {
			schedule(1000 * 10, () -> {
				if (discordMessage != null)
					discordMessage.delete().complete();
			});
		});
	}

	@Override
	public boolean hasPermission(Permission permission) {
		return member.hasPermission(permission);
	}

	@Override
	public void sendEmbed(BasicMessage message, boolean delete) {
		EmbedBuilder builder = new EmbedBuilder();
		builder.setColor(Color.RED);
		builder.setDescription(message.getMessage());
		channel.sendMessageEmbeds(builder.build()).queue(discordMessege -> {
			if (delete)
				schedule(1000 * 10, () -> discordMessege.delete().queue());
		});
	}

}
