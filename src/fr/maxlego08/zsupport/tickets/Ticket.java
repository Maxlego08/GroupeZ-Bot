package fr.maxlego08.zsupport.tickets;

import java.awt.Color;
import java.time.OffsetDateTime;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.utils.Request;
import fr.maxlego08.zsupport.utils.ZUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Ticket extends ZUtils {

	private final LangType type;
	private long channelId;
	private final long guildId;
	private final long userId;
	private boolean isWaiting = true;
	private boolean isPaypal = false;
	private long pluginEmoteId;
	private String name;

	/**
	 * @param lang
	 * @param channelId
	 * @param guildId
	 * @param userId
	 */
	public Ticket(LangType lang, long guildId, long userId) {
		super();
		this.type = lang;
		this.guildId = guildId;
		this.userId = userId;
	}

	/**
	 * @return the type
	 */
	public LangType getType() {
		return type;
	}

	/**
	 * @return the isWaiting
	 */
	public boolean isWaiting() {
		return isWaiting;
	}

	/**
	 * @param isWaiting
	 *            the isWaiting to set
	 */
	public void setWaiting() {
		this.isWaiting = false;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the lang
	 */
	public LangType getLang() {
		return type;
	}

	/**
	 * @return the channelId
	 */
	public long getChannelId() {
		return channelId;
	}

	/**
	 * @return the guildId
	 */
	public long getGuildId() {
		return guildId;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	protected String getMessage(Message message) {
		return super.getMessage(this.type, message);
	}

	/**
	 * 
	 * @param message
	 */
	public void message(Message message) {
		message(getMessage(message));
	}

	public void message(String message) {

		ZSupport instance = ZSupport.instance;
		JDA jda = instance.getJda();
		Guild guild = jda.getGuildById(guildId);
		TextChannel channel = guild.getTextChannelById(channelId);

		message = message.replace("%user%", jda.getUserById(userId).getAsMention());

		channel.sendMessage(message).queue();

	}

	/**
	 * Création du ticket
	 */
	public void build(User user, Guild guild, String ticketName) {

		this.name = ticketName;
		Member member = guild.getMember(user);

		TextChannel channel = guild.getCategoryById(Config.ticketCategoryId).createTextChannel(ticketName).complete();

		this.channelId = channel.getIdLong();

		channel.createPermissionOverride(member).setAllow(Permission.MESSAGE_READ).queue();
		channel.sendMessage(user.getAsMention()).queue((message) -> message.delete().queue());

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("GroupeZ - Support");
		builder.setColor(Color.getHSBColor(45, 45, 45));
		builder.setFooter("2020 - " + guild.getName(), guild.getIconUrl());
		builder.setTimestamp(OffsetDateTime.now());

		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append(getMessage(type, Message.TICKET_DESC));
		stringBuilder.append("\n");
		stringBuilder.append("\n");
		stringBuilder.append(getMessage(type, Message.TICKET_PLUGIN_CHOOSE));
		stringBuilder.append("\n");

		for (Plugin plugin : Config.plugins) {

			stringBuilder.append(guild.getEmoteById(plugin.getEmoteId()).getAsMention() + " " + plugin.getName());
			stringBuilder.append("\n");
		}

		builder.setDescription(stringBuilder.toString());

		net.dv8tion.jda.api.entities.Message message = channel.sendMessage(builder.build()).complete();
		for (Plugin plugin : Config.plugins)
			message.addReaction(guild.getEmoteById(plugin.getEmoteId())).queue();

	}

	public boolean isValid() {
		ZSupport instance = ZSupport.instance;
		JDA jda = instance.getJda();
		Guild guild = jda.getGuildById(guildId);
		return guild != null ? guild.getTextChannelById(channelId) != null ? true : false : false;
	}

	/**
	 * 
	 * @param plugin
	 * @param guild
	 * @param user
	 */
	public void choose(Plugin plugin, Guild guild, User user) {

		Member member = guild.getMember(user);
		TextChannel channel = guild.getTextChannelById(channelId);

		channel.putPermissionOverride(member).setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();

		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle("GroupeZ - Support");
		builder.setColor(Color.getHSBColor(45, 45, 45));
		builder.setFooter("Hébergeur minecraft: https://minestrator.com/?ref=1640");
		builder.setTimestamp(OffsetDateTime.now());

		StringBuilder stringBuilder = new StringBuilder();

		String message = getMessage(type, Message.TICKET_PLUGIN);

		message = message.replace("%plugin%", plugin.getName());
		message = message.replace("%pluginEmote%", guild.getEmoteById(plugin.getEmoteId()).getAsMention());

		message += "\n";
		message += "\n";

		if (!hasRole(member, plugin.getRole())) {
			message += getMessage(Message.TICKET_PLUGIN_ROLE_ERROR);
			message += "\n";
			message += getMessage(Message.TICKET_PLUGIN_ROLE_ERROR_ID);
			builder.setImage("http://img.groupez.xyz/paypal.png");
			this.isPaypal = true;
		} else
			message += getMessage(Message.TICKET_PLUGIN_ROLE);

		stringBuilder.append(message);

		builder.setDescription(stringBuilder.toString());
		channel.sendMessage(builder.build()).queue();

		this.isWaiting = false;
		this.pluginEmoteId = plugin.getEmoteId();
	}

	public boolean isPaypal() {
		return isPaypal;
	}

	public void paypalMessage(Guild guild, Member user, net.dv8tion.jda.api.entities.Message message,
			TextChannel channel) {

		if (!isPaypal())
			return;

		String content = message.getContentRaw();

		if (content.length() != 17) {

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("GroupeZ - Support");
			builder.setColor(Color.getHSBColor(45, 45, 45));
			builder.setFooter("Hébergeur minecraft: https://minestrator.com/?ref=1640");
			builder.setTimestamp(OffsetDateTime.now());

			String msg = getMessage(type, Message.TICKET_PLUGIN_ROLE_ERROR_ID);
			builder.setImage("http://img.groupez.xyz/paypal.png");
			builder.setDescription(msg);

			message.delete().complete();
			net.dv8tion.jda.api.entities.Message discordMessage = channel.sendMessage(builder.build()).complete();
			schedule(1000 * 10, () -> {
				if (discordMessage != null)
					discordMessage.delete().complete();
			});

		} else {

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("GroupeZ - Support");
			builder.setColor(Color.getHSBColor(45, 45, 45));
			builder.setFooter("Hébergeur minecraft: https://minestrator.com/?ref=1640");
			builder.setTimestamp(OffsetDateTime.now());

			String msg = getMessage(type, Message.TICKET_PLUGIN_ROLE_ID_SUCCESS);
			builder.setDescription(msg);
			net.dv8tion.jda.api.entities.Message message2 = channel.sendMessage(builder.build()).complete();
			message2.addReaction("✅").queue();
			message2.addReaction("❌").queue();

			channel.sendMessage(guild.getJDA().getUserById(522359210844094479l).getAsMention())
					.queue(c -> c.delete().queue());

			channel.putPermissionOverride(user).setAllow(Permission.MESSAGE_READ).queue();

		}

	}

	/**
	 * 
	 * @param guild
	 * @param member
	 * @param channel
	 * @param request
	 */
	public void payment(Guild guild, Member member, TextChannel channel, Request request) {

		if (!isPaypal)
			return;

		isPaypal = false;
		Member currentMember = guild.getMemberById(userId);
		channel.putPermissionOverride(currentMember).setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ)
				.queue();

		Plugin plugin = Config.plugins.stream().filter(pl -> pl.getEmoteId() == pluginEmoteId).findAny().orElse(null);
		if (plugin == null)
			return;

		if (request.equals(Request.VALID)) {

			guild.addRoleToMember(currentMember, guild.getJDA().getRoleById(plugin.getRole())).complete();

			String message = getMessage(Message.TICKET_PLUGIN_ROLE_SUCCESS);
			message = message.replace("%plugin%", plugin.getName());
			channel.sendMessage(message).complete();

		} else {

			String message = getMessage(Message.TICKET_PLUGIN_ROLE_ERROR_GIVE);
			message = message.replace("%plugin%", plugin.getName());
			channel.sendMessage(message).complete();
		}
	}

}
