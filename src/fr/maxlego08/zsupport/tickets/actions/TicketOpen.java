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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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

            String extension = attachment.getFileExtension();
            String url = attachment.getProxyUrl();
            System.out.println(attachment.getFileName() + " - " + extension + " - " + attachment.getContentType() + " -" + url);

            SqlManager.service.execute(() -> {


                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .build();

                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    String content = response.body();

                    MclogsClient mclogsClient = ZSupport.instance.getMclogsClient();
                    mclogsClient.uploadLog(content).thenAccept(uploadLogResponse -> {
                        System.out.println(uploadLogResponse.isSuccess());
                        System.out.println(uploadLogResponse);
                        System.out.println(uploadLogResponse.getUrl());
                        System.out.println(uploadLogResponse.getRawUrl());
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }


            });
        });
    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.OPEN;
    }
}
