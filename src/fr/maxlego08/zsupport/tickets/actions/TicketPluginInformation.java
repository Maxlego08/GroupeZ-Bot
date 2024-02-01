package fr.maxlego08.zsupport.tickets.actions;

import fr.maxlego08.zsupport.tickets.TicketStatus;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class TicketPluginInformation extends TicketAction {
    @Override
    public void process(Interaction interaction) {

        TextInput pluginVersion = TextInput.create("pluginVersion", "Version du Plugin", TextInputStyle.SHORT)
                .setPlaceholder("Entrez la version de votre plugin ici")
                .setMinLength(1)
                .setMaxLength(50) // Ajustez en fonction de la longueur attendue
                .build();

        TextInput serverVersion = TextInput.create("serverVersion", "Version du Serveur", TextInputStyle.SHORT)
                .setPlaceholder("Entrez la version de votre serveur ici")
                .setMinLength(1)
                .setMaxLength(50) // Ajustez en fonction de la longueur attendue
                .build();

        TextInput problemDescription = TextInput.create("problemDescription", "Description du Problème", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Décrivez le problème que vous rencontrez")
                .setMinLength(10)
                .setMaxLength(1000) // Ajustez en fonction de la longueur attendue
                .build();

        Modal modal = Modal.create("supportModal", "Support Technique")
                .addComponents(ActionRow.of(pluginVersion), ActionRow.of(serverVersion), ActionRow.of(problemDescription))
                .build();

        replyModal(modal);
    }

    @Override
    public void onButton(ButtonInteractionEvent event) {

    }

    @Override
    public void onSelect(StringSelectInteractionEvent event) {

    }

    @Override
    public TicketStatus getTicketStatus() {
        return TicketStatus.PLUGIN_INFORMATION;
    }
}
