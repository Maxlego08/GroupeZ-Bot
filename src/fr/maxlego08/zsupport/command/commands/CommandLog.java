package fr.maxlego08.zsupport.command.commands;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;

public class CommandLog extends VCommand {

    public CommandLog(CommandManager commandManager) {
        super(commandManager);
        this.consoleCanUse = false;
        this.onlyInCommandChannel = false;
        this.description = "Send log informations";
    }

    @Override
    protected CommandType perform(ZSupport main) {

        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Don't send your logs files here !");
        setEmbedFooter(this.guild, builder, new Color(200, 10, 10));
        builder.setDescription("Paste your logs on https://mclo.gs/");
        event.replyEmbeds(builder.build()).queue();

        return CommandType.SUCCESS;
    }

}
