package fr.maxlego08.zsupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Saveable;

public class Config implements Saveable {

	public static String botToken = "groupez.dev.token.secret";
	public static int ticketNumber = 1;
	public static Long ticketCategoryId = 511517312067829766l;
	public static long ticketChannel = 712305238748692572l;
	public static long ticketOrderChannel = 937373283911872542l;
	public static long commandChannel = 714518716943171605l;

	public static String API_URL = "https://groupez.dev/api/v1/discord/%s";
	public static String DASHBOARD_URL = "https://groupez.dev/dashboard/users/%s";
	
	public static List<Plugin> plugins = new ArrayList<>();

	static {

		plugins.add(new Plugin("zAuctionHouse", 712306983251738625l, 613374375240138761l, 1, 10));
		plugins.add(new Plugin("zShop", 712307096607129611l, 666552224490455041l, 2, 8));
		plugins.add(new Plugin("zSpawner", 712306863869263942l, 613374745186009097l, 4, 6));
		plugins.add(new Plugin("zNexus", 712307057092460584l, 700417597996400680l, 5, 6));
		plugins.add(new Plugin("zKoth", 712307144502018098l, 694224301653491782l, 9, 6));
		plugins.add(new Plugin("zTotem", 712306940323037185l, 613374798243823616l, 8, 6));
		plugins.add(new Plugin("zHopper", 712307379320127488l, 700417649229955154l, 6, 0));
		plugins.add(new Plugin("zTournament", 731802368152174622l, 730685641972645950l, 3, 15));
		plugins.add(new Plugin("zFactionRanking", 731802322211962881l, 729365118609129472l, 7, 10));
		plugins.add(new Plugin("zMobFighter", 843564378946273350l, 843564877611794482l, 41, 8));
		plugins.add(new Plugin("zAuctionHouse Redis", 941374457451249716l, 941723181348192277l, 210, 15));
		plugins.add(new Plugin("zVoteParty", 879471509930926121l, 0l, 124, 0));
		plugins.add(new Plugin("zOldEnchant", 941724257132634152l, 0l, 221, 0));

	}

	public static Optional<Plugin> getPlugin(int plugin_id) {
		return plugins.stream().filter(e -> e.getPlugin_id() == plugin_id).findFirst();
	}

	/**
	 * static Singleton instance.
	 */
	private static volatile Config instance;

	/**
	 * Private constructor for singleton.
	 */
	private Config() {
	}

	/**
	 * Return a singleton instance of Config.
	 */
	public static Config getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (Config.class) {
				if (instance == null) {
					instance = new Config();
				}
			}
		}
		return instance;
	}

	@Override
	public void save(Persist persist) {
		persist.save(getInstance());
	}

	@Override
	public void load(Persist persist) {
		persist.loadOrSaveDefault(getInstance(), Config.class);
	}

}
