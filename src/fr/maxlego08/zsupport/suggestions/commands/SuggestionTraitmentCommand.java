package fr.maxlego08.zsupport.suggestions.commands;

import java.util.Optional;

import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.command.CommandType;
import fr.maxlego08.zsupport.command.VCommand;
import fr.maxlego08.zsupport.suggestions.SuggestionManager;
import fr.maxlego08.zsupport.suggestions.entity.Suggestion;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;

public class SuggestionTraitmentCommand extends VCommand implements Constant {

	private final SuggestionManager suggestionManager = SuggestionManager.getInstance();

	public SuggestionTraitmentCommand(CommandManager commandManager) {
		super(commandManager);
		this.addRequireArg("message id");
		this.addOptionalArg("delete");
		this.permission = Permission.ADMINISTRATOR;
	}

	@Override
	protected CommandType perform(ZSupport main) {

		Optional<Suggestion> optional = this.suggestionManager.getByMessageId(this.argAsString(0));

		if (!optional.isPresent()) {
			sender.sendMessage(fr.maxlego08.zsupport.lang.BasicMessage.SUGGESTION_NOT_FOUND, true);
			return CommandType.EXCEPTION_ERROR;
		}

		Suggestion suggestion = optional.get();

		JDA jda = ZSupport.instance.getJda();
		TextChannel textChannel = jda.getTextChannelById(SUGGEST_CHANNEL);
		MessageHistory history = textChannel.getHistory();
		history.getRetrievedHistory().forEach(e -> {
			System.out.println(e);
		});
		Message message = history.getMessageById(suggestion.getMessageId());

		System.out.println(message);

		if (message == null) {
			sender.sendMessage(fr.maxlego08.zsupport.lang.BasicMessage.SUGGESTION_NOT_FOUND2, true);
			return CommandType.EXCEPTION_ERROR;
		}

		message.delete().queue(unused -> {

			String arg = argAsString(1, null);

			if (arg != null && arg.equalsIgnoreCase("delete")) {
				SuggestionManager.getSuggestions().remove(suggestion);
				ZSupport.instance.save();
				return;
			}

			TextChannel textChannelUntreadted = ZSupport.instance.getJda()
					.getTextChannelById(SUGGEST_UNTREATED_CHANNEL);

			if (textChannelUntreadted == null) {
				sender.sendMessage("Je n'arrive pas à trouver le channel prévu à cet effet.");
				return;
			}

			textChannelUntreadted.sendMessageEmbeds(new EmbedBuilder().setAuthor("Suggestion entrain d'être réalisée.")
					.setDescription(suggestion.getSuggestion()).setFooter(suggestion.getUuid().toString()).build())
					.queue(suggestionMessage -> {
						ActionRow of = ActionRow.of();

						this.suggestionManager.getChoices().forEach(choice -> of.getButtons()
								.add(Button.of(choice.getButtonStyle(), choice.getId(), choice.getEmoji())));
						suggestionMessage.getActionRows().add(of);
					});
		});

		return CommandType.SUCCESS;
	}
}
