package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.TicketStatus;
import fr.maxlego08.zsupport.utils.Plugin;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TicketVerificationPlugin extends TicketAction {
    @Override
    public void process(Interaction interaction) {

        StringSelectMenu.Builder selectionMenu = StringSelectMenu.create(BUTTON_SELECT_PLUGIN);
        Config.plugins.stream().filter(plugin -> plugin.getPluginId() != 253).forEach(plugin -> {

            Emoji emote = guild.getEmojiById(plugin.getEmoteId());
            selectionMenu.addOption(plugin.getName(), plugin.getName(), emote);

        });

        EmbedBuilder builder = this.createEmbed();

        StringBuilder stringBuilder = this.createDescription();
        stringBuilder.append(getMessage(this.ticket.getLangType(), Message.TICKET_OTHER_INFO));

        builder.setDescription(stringBuilder.toString());

        ((IMessageEditCallback) this.event).editMessageEmbeds(builder.build()).setActionRow(selectionMenu.build()).queue();
    }

    @Override
    public void onButton(ButtonInteractionEvent event) {

    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {

        List<String> strings = event.getValues();

        if (strings.size() == 1) {

            String pluginName = strings.get(0);
            Plugin plugin = Config.plugins.stream().filter(configPlugin -> configPlugin.getName().equals(pluginName)).findAny().orElse(Plugin.EMPTY);

            this.ticket.setPluginId(plugin.getPluginId());
            processNextAction(TicketStatus.VERIFY_PURCHASE);

        } else {
            event.reply(getMessage(this.ticket.getLangType(), Message.TICKET_PLUGIN_ERROR)).queue();
        }

    }

    @Override
    public void onModal(ModalInteractionEvent event) {

    }

    @Override
    public void onMessage(MessageReceivedEvent event) {

    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.VERIFY_PURCHASE_PLUGIN;
    }
}
