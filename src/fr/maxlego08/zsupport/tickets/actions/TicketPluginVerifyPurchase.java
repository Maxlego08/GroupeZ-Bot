package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.tickets.TicketStatus;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;

import java.util.concurrent.TimeUnit;

public class TicketPluginVerifyPurchase extends TicketAction {

    @Override
    public void process(Interaction interaction) {

        IMessageEditCallback event = (IMessageEditCallback) interaction;

        EmbedBuilder builder = new EmbedBuilder();
        setEmbedFooter(this.guild, builder);
        setDescription(builder, ":gear: Check your purchase, please wait.");

        event.editMessageEmbeds(builder.build()).setActionRow(createCloseButton()).queueAfter(1, TimeUnit.SECONDS, editMessage -> {

            Plugin plugin = ticket.getPlugin();

            // Vérification du rôle de l'utilisateur si le plugin est premium
            if (!hasRole(this.member, plugin.getRole())) {

                VerifyManager verifyManager = VerifyManager.getInstance();
                verifyManager.hasPurchasePlugin(this.user, plugin, hasPurchase -> {

                    // Si l'utilisateur n'a pas acheté le plugin
                    if (!hasPurchase) {
                        editMessage.deleteOriginal().queue();
                        processNextAction(TicketStatus.PLUGIN_VERIFY_NEED_INFORMATION);
                    } else {
                        Role role = guild.getRoleById(plugin.getRole());
                        if (role != null) guild.addRoleToMember(this.member, role).queue();
                    }
                });
                return;
            }

            editMessage.deleteOriginal().queueAfter(1, TimeUnit.SECONDS, r -> {
                processNextAction(TicketStatus.PLUGIN_INFORMATION);
            });
        });
    }

    @Override
    public void onButton(ButtonInteractionEvent event) {

    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {

    }

    @Override
    public void onModal(ModalInteractionEvent event) {

    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.PLUGIN_VERIFY_PURCHASE;
    }

    @Override
    public void onMessage(MessageReceivedEvent event) {

    }
}
