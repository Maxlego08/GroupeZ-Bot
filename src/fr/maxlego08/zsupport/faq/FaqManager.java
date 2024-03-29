package fr.maxlego08.zsupport.faq;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandChoice;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.command.commands.faq.CommandFaq;
import fr.maxlego08.zsupport.command.commands.faq.CommandFaqDelete;
import fr.maxlego08.zsupport.command.commands.faq.CommandFaqRegister;
import fr.maxlego08.zsupport.tickets.storage.SqlManager;
import fr.maxlego08.zsupport.utils.ZUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages FAQs for the support system, including retrieval, creation, and deletion of FAQs.
 */
public class FaqManager extends ZUtils {

    /**
     * The SQL manager to interact with the database.
     */
    private final SqlManager sqlManager;

    /**
     * A list of FAQs currently loaded.
     */
    private List<Faq> faqs = new ArrayList<>();

    private VCommand faqCommand;

    /**
     * Constructs a new FAQ Manager.
     *
     * @param sqlManager The SQL manager to use for database interactions.
     */
    public FaqManager(SqlManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    /**
     * Retrieves an FAQ by its name.
     *
     * @param name The name of the FAQ to retrieve.
     * @return An optional containing the FAQ if found, or empty otherwise.
     */
    private Optional<Faq> get(String name) {
        return this.faqs.stream().filter(faq -> faq.getName().equals(name)).findFirst();
    }

    /**
     * Generates a list of command choices for FAQs.
     *
     * @return A list of command choices for use in commands.
     */
    public List<CommandChoice> getChoices() {
        return this.faqs.stream().map(faq -> new CommandChoice(faq.getTitle(), faq.getName())).collect(Collectors.toList());
    }

    /**
     * Loads FAQs from the database and registers FAQ-related commands.
     */
    public void selectFqa() {
        SqlManager.service.execute(() -> {
            this.faqs = this.sqlManager.getAllFaqs();
            registerCommands();
        });
    }

    private void registerCommands() {
        CommandManager commandManager = ZSupport.instance.getCommandManager();
        commandManager.registerCommand("faq", this.faqCommand = new CommandFaq(commandManager, this));
        commandManager.registerCommand("fcreate", new CommandFaqRegister(commandManager, this));
        commandManager.registerCommand("fdelete", new CommandFaqDelete(commandManager, this));
    }

    /**
     * Sends an FAQ in response to a slash command interaction.
     *
     * @param event The event of the slash command interaction.
     * @param name  The name of the FAQ to send.
     */
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
        String answer = faq.getAnswer();
        answer = answer.replace("%ticket%", event.getGuild().getTextChannelById(Config.ticketChannel).getAsMention());
        builder.setDescription(answer);

        event.replyEmbeds(builder.build()).queue();
    }

    /**
     * Creates a new FAQ and adds it to the database.
     *
     * @param event       The event of the slash command interaction.
     * @param name        The name of the new FAQ.
     * @param title       The title of the new FAQ.
     * @param description The description (answer) of the new FAQ.
     */
    public void createFaq(SlashCommandInteractionEvent event, String name, String title, String description) {

        if (get(name).isPresent()) {
            event.reply(":x: FAQ " + name + " already exist.").setEphemeral(true).queue();
            return;
        }

        Faq faq = new Faq(name, title, description);
        this.faqs.add(faq);
        this.sqlManager.addFaq(faq);

        updateFaq(Objects.requireNonNull(event.getGuild()));

        event.reply(":white_check_mark: You just create the FAQ:" + name).setEphemeral(true).queue();
    }

    private void updateFaq(Guild guild) {
        CommandManager commandManager = ZSupport.instance.getCommandManager();

        commandManager.registerCommands();
        registerCommands();

        List<CommandData> commands = new ArrayList<>();
        commandManager.getCommands().forEach(command -> {
            if (!command.isPlayerCanUse()) return;
            commands.add(command.toCommandData());
        });

        guild.updateCommands().addCommands(commands).queue();
    }

    /**
     * Deletes an FAQ by its name.
     *
     * @param event The event of the slash command interaction.
     * @param name  The name of the FAQ to delete.
     */
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
