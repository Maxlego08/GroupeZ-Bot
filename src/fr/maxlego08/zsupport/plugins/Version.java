package fr.maxlego08.zsupport.plugins;

public class Version {

	private final int id;
	private final String version;
	private final int download;
	private final int plugin_id;
	private final String title;
	private final String extension;
	private final String description;
	private final String created_at;
	private final String updated_at;
	private final String file;

	/**
	 * @param id
	 * @param version
	 * @param download
	 * @param plugin_id
	 * @param title
	 * @param extension
	 * @param description
	 * @param created_at
	 * @param updated_at
	 * @param file
	 */
	public Version(int id, String version, int download, int plugin_id, String title, String extension,
			String description, String created_at, String updated_at, String file) {
		super();
		this.id = id;
		this.version = version;
		this.download = download;
		this.plugin_id = plugin_id;
		this.title = title;
		this.extension = extension;
		this.description = description;
		this.created_at = created_at;
		this.updated_at = updated_at;
		this.file = file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Version [id=" + id + ", version=" + version + ", download=" + download + ", plugin_id=" + plugin_id
				+ ", title=" + title + ", extension=" + extension + ", created_at=" + created_at + ", updated_at="
				+ updated_at + ", file=" + file + "]";
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the download
	 */
	public int getDownload() {
		return download;
	}

	/**
	 * @return the plugin_id
	 */
	public int getPlugin_id() {
		return plugin_id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
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
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

}
