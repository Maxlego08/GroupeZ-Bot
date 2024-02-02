package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.actions.TicketAction;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.ZUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class Ticket extends ZUtils {

    private final LangType langType;
    private final long channelId;
    private final long userId;
    private final long createdAt;
    private long updatedAt;
    private long id;
    private TicketStatus ticketStatus;
    private TicketType ticketType;
    private long pluginId;

    private TextChannel textChannel;
    private TicketAction ticketAction;

    public Ticket(LangType langType, long channelId, long userId, TicketStatus ticketStatus, TicketType ticketType) {
        this.langType = langType;
        this.channelId = channelId;
        this.userId = userId;
        this.ticketStatus = ticketStatus;
        this.ticketType = ticketType;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LangType getLangType() {
        return langType;
    }

    public long getChannelId() {
        return channelId;
    }

    public long getUserId() {
        return userId;
    }

    public TicketStatus getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(TicketStatus ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public TicketType getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketType ticketType) {
        this.ticketType = ticketType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void sendMessage(Guild guild, Message message) {
        sendMessage(guild, getMessage(this.langType, message));
    }

    public void sendMessage(Guild guild, String message) {

        TextChannel channel = guild.getTextChannelById(this.channelId);
        if (channel == null) return;

        message = message.replace("%user%", getUser().getAsMention());
        channel.sendMessage(message).queue();
    }

    public User getUser() {
        return ZSupport.instance.getJda().getUserById(this.userId);
    }

    public TextChannel getTextChannel(Guild guild) {
        if (this.textChannel == null) {
            this.textChannel = guild.getTextChannelById(this.channelId);
        }
        return this.textChannel;
    }

    public void setTextChannel(TextChannel textChannel) {
        this.textChannel = textChannel;
    }

    public TicketAction getTicketAction() {
        return ticketAction;
    }

    public void setTicketAction(TicketAction ticketAction) {
        this.ticketAction = ticketAction;
    }

    public long getPluginId() {
        return pluginId;
    }

    public void setPluginId(long pluginId) {
        this.pluginId = pluginId;
    }

    public Plugin getPlugin() {
        return Config.getPlugin((int) this.pluginId).orElse(Plugin.EMPTY);
    }

    public void update() {
        this.updatedAt = System.currentTimeMillis();
    }

    public void close(Guild guild) {
        this.ticketStatus = TicketStatus.CLOSE;
        TextChannel textChannel = getTextChannel(guild);
        Member member = guild.getMemberById(this.userId);
        PermissionOverrideAction permissionOverrideAction = textChannel.upsertPermissionOverride(member);
        permissionOverrideAction.clear(Permission.VIEW_CHANNEL, Permission.MESSAGE_SEND).queue();
    }
}
