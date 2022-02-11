package fr.maxlego08.zsupport.verify;

import fr.maxlego08.zsupport.Config;

public class GUser {

	private final String name;
	private final int id;
	private final String avatar;
	private final long updatedAt;

	/**
	 * @param name
	 * @param id
	 * @param avater
	 */
	public GUser(String name, int id, String avatar) {
		super();
		this.name = name;
		this.id = id;
		this.avatar = avatar;
		this.updatedAt = System.currentTimeMillis();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the avater
	 */
	public String getAvatar() {
		return avatar;
	}

	public long getUpdatedAt() {
		return updatedAt;
	}

	public String getDashboardURL() {
		return String.format(Config.DASHBOARD_URL, this.id);
	}

}
