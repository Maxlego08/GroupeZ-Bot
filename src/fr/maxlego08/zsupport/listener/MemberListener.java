package fr.maxlego08.zsupport.listener;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.maxlego08.zsupport.role.RoleManager;
import fr.maxlego08.zsupport.utils.Constant;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberListener extends ListenerAdapter implements Constant {

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {

		Guild guild = event.getGuild();
		Role role = guild.getRoleById(ROLE_NOT_ROBOT);
		Member member = event.getMember();
		MessageChannel channel = event.getChannel();

		if (channel.getIdLong() == CHANNEL_ROBOT) {

			guild.addRoleToMember(member, role).complete();
			// event.getUser().openPrivateChannel().complete().sendMessage("**You
			// are not a robot !**").complete();
			// event.getUser().openPrivateChannel().complete().sendMessage("https://youtu.be/fsF7enQY8uI").complete();

			/*
			 * Envoyer un message à l'utilisateur avec toutes les informations
			 * sur le GroupeZ
			 */

		}

	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		Message message = event.getMessage();
		Member member = event.getMember();
		Guild guild = null;
		try {
			guild = event.getGuild();
		} catch (Exception e) {
		}
		
		if (guild == null)
			return;
		
		int roleMentionned = message.getMentionedRoles().size();
		int memberMentionned = message.getMentionedMembers().size();

		if ((roleMentionned >= 5 || memberMentionned >= 10) && !member.hasPermission(Permission.MESSAGE_MANAGE)) {

			guild.kick(member, "Le message contient trop de mention: (" + roleMentionned + " roles mentionné, "
					+ memberMentionned + " membres mentionné)").complete();
			member.getUser().openPrivateChannel().complete().sendMessage("You do not have permission to do this.")
					.complete();

		}

	}

	@Override
	public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
		Member member = event.getMember();
		List<Role> roles = event.getRoles();
		RoleManager manager = RoleManager.getInstance();
		manager.addRole(member, roles);
	}

	@Override
	public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
		Member member = event.getMember();
		List<Role> roles = event.getRoles();
		RoleManager manager = RoleManager.getInstance();
		manager.removeRole(member, roles);
	}

	@Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		Guild guild = event.getGuild();
		Role role = guild.getRoleById(ROLE_DEFAULT);
		Member member = event.getMember();
		guild.addRoleToMember(member, role).complete();

		OffsetDateTime dateTime = member.getUser().getTimeCreated();
		Date date = new Date();
		OffsetDateTime offsetDateTime = date.toInstant().atOffset(ZoneOffset.UTC);

		RoleManager manager = RoleManager.getInstance();
		if (!manager.giveRoles(guild, member, guild.getRoleById(ROLE_NOT_ROBOT)))
			if (Math.abs(offsetDateTime.getYear() - dateTime.getYear()) >= 1) {
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						Role role = guild.getRoleById(ROLE_NOT_ROBOT);
						guild.addRoleToMember(member, role).complete();
					}
				}, 1000);
			}

	}

}
