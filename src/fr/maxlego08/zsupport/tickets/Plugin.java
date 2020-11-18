package fr.maxlego08.zsupport.tickets;

public class Plugin {

	private final String name;
	private final long emoteId;
	private final long role;

	/**
	 * @param name
	 * @param emoteId
	 * @param role
	 */
	public Plugin(String name, long emoteId, long role) {
		super();
		this.name = name;
		this.emoteId = emoteId;
		this.role = role;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Plugin [name=" + name + ", emoteId=" + emoteId + ", role=" + role + "]";
	}

}
