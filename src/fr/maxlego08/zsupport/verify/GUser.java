package fr.maxlego08.zsupport.verify;

import java.util.List;

import fr.maxlego08.zsupport.Config;

public class GUser {

	private final String name;
	private final int id;
	private final String avatar;
	private final long expiredAt;
	private final List<GPlugin> plugins;

	/**
	 * @param name
	 * @param id
	 * @param avater
	 */
	public GUser(String name, int id, String avatar, List<GPlugin> plugins) {
		super();
		this.name = name;
		this.id = id;
		this.avatar = avatar;
		this.plugins = plugins;
		this.expiredAt = System.currentTimeMillis() + (1000 * 60 * 5);
	}

	public List<GPlugin> getPlugins() {
		return plugins;
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

	public long getExpiredAt() {
		return expiredAt;
	}

	public String getDashboardURL() {
		return String.format(Config.DASHBOARD_URL, this.id);
	}

	public boolean isExpired() {
		return System.currentTimeMillis() > this.expiredAt;
	}

}
