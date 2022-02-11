package fr.maxlego08.zsupport.plugins;

import fr.maxlego08.zsupport.Config;

public class Resource {

	private final int id;
	private final String name;
	private final int price;
	private final String image;
	private final String description;
	private final String created_at;
	private final String updated_at;
	private final String tag;
	private final int user_id;
	private final int version_id;
	private final int category_id;
	private final int download;
	private final int purchases;
	private final int is_display;
	private final int is_pending;
	private final String file;
	private final String unique_id;
	private final String logo;
	private final Version version;

	/**
	 * @param id
	 * @param name
	 * @param price
	 * @param image
	 * @param description
	 * @param created_at
	 * @param updated_at
	 * @param tag
	 * @param user_id
	 * @param version_id
	 * @param category_id
	 * @param download
	 * @param purchase
	 * @param is_display
	 * @param is_pending
	 * @param file
	 * @param unique_id
	 * @param logo
	 * @param version
	 */
	public Resource(int id, String name, int price, String image, String description, String created_at,
			String updated_at, String tag, int user_id, int version_id, int category_id, int download, int purchases,
			int is_display, int is_pending, String file, String unique_id, String logo, Version version) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.image = image;
		this.description = description;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.tag = tag;
		this.user_id = user_id;
		this.version_id = version_id;
		this.category_id = category_id;
		this.download = download;
		this.purchases = purchases;
		this.is_display = is_display;
		this.is_pending = is_pending;
		this.file = file;
		this.unique_id = unique_id;
		this.logo = logo;
		this.version = version;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the price
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
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
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @return the user_id
	 */
	public int getUser_id() {
		return user_id;
	}

	/**
	 * @return the version_id
	 */
	public int getVersion_id() {
		return version_id;
	}

	/**
	 * @return the category_id
	 */
	public int getCategory_id() {
		return category_id;
	}

	/**
	 * @return the download
	 */
	public int getDownload() {
		return download;
	}

	/**
	 * @return the purchases
	 */
	public int getPurchases() {
		return purchases;
	}

	/**
	 * @return the is_display
	 */
	public int getIs_display() {
		return is_display;
	}

	/**
	 * @return the is_pending
	 */
	public int getIs_pending() {
		return is_pending;
	}

	/**
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * @return the unique_id
	 */
	public String getUnique_id() {
		return unique_id;
	}

	/**
	 * @return the logo
	 */
	public String getLogo() {
		return logo;
	}

	/**
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Resource [id=" + id + ", name=" + name + ", price=" + price + ", image=" + image + ", created_at="
				+ created_at + ", updated_at=" + updated_at + ", tag=" + tag + ", user_id=" + user_id + ", version_id="
				+ version_id + ", category_id=" + category_id + ", download=" + download + ", purchases=" + purchases
				+ ", is_display=" + is_display + ", is_pending=" + is_pending + ", file=" + file + ", unique_id="
				+ unique_id + ", logo=" + logo + ", version=" + version + "]";
	}

	public String getResourceUrl() {
		return String.format(Config.RESOURCE_URL, this.id);
	}

}
