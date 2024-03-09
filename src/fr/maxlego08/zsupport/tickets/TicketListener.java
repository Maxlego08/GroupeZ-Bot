package fr.maxlego08.zsupport.tickets;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.utils.ChannelType;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.attribute.ICategorizableChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Objects;

public class TicketListener extends ListenerAdapter implements Constant {

    private final TicketManager ticketManager;
    private final ErrorManager manager = new ErrorManager();

    public TicketListener(TicketManager ticketManager) {
        this.ticketManager = ticketManager;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        Button button = event.getButton();

        if (event.getChannel().getIdLong() == Config.ticketChannel && !event.getUser().isBot()) {

            LangType langType = Objects.equals(button.getId(), BUTTON_FR) ? LangType.FR : LangType.US;
            TicketStatus ticketStatus = button.getId().equals(BUTTON_ZMENU) ? TicketStatus.VERIFY_ZMENU_PURCHASE : TicketStatus.CHOOSE_TYPE;

            this.ticketManager.createTicket(event.getUser(), event.getGuild(), langType, event, ticketStatus);
        } else if (event.getChannel() instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategoryIdLong() == Config.ticketCategoryId && !event.getUser().isBot()) {

            this.ticketManager.buttonAction(event, event.getGuild());
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getChannel() instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategoryIdLong() == Config.ticketCategoryId && !event.getUser().isBot()) {

            this.ticketManager.selectionAction(event, event.getGuild());
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getChannel() instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategoryIdLong() == Config.ticketCategoryId && !event.getUser().isBot()) {

            this.ticketManager.modalAction(event, event.getGuild());
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        MessageChannel channel = event.getChannel();
        if (event.getAuthor().isBot()) return;

        // General help
        if (channel.getIdLong() == Config.generalChannel) {
            this.manager.processMessage(event, event.getMessage(), event.getAuthor());
        }

        // DONT PING STAFF OMG
        if (event.getMessage().getMentions().getMembers().stream().anyMatch(e -> e.hasPermission(Permission.MESSAGE_MANAGE) && !e.getUser().isBot()) && !event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
            event.getMessage().reply(":rage: Please respect the rules and do not mention the team members.").queue();
        }

        this.ticketManager.processMessageUpload(event);
        this.ticketManager.processZMenuForumMessage(event);

        if (event.getChannel() instanceof ICategorizableChannel iCategorizableChannel && iCategorizableChannel.getParentCategoryIdLong() == Config.ticketCategoryId) {

            this.ticketManager.onMessage(event, event.getGuild());
        } else if (Config.channelsWithInformations.containsKey(channel.getIdLong()) && channel instanceof TextChannel textChannel) {

            ChannelType channelType = Config.channelsWithInformations.get(channel.getIdLong());
            this.ticketManager.sendChannelInformations(event, textChannel, channelType);
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        this.ticketManager.userLeave(event.getGuild(), event.getUser());
    }
}
