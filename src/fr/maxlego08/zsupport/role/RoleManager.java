package fr.maxlego08.zsupport.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Savable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public class RoleManager implements Savable {

	private static Map<Long, RoleUser> roles = new HashMap<Long, RoleUser>();

	/**
	 * static Singleton instance.
	 */
	private static volatile RoleManager instance;

	/**
	 * Private constructor for singleton.
	 */
	private RoleManager() {
	}

	/**
	 * Return a singleton instance of RoleManager.
	 */
	public static RoleManager getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (RoleManager.class) {
				if (instance == null) {
					instance = new RoleManager();
				}
			}
		}
		return instance;
	}

	@Override
	public void save(Persist persist) {
		persist.save(this, "roles");
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(this, RoleManager.class, "roles");
	}

	public RoleUser getRole(long id) {
		if (!roles.containsKey(id)) {
			RoleUser roleUser = new RoleUser(id);
			roles.put(id, roleUser);
			return roleUser;
		}
		return roles.get(id);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean contains(long id) {
		return roles.containsKey(id);
	}

	/**
	 * Gives roles
	 * 
	 * @param guild
	 * @param member
	 * @param role
	 * @return
	 */
	public boolean giveRoles(Guild guild, Member member, Role role) {

		if (!this.contains(member.getIdLong()))
			return false;

		RoleUser roleUser = getRole(member.getIdLong());
		for (long currentRole : roleUser.getRoles())
			try {

				Role tmpRole = guild.getRoleById(currentRole);
				guild.addRoleToMember(member, tmpRole).complete();

			} catch (Exception e) {
				return false;
			}

		guild.addRoleToMember(member, role).complete();
		return true;
	}

	/**
	 * Check if id is a role
	 * 
	 * @param role
	 * @return
	 */
	public boolean isRole(Role role) {
		return Config.plugins.stream().filter(plugin -> plugin.getRole() == role.getIdLong()).findFirst().isPresent();
	}

	/**
	 * Add role
	 * 
	 * @param member
	 * @param addRoles
	 */
	public void addRole(Member member, List<Role> addRoles) {

		for (Role role : addRoles) {

			if (!isRole(role))
				continue;

			RoleUser roleUser = getRole(member.getIdLong());
			roleUser.add(role.getIdLong());
		}

	}

	/**
	 * Remove role
	 * 
	 * @param member
	 * @param addRoles
	 */
	public void removeRole(Member member, List<Role> addRoles) {

		for (Role role : addRoles) {

			if (!isRole(role))
				continue;

			RoleUser roleUser = getRole(member.getIdLong());
			roleUser.remove(role.getIdLong());
		}

	}

}