package fr.maxlego08.zsupport.command;

import java.util.ArrayList;
import java.util.List;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.commands.Arguments;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.utils.commands.Sender;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class VCommand extends Arguments implements Constant {

	public VCommand(CommandManager commandManager) {
		super();
		this.commandManager = commandManager;
	}

	/**
	 * Permission used for the command, if it is a null then everyone can
	 * execute the command
	 */
	protected Permission permission;
	protected VCommand parent;
	protected List<String> subCommands = new ArrayList<String>();
	protected List<VCommand> subVCommands = new ArrayList<VCommand>();
	protected List<String> requireArgs = new ArrayList<String>();
	protected List<String> optionalArgs = new ArrayList<String>();
	protected boolean consoleCanUse = true;
	protected boolean playerCanUse = true;
	protected boolean ignoreParent = false;
	protected boolean ignoreArgs = false;
	protected boolean onlyInCommandChannel = false;
	protected boolean DEBUG = true;
	protected Sender sender;
	protected PlayerSender player;
	protected String syntaxe;
	protected String description;
	protected int argsMinLength;
	protected int argsMaxLength;
	private CommandManager commandManager;
	protected ZSupport instance;

	protected MessageReceivedEvent event;
	protected TextChannel textChannel;
	protected Guild guild;

	public boolean isOnlyInCommandChannel() {
		return onlyInCommandChannel;
	}
	
	/**
	 * Permet de savoir le nombre de parent de façon récursive
	 * 
	 * @param defaultParent
	 * @return
	 */
	private int parentCount(int defaultParent) {
		return parent == null ? defaultParent : parent.parentCount(defaultParent + 1);
	}

	/**
	 * 
	 * @param main
	 * @param commandSender
	 * @param args
	 * @return {@link CommandType}
	 */
	public CommandType prePerform(ZSupport main, Sender commandSender, String[] args, MessageReceivedEvent event) {

		// On met à jour le nombre d'argument en fonction du nombre de parent

		parentCount = parentCount(0);
		argsMaxLength = requireArgs.size() + optionalArgs.size() + parentCount;
		argsMinLength = requireArgs.size() + parentCount;

		// On génère le syntaxe de base s'il y est impossible de la trouver
		if (syntaxe == null)
			syntaxe = generateDefaultSyntaxe("");

		this.args = args;

		String defaultString = argAsString(0);

		if (defaultString != null) {
			for (VCommand subCommand : subVCommands) {
				if (subCommand.getSubCommands().contains(defaultString.toLowerCase()))
					return CommandType.CONTINUE;
			}
		}

		if (argsMinLength != 0 && argsMaxLength != 0
				&& !(args.length >= argsMinLength && args.length <= argsMaxLength)) {
			return CommandType.SYNTAX_ERROR;
		}

		this.sender = commandSender;
		if (sender instanceof PlayerSender)
			player = (PlayerSender) commandSender;

		this.event = event;
		if (event != null) {
			this.textChannel = event.getTextChannel();
			this.guild = event.getGuild();
		}

		try {
			return perform(main);
		} catch (Exception e) {
			if (DEBUG) {
				System.out.println(PREFIX_CONSOLE + "Commands: " + toString());
				System.out.println(PREFIX_CONSOLE + "Error:");
				e.printStackTrace();
			}
			return CommandType.SYNTAX_ERROR;
		}
	}

	protected TextChannel getChannel(long id) {
		return guild.getTextChannelById(id);
	}

	/**
	 * method that allows you to execute the command
	 */
	protected abstract CommandType perform(ZSupport main);

	public boolean isPlayerCanUse() {
		return playerCanUse;
	}

	/**
	 * @return the permission
	 */
	public Permission getPermission() {
		return permission;
	}

	/**
	 * @return the parent
	 */
	public VCommand getParent() {
		return parent;
	}

	/**
	 * @return the subCommands
	 */
	public List<String> getSubCommands() {
		return subCommands;
	}

	/**
	 * @return the subVCommands
	 */
	public List<VCommand> getSubVCommands() {
		return subVCommands;
	}

	/**
	 * @return the requireArgs
	 */
	public List<String> getRequireArgs() {
		return requireArgs;
	}

	/**
	 * @return the optionalArgs
	 */
	public List<String> getOptionalArgs() {
		return optionalArgs;
	}

	/**
	 * @return the consoleCanUse
	 */
	public boolean isConsoleCanUse() {
		return consoleCanUse;
	}

	/**
	 * @return the ignoreParent
	 */
	public boolean isIgnoreParent() {
		return ignoreParent;
	}

	/**
	 * @return the ignoreArgs
	 */
	public boolean isIgnoreArgs() {
		return ignoreArgs;
	}

	/**
	 * @return the dEBUG
	 */
	public boolean isDEBUG() {
		return DEBUG;
	}

	/**
	 * @return the sender
	 */
	public Sender getSender() {
		return sender;
	}

	/**
	 * @return the player
	 */
	public PlayerSender getPlayer() {
		return player;
	}

	/**
	 * @return the syntaxe
	 */
	public String getSyntaxe() {
		if (syntaxe == null) {
			syntaxe = generateDefaultSyntaxe("");
		}
		return syntaxe;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the argsMinLength
	 */
	public int getArgsMinLength() {
		return argsMinLength;
	}

	/**
	 * @return the argsMaxLength
	 */
	public int getArgsMaxLength() {
		return argsMaxLength;
	}

	/**
	 * @return the instance
	 */
	public ZSupport getInstance() {
		return instance;
	}

	/*
	 * Ajouter un argument obligatoire
	 */
	protected void addRequireArg(String message) {
		this.requireArgs.add(message);
		this.ignoreParent = parent == null ? true : false;
		this.ignoreArgs = true;
	}

	/**
	 * Ajouter un argument optionel
	 * 
	 * @param message
	 */
	protected void addOptionalArg(String message) {
		this.optionalArgs.add(message);
		this.ignoreParent = parent == null ? true : false;
		this.ignoreArgs = true;
	}

	/**
	 * Adds sub orders
	 * 
	 * @param subCommand
	 * @return this
	 */
	public VCommand addSubCommand(String subCommand) {
		this.subCommands.add(subCommand);
		return this;
	}

	/**
	 * Adds sub orders
	 * 
	 * @param subCommand
	 * @return this
	 */
	public VCommand addSubCommand(VCommand command) {
		command.parent = this;
		commandManager.addCommand(command);
		this.subVCommands.add(command);
		return this;
	}

	/**
	 * Permet de générer la syntaxe de la commande manuellement Mais vous pouvez
	 * la mettre vous même avec le setSyntaxe()
	 * 
	 * @param syntaxe
	 * @return generate syntaxe
	 */
	private String generateDefaultSyntaxe(String syntaxe) {

		String tmpString = subCommands.get(0);

		if (requireArgs.size() != 0 && syntaxe.equals(""))
			for (String requireArg : requireArgs) {
				requireArg = "<" + requireArg + ">";
				syntaxe += " " + requireArg;
			}
		if (optionalArgs.size() != 0 && syntaxe.equals(""))
			for (String optionalArg : optionalArgs) {
				optionalArg = "[<" + optionalArg + ">]";
				syntaxe += " " + optionalArg;
			}

		tmpString += syntaxe;

		if (parent == null)
			return "/" + tmpString;

		return parent.generateDefaultSyntaxe(" " + tmpString);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VCommand [permission=" + permission + ", parent=" + parent + ", subCommands=" + subCommands
				+ ", requireArgs=" + requireArgs + ", optionalArgs=" + optionalArgs + ", consoleCanUse=" + consoleCanUse
				+ ", ignoreParent=" + ignoreParent + ", ignoreArgs=" + ignoreArgs + ", syntaxe=" + syntaxe
				+ ", description=" + description + ", argsMinLength=" + argsMinLength + ", argsMaxLength="
				+ argsMaxLength + "]";
	}

}
