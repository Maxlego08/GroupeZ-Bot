package fr.maxlego08.zsupport.utils;

public class Plugin {

	private final String name;
	private final long emoteId;
	private final long role;
	private final int plugin_id;

	/**
	 * @param name
	 * @param emoteId
	 * @param role
	 * @param plugin_id
	 */
	public Plugin(String name, long emoteId, long role, int plugin_id) {
		super();
		this.name = name;
		this.emoteId = emoteId;
		this.role = role;
		this.plugin_id = plugin_id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the plugin_id
	 */
	public int getPlugin_id() {
		return plugin_id;
	}

	/**
	 * @return the emoteId
	 */
	public long getEmoteId() {
		return emoteId;
	}

	/**
	 * @return the role
	 */
	public long getRole() {
		return role;
	}

}
