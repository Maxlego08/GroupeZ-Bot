package fr.maxlego08.zsupport.command.commands;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandChoice;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.verify.VerifyManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

public class CommandCustomerVerify extends VCommand {

    public CommandCustomerVerify(CommandManager commandManager) {
        super(commandManager);
        this.consoleCanUse = false;
        this.onlyInCommandChannel = false;
        this.description = "Allows you to check your purchases on groupez.dev";
        this.permission = Permission.MESSAGE_MANAGE;
        this.addRequireArg(OptionType.USER, "utilisateur", "Player who will receive the role");
        List<CommandChoice> commandChoices = Config.plugins.stream().filter(Plugin::isPremium).map(plugin -> new CommandChoice(plugin.getName(), String.valueOf(plugin.getPluginId()))).toList();
        this.addRequireArg(OptionType.STRING, "plugin", "Plugin name", commandChoices);
    }

    @Override
    protected CommandType perform(ZSupport main) {

        Member target = this.event.getOption("utilisateur").getAsMember();
        int pluginId = Integer.parseInt(this.event.getOption("plugin").getAsString());

        User user = this.event.getUser();

        VerifyManager manager = VerifyManager.getInstance();
        manager.updateUserAsync(user, this.player, this.event, target, pluginId);

        return CommandType.SUCCESS;
    }

}
