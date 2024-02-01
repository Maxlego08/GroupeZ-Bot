package fr.maxlego08.zsupport.command.commands.tickets;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;

public class CommandTicketSet extends VCommand {

    public CommandTicketSet(CommandManager commandManager) {
        super(commandManager);
        this.consoleCanUse = false;
        this.permission = Permission.ADMINISTRATOR;
        this.description = "Afficher le message des tickets";
    }

    @Override
    protected CommandType perform(ZSupport main) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Create a ticket");
        setEmbedFooter(this.guild, builder);

        setDescription(builder,
                "",
                "**Informations**:",
                ":flag_fr: Clique pour créer un ticket",
                ":warning: Veuillez vérifier que votre compte discord est bien relié sur le site.",
                "",
                ":flag_us: Click for create a ticket",
                ":warning: Please check that your discord account is linked to the site.",
                "",
                ":white_check_mark: Link your discord account to the site https://groupez.dev/dashboard/account",
                "",
                "**Note**",
                "**1.** Please provide as much information as possible about your problem.",
                "**2.** Please do not mention the staff, wait for someone to answer you.",
                "**3.** The GroupeZ team is located in the timezone Paris (UTC+1)"
        );

        builder.setImage("https://img.groupez.dev/groupez/link-discord.gif");

        Button buttonFr = new ButtonImpl(BUTTON_FR, "Créer un ticket en Français", ButtonStyle.PRIMARY, false, Emoji.fromUnicode("U+1F1EB U+1F1F7"));
        Button buttonEn = new ButtonImpl(BUTTON_EN, "Create a ticket in English", ButtonStyle.SUCCESS, false, Emoji.fromUnicode("U+1F1FA U+1F1F8"));

        this.textChannel.sendMessageEmbeds(builder.build()).setActionRow(buttonFr, buttonEn).queue(message -> {
            this.event.deferReply(true).setContent("Envoie de la commande effectué avec succès.").queue();
        });

        return CommandType.SUCCESS;
    }

}
