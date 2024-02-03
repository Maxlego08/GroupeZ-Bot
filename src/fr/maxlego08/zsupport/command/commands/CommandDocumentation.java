package fr.maxlego08.zsupport.command.commands;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;

public class CommandDocumentation extends VCommand {

    public CommandDocumentation(CommandManager commandManager) {
        super(commandManager);
        this.consoleCanUse = false;
        this.onlyInCommandChannel = false;
        this.description = "Display plugin documentation";
    }

    @Override
    protected CommandType perform(ZSupport main) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Documentations");
        setEmbedFooter(this.guild, builder);

        List<String> description = new ArrayList<>();
        Config.documentations.forEach((plugin, link) -> description.add(plugin + ": " + link));
        description.sort(String::compareToIgnoreCase);

        setDescription(builder, description);
        event.replyEmbeds(builder.build()).queue();

        return CommandType.SUCCESS;
    }

}
