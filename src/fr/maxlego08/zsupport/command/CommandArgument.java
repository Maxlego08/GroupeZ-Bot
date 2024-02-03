package fr.maxlego08.zsupport.command;

import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;

public record CommandArgument(OptionType optionType, String name, String description, List<CommandChoice> choices) {
}

