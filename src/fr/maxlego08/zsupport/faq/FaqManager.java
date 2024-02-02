package fr.maxlego08.zsupport.faq;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandChoice;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.commands.faq.CommandFaq;
import fr.maxlego08.zsupport.command.commands.faq.CommandFaqDelete;
import fr.maxlego08.zsupport.command.commands.faq.CommandFaqRegister;
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

    private Optional<Faq> get(String name) {
        return this.faqs.stream().filter(faq -> faq.getName().equals(name)).findFirst();
    }

    public List<CommandChoice> getChoices() {
        return this.faqs.stream().map(faq -> new CommandChoice(faq.getTitle(), faq.getName())).collect(Collectors.toList());
    }

    public void selectFqa() {
        SqlManager.service.execute(() -> {
            this.faqs = this.sqlManager.getAllFaqs();
            CommandManager commandManager = ZSupport.instance.getCommandManager();
            commandManager.registetCommand("faq", new CommandFaq(commandManager, this));
            commandManager.registetCommand("faq-create", new CommandFaqRegister(commandManager, this));
            commandManager.registetCommand("faq-delete", new CommandFaqDelete(commandManager, this));
        });
    }

    public void sendFaq(SlashCommandInteractionEvent event, String name) {
        Optional<Faq> optional = get(name);
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

    public void createFaq(SlashCommandInteractionEvent event, String name, String title, String description) {

        if (get(name).isPresent()) {
            event.reply(":x: FAQ " + name + " already exist.").setEphemeral(true).queue();
            return;
        }

        Faq faq = new Faq(name, title, description);
        this.faqs.add(faq);
        this.sqlManager.addFaq(faq);

        event.reply(":white_check_mark: You just create the FAQ:" + name).setEphemeral(true).queue();
    }

    public void deleteFaq(SlashCommandInteractionEvent event, String name) {

        Optional<Faq> optional = get(name);
        if (optional.isEmpty()) {
            event.reply(":x: Cannot find FAQ with name " + name).setEphemeral(true).queue();
            return;
        }

        Faq faq = optional.get();
        this.faqs.remove(faq);
        this.sqlManager.deleteFaqById(faq.getId());
        event.reply(":white_check_mark: You just delete the FAQ:" + name).setEphemeral(true).queue();
    }
}
