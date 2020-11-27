package fr.maxlego08.zsupport.role;

import java.util.ArrayList;
import java.util.List;

public class RoleUser {

	private final long id;
	private final List<Long> roles = new ArrayList<Long>();

	/**
	 * @param id
	 */
	public RoleUser(long id) {
		super();
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the roles
	 */
	public List<Long> getRoles() {
		return roles;
	}

	/**
	 * Check if role exist
	 * @param id
	 * @return true if roles contains id
	 */
	public boolean is(long id) {
		return roles.contains(id);
	}

	/**
	 * Add
	 * @param id
	 */
	public void add(long id) {
		this.roles.add(id);
	}

	/**
	 * Remove
	 * @param id
	 */
	public void remove(long id) {
		this.roles.remove(id);
	}
}
