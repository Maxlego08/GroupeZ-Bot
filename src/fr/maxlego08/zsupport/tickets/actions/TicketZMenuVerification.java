package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.tickets.TicketStatus;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TicketZMenuVerification extends TicketAction {
    @Override
    public void process(Interaction interaction) {

        EmbedBuilder builder = new EmbedBuilder();
        setEmbedFooter(this.guild, builder, new Color(230, 182, 25));
        setDescription(builder, ":gear: Check your purchase, please wait.");

        updatePermission(r -> {
            textChannel.sendMessageEmbeds(builder.build()).setActionRow(createCloseButton()).queueAfter(1, TimeUnit.SECONDS, editMessage -> {

                if (!hasRole(this.member, Config.zMenuPremium) && !hasRole(this.member, Config.zMenuPro)) {

                    VerifyManager verifyManager = VerifyManager.getInstance();

                    verifyManager.verifyMinecraftInventoryUser(this.user, textChannel, mib -> {
                        System.out.println(mib);

                        editMessage.delete().queue();
                        if (mib.hasAccess()) {

                            Role role = guild.getRoleById(mib.power() == PREMIUM_POWER ? Config.zMenuPremium : Config.zMenuPro);
                            if (role != null) guild.addRoleToMember(this.member, role).queue();

                            processNextAction(TicketStatus.PLUGIN_INFORMATION);
                        } else {
                            processNextAction(TicketStatus.VERIFY_ZMENU_CLOSE);
                        }
                    });
                    return;
                }

                editMessage.delete().queueAfter(1, TimeUnit.SECONDS, r2 -> {
                    processNextAction(TicketStatus.PLUGIN_INFORMATION);
                });
            });
        }, Permission.VIEW_CHANNEL);

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
    public void onMessage(MessageReceivedEvent event) {

    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.VERIFY_ZMENU_PURCHASE;
    }
}
