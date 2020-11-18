package fr.maxlego08.zsupport.listener;

import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberListener extends ListenerAdapter implements Constant {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {

		if (event.getChannel().getIdLong() == CHANNEL_ROBOT) {

			event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(ROLE_NOT_ROBOT))
					.complete();
			event.getUser().openPrivateChannel().complete().sendMessage("**You are not a robot !**").complete();
			event.getUser().openPrivateChannel().complete().sendMessage("https://youtu.be/fsF7enQY8uI").complete();

		}

	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(ROLE_DEFAULT)).complete();
	}

}
