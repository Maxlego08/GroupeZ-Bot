package fr.maxlego08.zsupport.command;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.commands.CommandClear;
import fr.maxlego08.zsupport.command.commands.CommandDocumentation;
import fr.maxlego08.zsupport.command.commands.CommandPlugins;
import fr.maxlego08.zsupport.command.commands.CommandPurchase;
import fr.maxlego08.zsupport.command.commands.CommandRoles;
import fr.maxlego08.zsupport.command.commands.CommandServer;
import fr.maxlego08.zsupport.command.commands.CommandStop;
import fr.maxlego08.zsupport.command.commands.CommandVerify;
import fr.maxlego08.zsupport.command.commands.tickets.CommandTicketSet;
import fr.maxlego08.zsupport.command.commands.tickets.CommandTicketVocal;
import fr.maxlego08.zsupport.lang.BasicMessage;
import fr.maxlego08.zsupport.suggestions.commands.SuggestionTraitmentCommand;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.utils.commands.ConsoleSender;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.utils.commands.Sender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class CommandManager extends ZUtils implements Constant {

	private final ZSupport support;
	private final List<VCommand> commands = new ArrayList<VCommand>();

	public CommandManager(ZSupport support) {
		super();
		this.support = support;

		registetCommand("stop", new CommandStop(this), "end");
		registetCommand("ticketset", new CommandTicketSet(this));
		registetCommand("vocal", new CommandTicketVocal(this));
		registetCommand("server", new CommandServer(this));
		registetCommand("roles", new CommandRoles(this));
		registetCommand("documentation", new CommandDocumentation(this), "d", "doc", "docs");
		registetCommand("clear", new CommandClear(this));
		registetCommand("verify", new CommandVerify(this));
		registetCommand("howtopurchase", new CommandPurchase(this));
		registetCommand("plugins", new CommandPlugins(this));
		registetCommand("suggestion-traitment", new SuggestionTraitmentCommand(this), "st");
	}

	public VCommand addCommand(VCommand command) {
		this.commands.add(command);
		return command;
	}

	public VCommand registetCommand(String cmd, VCommand command, String... strings) {
		command.subCommands.add(cmd);
		for (String s : strings) {
			command.subCommands.add(s);
		}
		this.commands.add(command);
		return command;
	}

	/**
	 * 
	 * @param sender
	 * @param cmd
	 * @param args
	 * @param event
	 * @return
	 */
	public boolean onCommand(Sender sender, String cmd, String[] args, MessageReceivedEvent event) {
		for (VCommand command : this.commands) {
			if (command.getSubCommands().contains(cmd.toLowerCase())) {
				if ((args.length == 0 || command.isIgnoreParent()) && command.getParent() == null) {
					CommandType type = processRequirements(command, sender, args, event);
					if (!type.equals(CommandType.CONTINUE)) {
						return true;
					}
				}
			} else if (args.length >= 1 && command.getParent() != null
					&& canExecute(args, cmd.toLowerCase(), command)) {
				CommandType type = processRequirements(command, sender, args, event);
				if (!type.equals(CommandType.CONTINUE)) {
					return true;
				}
			}
		}
		sender.sendEmbed(BasicMessage.COMMAND_NOT_FOUND, true);
		return true;
	}

	/**
	 * @param args
	 * @param cmd
	 * @param command
	 * @return true if can execute
	 */
	private boolean canExecute(String[] args, String cmd, VCommand command) {
		for (int index = args.length - 1; index > -1; index--) {
			if (command.getSubCommands().contains(args[index].toLowerCase())) {
				if (command.isIgnoreArgs()
						&& (command.getParent() != null ? canExecute(args, cmd, command.getParent(), index - 1) : true))
					return true;
				if (index < args.length - 1)
					return false;
				return canExecute(args, cmd, command.getParent(), index - 1);
			}
		}
		return false;
	}

	/**
	 * @param args
	 * @param cmd
	 * @param command
	 * @param index
	 * @return
	 */
	private boolean canExecute(String[] args, String cmd, VCommand command, int index) {
		if (index < 0 && command.getSubCommands().contains(cmd.toLowerCase()))
			return true;
		else if (index < 0)
			return false;
		else if (command.getSubCommands().contains(args[index].toLowerCase()))
			return canExecute(args, cmd, command.getParent(), index - 1);
		else
			return false;
	}

	/**
	 * Allows you to run the command
	 * 
	 * @param command
	 * @param sender
	 * @param strings
	 * @return
	 */
	private CommandType processRequirements(VCommand command, Sender sender, String[] strings,
			MessageReceivedEvent event) {

		if (!(sender instanceof PlayerSender) && !command.isConsoleCanUse()) {
			sender.sendMessage(BasicMessage.COMMAND_NO_CONSOLE);
			return CommandType.DEFAULT;
		}
		if (!(sender instanceof ConsoleSender) && !command.isPlayerCanUse()) {
			sender.sendEmbed(BasicMessage.COMMAND_NO_PLAYER, true);
			return CommandType.DEFAULT;
		}
		if (sender instanceof PlayerSender && command.isOnlyInCommandChannel()) {

			TextChannel channel = event.getTextChannel();

			if (channel.getIdLong() != Config.commandChannel) {
				EmbedBuilder builder = new EmbedBuilder();
				builder.setColor(Color.RED);

				TextChannel commandChannel = channel.getJDA().getTextChannelById(Config.commandChannel);
				String message = ":x: You must use the " + commandChannel.getAsMention() + " to send a command.";
				builder.setDescription(message);

				event.getMessage().replyEmbeds(builder.build()).queue(msg -> {
					msg.delete().queueAfter(10, TimeUnit.SECONDS);
				});
				return CommandType.DEFAULT;
			}

		}

		if (command.getPermission() == null || sender.hasPermission(command.getPermission())) {
			CommandType returnType = command.prePerform(support, sender, strings, event);

			if (returnType == CommandType.SYNTAX_ERROR) {
				sender.sendMessage(BasicMessage.COMMAND_SYNTAXE_ERROR, true, command.getSyntaxe());
			}

			return returnType;
		}
		sender.sendEmbed(BasicMessage.COMMAND_NO_PERMISSION, true);
		return CommandType.DEFAULT;
	}

}
