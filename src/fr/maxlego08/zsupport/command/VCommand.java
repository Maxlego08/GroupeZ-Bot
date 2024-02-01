package fr.maxlego08.zsupport.command;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.commands.Arguments;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import fr.maxlego08.zsupport.utils.commands.Sender;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.List;

public abstract class VCommand extends Arguments implements Constant {

    private final CommandManager commandManager;
    /**
     * Permission used for the command, if it is a null then everyone can
     * execute the command
     */
    protected Permission permission;
    protected VCommand parent;
    protected List<String> subCommands = new ArrayList<>();
    protected List<VCommand> subVCommands = new ArrayList<>();
    protected List<CommandArgument> requireArgs = new ArrayList<>();
    protected List<CommandArgument> optionalArgs = new ArrayList<>();
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
    protected ZSupport instance;
    protected SlashCommandInteractionEvent event;
    protected MessageChannelUnion textChannel;
    protected Guild guild;

    public VCommand(CommandManager commandManager) {
        super();
        this.commandManager = commandManager;
    }

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
     * @param main
     * @param commandSender
     * @param args
     * @return {@link CommandType}
     */
    public CommandType prePerform(ZSupport main, Sender commandSender, String[] args, SlashCommandInteractionEvent event) {

        // We update the number of arguments according to the number of parents

        this.parentCount = parentCount(0);
        this.argsMaxLength = this.requireArgs.size() + this.optionalArgs.size() + this.parentCount;
        this.argsMinLength = this.requireArgs.size() + this.parentCount;

        // We generate the basic syntax if it is impossible to find it
        if (this.syntaxe == null) {
            this.syntaxe = "";
        }

        this.args = args;

        String defaultString = argAsString(0);

        if (defaultString != null) {
            for (VCommand subCommand : subVCommands) {
                if (subCommand.getSubCommands().contains(defaultString.toLowerCase()))
                    return CommandType.CONTINUE;
            }
        }

		/*if (this.argsMinLength != 0 && this.argsMaxLength != 0
				&& !(args.length >= this.argsMinLength && args.length <= this.argsMaxLength)) {
			return CommandType.SYNTAX_ERROR;
		}*/

        this.sender = commandSender;
        if (this.sender instanceof PlayerSender) {
            this.player = (PlayerSender) commandSender;
        }

        this.event = event;
        if (event != null) {
            this.textChannel = event.getChannel();
            this.guild = event.getGuild();
        }

        try {
            return perform(main);
        } catch (Exception e) {
            if (this.DEBUG) {
                System.out.println(PREFIX_CONSOLE + "Commands: " + this);
                System.out.println(PREFIX_CONSOLE + "Error:");
                e.printStackTrace();
            }
            return CommandType.SYNTAX_ERROR;
        }
    }

    protected TextChannel getChannel(long id) {
        return this.guild.getTextChannelById(id);
    }

    /**
     * method that allows you to execute the command
     */
    protected abstract CommandType perform(ZSupport main);

    public boolean isPlayerCanUse() {
        return this.playerCanUse;
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
    public List<CommandArgument> getRequireArgs() {
        return requireArgs;
    }

    /**
     * @return the optionalArgs
     */
    public List<CommandArgument> getOptionalArgs() {
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
        return syntaxe;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description == null ? "No description" : description;
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
    protected void addRequireArg(OptionType optionType, String name, String description) {
        this.addRequireArg(optionType, name, description, new ArrayList<>());
    }

    protected void addRequireArg(OptionType optionType, String name, String description, List<CommandChoice> choices) {
        this.requireArgs.add(new CommandArgument(optionType, name, description, choices));
        this.ignoreParent = parent == null;
        this.ignoreArgs = true;
    }

    protected void addOptionalArg(CommandArgument commandArgument) {
        this.optionalArgs.add(commandArgument);
        this.ignoreParent = parent == null;
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
     * @param command
     * @return this
     */
    public VCommand addSubCommand(VCommand command) {
        command.parent = this;
        commandManager.addCommand(command);
        this.subVCommands.add(command);
        return this;
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
