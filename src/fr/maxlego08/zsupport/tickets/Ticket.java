package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.ZUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

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
		return this.type;
	}

	/**
	 * @return the isWaiting
	 */
	public boolean isWaiting() {
		return this.isWaiting;
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
		return this.type;
	}

	/**
	 * @return the ticketStep
	 */
	public TicketStep getTicketStep() {
		return this.ticketStep;
	}

	/**
	 * @return the step
	 */
	public Step getStep() {
		return this.step;
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
		return this.channelId;
	}

	/**
	 * @return the guildId
	 */
	public long getGuildId() {
		return this.guildId;
	}

	public void setTextChannel(TextChannel textChannel) {
		this.textChannel = textChannel;
		this.channelId = textChannel.getIdLong();
	}

	public TextChannel getTextChannel() {
		return this.textChannel;
	}

	public TextChannel getTextChannel(Guild guild) {
		if (this.textChannel == null) {
			this.textChannel = guild.getTextChannelById(this.channelId);
		}
		return this.textChannel;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return this.userId;
	}

	public String getName() {
		return this.name;
	}

	public void setPlugin(Plugin plugin) {
		this.pluginName = plugin.getName();
	}

	public Plugin getPlugin() {
		return Config.plugins.stream().filter(e -> e.getName().equals(this.pluginName)).findFirst()
				.orElse(new Plugin(this.pluginName, 0, 0, 0, 0));
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
		Guild guild = jda.getGuildById(this.guildId);
		TextChannel channel = guild.getTextChannelById(this.channelId);

		message = message.replace("%user%", jda.getUserById(this.userId).getAsMention());

		channel.sendMessage(message).queue();

	}

	public boolean isValid() {
		ZSupport instance = ZSupport.instance;
		JDA jda = instance.getJda();
		Guild guild = jda.getGuildById(this.guildId);
		return guild != null ? guild.getTextChannelById(this.channelId) != null ? true : false : false;
	}

	public void step(TicketManager manager) {
		if (this.ticketStep == null) {
			System.out.println("Le step est null!");
			return;
		}
		this.step = this.ticketStep.getStep();
		this.step.manager = manager;
		this.step.ticket = this;
		
		System.out.println(this.step);
	}

}
