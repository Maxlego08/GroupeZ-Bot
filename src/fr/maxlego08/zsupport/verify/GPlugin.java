package fr.maxlego08.zsupport.verify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GPlugin {

	private final int id;
	private final int pluginId;
	private final int userId;

	/**
	 * @param id
	 * @param pluginId
	 * @param userId
	 */
	public GPlugin(int id, int pluginId, int userId) {
		super();
		this.id = id;
		this.pluginId = pluginId;
		this.userId = userId;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the pluginId
	 */
	public int getPluginId() {
		return pluginId;
	}

	/**
	 * @return the userId
	 */
	public int getUserId() {
		return userId;
	}

	@SuppressWarnings("unchecked")
	public static List<GPlugin> toList(List<Object> list) {
		List<GPlugin> gPlugins = new ArrayList<>();

		list.forEach(e -> {

			Map<String, Object> map = (Map<String, Object>) e;

			System.out.println(map);

			int id = ((Number) map.get("id")).intValue();
			int pluginId = ((Number) map.get("plugin_id")).intValue();
			int userId = ((Number) map.get("user_id")).intValue();

			gPlugins.add(new GPlugin(id, pluginId, userId));

		});

		return gPlugins;
	}

}
