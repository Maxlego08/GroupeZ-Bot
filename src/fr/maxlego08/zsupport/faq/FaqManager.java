package fr.maxlego08.zsupport.faq;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandChoice;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.commands.faq.CommandFaq;
import fr.maxlego08.zsupport.tickets.storage.SqlManager;
import fr.maxlego08.zsupport.utils.ZUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class FaqManager extends ZUtils {

    private final SqlManager sqlManager;
    private List<Faq> faqs = new ArrayList<>();

    public FaqManager(SqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public List<Faq> getFaqs() {
        return faqs;
    }

    public List<CommandChoice> getChoices() {
        return this.faqs.stream().map(faq -> new CommandChoice(faq.getTitle(), faq.getName())).collect(Collectors.toList());
    }

    public void selectFqa() {
        SqlManager.service.execute(() -> {
            this.faqs = this.sqlManager.getAllFaqs();
            CommandManager commandManager = ZSupport.instance.getCommandManager();
            commandManager.registetCommand("faq", new CommandFaq(commandManager, this));
        });
    }

    public void sendFaq(SlashCommandInteractionEvent event, String name) {
        Optional<Faq> optional = this.faqs.stream().filter(faq -> faq.getName().equals(name)).findFirst();
        if (optional.isEmpty()) {
            event.reply(":x: Cannot find FAQ with name " + name).setEphemeral(true).queue();
            return;
        }

        Faq faq = optional.get();
        EmbedBuilder builder = new EmbedBuilder();
        setEmbedFooter(Objects.requireNonNull(event.getGuild()), builder, new Color(18, 230, 69));
        builder.setTitle(faq.getTitle());
        builder.setDescription(faq.getAnswer());

        event.replyEmbeds(builder.build()).queue();
    }

}
