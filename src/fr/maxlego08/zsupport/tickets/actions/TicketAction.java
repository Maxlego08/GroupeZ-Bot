package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketManager;
import fr.maxlego08.zsupport.tickets.TicketStatus;
import fr.maxlego08.zsupport.tickets.TicketType;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public abstract class TicketAction extends ZUtils {

    protected TicketManager ticketManager;
    protected Ticket ticket;
    protected Guild guild;
    protected TextChannel textChannel;
    protected User user;
    protected Member member;
    protected Interaction event;

    public abstract void process(Interaction interaction);

    public abstract void onButton(ButtonInteractionEvent event);

    public abstract void onSelect(StringSelectInteractionEvent event);

    public abstract void onModal(ModalInteractionEvent event);

    public abstract void onMessage(MessageReceivedEvent event);

    public abstract TicketStatus getTicketStatus();

    public void preProcess(TicketManager ticketManager, Ticket ticket, Guild guild, TextChannel textChannel, Member member, Interaction interaction) {
        this.ticketManager = ticketManager;
        this.ticket = ticket;
        this.guild = guild;
        this.textChannel = textChannel;
        this.member = member;
        this.user = member.getUser();
        this.event = interaction;
        this.process(interaction);
    }

    protected EmbedBuilder createEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("GroupeZ - Support");
        setEmbedFooter(this.guild, builder, new Color(45, 150, 45));
        return builder;
    }

    public void preButtonAction(ButtonInteractionEvent event, Member member, Guild guild, TicketManager ticketManager, Ticket ticket) {
        this.ticketManager = ticketManager;
        this.ticket = ticket;
        this.guild = guild;
        this.textChannel = ticket.getTextChannel(guild);
        this.member = member;
        this.user = member.getUser();
        this.event = event;
        onButton(event);

        this.processModeratorAccept(event);
    }

    protected void processNextAction(TicketStatus ticketStatus) {

        TicketAction ticketAction = ticketStatus.getAction();
        if (ticketAction == null) return;

        this.ticket.setTicketStatus(ticketStatus);
        this.ticket.setTicketAction(ticketAction);

        this.ticketManager.updateTicket(ticket);
        processChannelName(ticketStatus);

        ticketAction.preProcess(this.ticketManager, this.ticket, this.guild, this.textChannel, this.member, this.event);
    }

    public void processChannelName(TicketStatus ticketStatus) {
        if (ticketStatus.getChannelName() != null) {
            TextChannelManager channelManager = this.textChannel.getManager();
            String channelName = ticketStatus.getChannelName();
            channelName = channelName.replace("%id%", String.format("%04d", ticket.getId()));
            channelName = channelName.replace("%plugin%", ticket.getPlugin().getName());
            channelManager.setName("ticket-" + channelName).queue();
        }
    }

    protected void replyModal(Modal modal) {
        if (this.event instanceof IModalCallback) {
            ((IModalCallback) this.event).replyModal(modal).queue();
        } else if (this.event instanceof IReplyCallback) {
            ((IReplyCallback) this.event).reply("An error occurred during the interaction.").queue();
        }
    }

    protected StringBuilder createDescription() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Bienvenue sur le support de **GroupeZ**");
        stringBuilder.append("\n");
        stringBuilder.append("Pour une meilleur prise en charge de votre demande, merci de répondre aux questions:");
        stringBuilder.append("\n");
        stringBuilder.append("\n");
        return stringBuilder;
    }

    public void preSelectionAction(StringSelectInteractionEvent event, Member member, Guild guild, TicketManager ticketManager, Ticket ticket) {
        this.ticketManager = ticketManager;
        this.ticket = ticket;
        this.guild = guild;
        this.textChannel = ticket.getTextChannel(guild);
        this.member = member;
        this.user = member.getUser();
        this.event = event;
        this.onSelect(event);
    }

    protected ItemComponent createCloseButton() {
        return new ButtonImpl(BUTTON_CLOSE, "Close", ButtonStyle.DANGER, false, Emoji.fromUnicode("U+26A0"));
    }

    public void preModalAction(ModalInteractionEvent event, Member member, Guild guild, TicketManager ticketManager, Ticket ticket) {
        this.ticketManager = ticketManager;
        this.ticket = ticket;
        this.guild = guild;
        this.textChannel = ticket.getTextChannel(guild);
        this.member = member;
        this.user = member.getUser();
        this.event = event;
        this.onModal(event);
    }

    public void preMessageAction(MessageReceivedEvent event, Member member, Guild guild, TicketManager ticketManager, Ticket ticket) {
        this.ticketManager = ticketManager;
        this.ticket = ticket;
        this.guild = guild;
        this.textChannel = ticket.getTextChannel(guild);
        this.member = member;
        this.user = member.getUser();
        this.event = null;
        this.onMessage(event);
    }

    protected void sendModeratorAccept() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("GroupeZ - Support");
        setDescription(builder, ":question: A member of the team will check your purchase, please wait.");
        setEmbedFooter(this.guild, builder, new Color(220, 111, 18));
        Button button = new ButtonImpl(BUTTON_MODERATOR_ACCEPT, "Verify the purchase", ButtonStyle.SUCCESS, false, Emoji.fromUnicode("U+2705"));
        this.textChannel.sendMessageEmbeds(builder.build()).setActionRow(button).queue();
    }

    private void processModeratorAccept(ButtonInteractionEvent event) {
        if (!Objects.equals(event.getButton().getId(), BUTTON_MODERATOR_ACCEPT)) return;

        if (this.member.hasPermission(Permission.MESSAGE_MANAGE)) {

            EmbedBuilder builder = new EmbedBuilder();
            builder.setTitle("GroupeZ - Support");
            setDescription(builder, ":white_check_mark: Your purchase has been validated by a member of the team.");
            setEmbedFooter(this.guild, builder, new Color(29, 229, 15));
            event.editMessageEmbeds(builder.build()).queue();

            Member target = this.guild.getMemberById(this.ticket.getUserId());
            VerifyManager manager = VerifyManager.getInstance();
            manager.updateUserAsync(null, event.getGuild(), target, this.ticket.getPluginId(), event.getMessage());

            if (ticket.getTicketType() == TicketType.VERIFICATION) {
                ticket.close(guild);
                this.ticketManager.getSqlManager().updateTicket(ticket, true);
                event.getChannel().delete().queueAfter(3, TimeUnit.SECONDS);
            }
        } else {
            event.reply(":x: You do not have permission to verify the purchase yourself. Please wait.").setEphemeral(true).queue();
        }
    }

    protected boolean isAuthor() {
        return this.ticket.getUserId() == this.member.getIdLong();
    }

    protected void updatePermission(Consumer<PermissionOverride> consumer, Permission... permission) {
        PermissionOverrideAction permissionOverrideAction = textChannel.upsertPermissionOverride(this.member);
        permissionOverrideAction.setAllowed(permission).queue(consumer);
    }

    public void startConfirmClose(Interaction event, Guild guild, Member member, TextChannel textChannel, Ticket ticket) {
        this.event = event;
        this.guild = guild;
        this.member = member;
        this.textChannel = textChannel;
        this.ticket = ticket;

        EmbedBuilder builder = createEmbed();
        setDescription(builder, ":warning: Do you really want to close the ticket ?");
        Button buttonConfirm = new ButtonImpl(BUTTON_CLOSE_CONFIRM, "Yes I want to close the ticket", ButtonStyle.DANGER, false, Emoji.fromUnicode("U+26A0"));

        this.textChannel.sendMessageEmbeds(builder.build()).setActionRow(buttonConfirm).queue(message -> {
            schedule(10000, () -> {
                // The user has not closed the ticket.
                if (this.ticket.getTicketStatus() != TicketStatus.CLOSE) message.delete().queue();
            });
        });
    }

    protected void sendVacationInformation() {
        var now = System.currentTimeMillis();
        var vacation = Config.vacation;
        if (vacation == null) return;

        if (now >= vacation.getStartAt() && now <= vacation.getEndAt()) {
            textChannel.sendMessage(member.getAsMention() + ", **Maxlego08** is currently on **vacation**, the response time will be longer than usual, please wait!").queueAfter(1, TimeUnit.SECONDS);
        }
    }

}
