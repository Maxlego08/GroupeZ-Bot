package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.tickets.TicketStatus;
import fr.maxlego08.zsupport.tickets.storage.SqlManager;
import gs.mclo.api.MclogsClient;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.requests.restaction.PermissionOverrideAction;

public class TicketOpen extends TicketAction {

    @Override
    public void process(Interaction interaction) {
        PermissionOverrideAction permissionOverrideAction = textChannel.upsertPermissionOverride(this.member);
        permissionOverrideAction.setAllowed(Permission.MESSAGE_SEND, Permission.VIEW_CHANNEL).queue();
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

        Message message = event.getMessage();
        message.getAttachments().forEach(attachment -> {

            if (attachment.isImage() || attachment.isVideo()) return;

            String fileExtension = attachment.getFileExtension();
            if (fileExtension != null && (fileExtension.equals("yml") || fileExtension.equals("log") || fileExtension.equals("txt"))) {
                SqlManager.service.execute(() -> {
                    String content = readContentFromURL(attachment.getProxy().getUrl());
                    try {
                        MclogsClient mclogsClient = ZSupport.instance.getMclogsClient();
                        mclogsClient.uploadLog(content).thenAccept(uploadLogResponse -> {
                            if (uploadLogResponse.isSuccess()) {
                                event.getMessage().reply(":open_file_folder: " + attachment.getFileName() + ": https://mclo.gs/" + uploadLogResponse.getId()).queue(success -> {
                                    success.suppressEmbeds(true).queue();
                                });
                            }
                        });
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.OPEN;
    }
}
