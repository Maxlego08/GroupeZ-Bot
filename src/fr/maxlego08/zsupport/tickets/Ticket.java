package fr.maxlego08.zsupport.tickets;

import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.function.Consumer;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.api.DiscordPlayer;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class Ticket extends ZUtils {

	private final LangType type;
	private long channelId;
	private final long guildId;
	private final long userId;
	private boolean isWaiting = true;
	private String name;
	private TicketStep ticketStep;
	private String pluginName;

	private transient TextChannel textChannel;
	private transient Step step;

	/**
	 * @param lang
	 * @param channelId
	 * @param guildId
	 * @param userId
	 */
	public Ticket(LangType lang, long guildId, long userId, String name) {
		super();
		this.type = lang;
		this.guildId = guildId;
		this.userId = userId;
		this.name = name;
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
	 * @return the ticketStep
	 */
	public TicketStep getTicketStep() {
		return ticketStep;
	}

	/**
	 * @return the step
	 */
	public Step getStep() {
		return step;
	}

	/**
	 * @param channelId
	 *            the channelId to set
	 */
	public void setChannelId(long channelId) {
		this.channelId = channelId;
	}

	/**
	 * @param isWaiting
	 *            the isWaiting to set
	 */
	public void setWaiting(boolean isWaiting) {
		this.isWaiting = isWaiting;
	}

	/**
	 * @param ticketStep
	 *            the ticketStep to set
	 */
	public void setTicketStep(TicketStep ticketStep) {
		this.ticketStep = ticketStep;
	}

	/**
	 * @param step
	 *            the step to set
	 */
	public void setStep(Step step) {
		this.step = step;
		this.ticketStep = step.getStep();
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

	public void setTextChannel(TextChannel textChannel) {
		this.textChannel = textChannel;
		this.channelId = textChannel.getIdLong();
	}

	public TextChannel getTextChannel() {
		return textChannel;
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

	public void setPlugin(Plugin plugin) {
		this.pluginName = plugin.getName();
	}

	public Plugin getPlugin() {
		return Config.plugins.stream().filter(e -> e.getName().equals(this.pluginName)).findFirst().orElse(new Plugin(this.pluginName, 0, 0, 0));
	}

	/**
	 * 
	 * @param message
	 * @return
	 */
	public String getMessage(Message message, Object... args) {
		return super.getMessage(this.type, message, args);
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
	 * 
	 * @param user
	 * @param guild
	 * @param ticketName
	 */
	public void build(User user, Guild guild, String ticketName, Consumer<TextChannel> consumer) {

		this.name = ticketName;
		Member member = guild.getMember(user);

		guild.getCategoryById(Config.ticketCategoryId).createTextChannel(ticketName).queue(channel -> {

			this.channelId = channel.getIdLong();

			channel.sendMessage(user.getAsMention()).queue(message -> message.delete().queue());

			// Gestion des permissions
			PermissionOverrideAction permissionOverrideAction1 = channel.createPermissionOverride(member);
			permissionOverrideAction1.setAllow(Permission.MESSAGE_READ).queue();

			channel.sendMessage(user.getAsMention()).queue((message) -> message.delete().queue());

			EmbedBuilder builder = new EmbedBuilder();
			builder.setTitle("GroupeZ - Support");
			builder.setColor(Color.getHSBColor(45, 45, 45));
			builder.setFooter("2021 - " + guild.getName(), guild.getIconUrl());
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

			channel.sendMessageEmbeds(builder.build()).queue(message -> {
				for (Plugin plugin : Config.plugins)
					message.addReaction(guild.getEmoteById(plugin.getEmoteId())).queue();
			});

			consumer.accept(channel);
		});

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

		PermissionOverrideAction permissionOverrideAction = channel.putPermissionOverride(member);
		permissionOverrideAction.setAllow(Permission.MESSAGE_WRITE, Permission.MESSAGE_READ).queue();

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

			PlayerSender sender = new DiscordPlayer(user, member, channel);
			VerifyManager manager = VerifyManager.getInstance();
			manager.submitData(user, channel, sender, false);

		}

		message += getMessage(Message.TICKET_PLUGIN_ROLE);

		stringBuilder.append(message);

		builder.setDescription(stringBuilder.toString());
		channel.sendMessageEmbeds(builder.build()).queue();

		ChannelManager channelManager = channel.getManager();
		channelManager.setName(channel.getName() + "-" + plugin.getName()).queue();

		this.isWaiting = false;

	}

	public void step(TicketManager manager) {
		if (this.ticketStep == null) {
			System.out.println("Le step est null!");
			return;
		}
		this.step = this.ticketStep.getStep();
		this.step.manager = manager;
		this.step.ticket = this;
	}

}
