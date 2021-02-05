package fr.maxlego08.zsupport.verify;

public class GPlugin {

	private final int id;
	private final int plugin_id;
	private final String stripe_id;
	private final String uid;
	private final String status;
	private final int price;
	private final String created_at;
	private final String updated_at;
	private final int gift_id;

	/**
	 * @param id
	 * @param plugin_id
	 * @param stripe_id
	 * @param uid
	 * @param status
	 * @param price
	 * @param created_at
	 * @param updated_at
	 * @param gift_id
	 */
	public GPlugin(int id, int plugin_id, String stripe_id, String uid, String status, int price, String created_at,
			String updated_at, int gift_id) {
		super();
		this.id = id;
		this.plugin_id = plugin_id;
		this.stripe_id = stripe_id;
		this.uid = uid;
		this.status = status;
		this.price = price;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.gift_id = gift_id;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the plugin_id
	 */
	public int getPlugin_id() {
		return plugin_id;
	}

	/**
	 * @return the stripe_id
	 */
	public String getStripe_id() {
		return stripe_id;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * @return the created_at
	 */
	public String getCreated_at() {
		return created_at;
	}

	/**
	 * @return the updated_at
	 */
	public String getUpdated_at() {
		return updated_at;
	}

	/**
	 * @return the gift_id
	 */
	public int getGift_id() {
		return gift_id;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Plugin [id=" + id + ", plugin_id=" + plugin_id + ", stripe_id=" + stripe_id + ", uid=" + uid
				+ ", status=" + status + ", price=" + price + ", created_at=" + created_at + ", updated_at="
				+ updated_at + ", gift_id=" + gift_id + "]";
	}
	
	

}
