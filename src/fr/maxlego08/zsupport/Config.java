package fr.maxlego08.zsupport;

import fr.maxlego08.zsupport.tickets.storage.SqlConfiguration;
import fr.maxlego08.zsupport.utils.ChannelType;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Savable;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Config implements Savable {

    public static String botToken = "groupez.dev.token.secret";
    public static int ticketNumber = 1;
    public static long ticketCategoryId = 1202650515377360897L;
    public static long ticketChannel = 1202650530845823027L;
    public static long ticketOrderChannel = 937373283911872542L;
    public static long commandChannel = 1202650480363311104L;
    public static long pluginsChannel = 941739992139579523L;
    public static long groupezEmote = 710806746779615233L;
    public static long spigotEmote = 942107776883257364L;
    public static long generalChannel = 1202649374740447285L;
    public static long ticketLogChannel = 1202652708335788084L;

    public static String CUSTOM_KEY = "sdfsdkfsldkfsdfdsfkmsdlmf,mdslkflsdk;fk�ljzedioahrnduidgstjhqsbgvdiyqsdgqksjdzapo�fmazflpo�icjozefdpzokfdozpefijz,fkezfljsdklfzeoirfsndlkfjkezf6+ze5f+zef2+ezf5s3fzef2zef+zfe56z+etfg+rf4+sfg";
    public static String API_URL = "https://groupez.dev/api/v1/discord/%s";
    public static String API_URL_VERIFY_CUSTOMER = "https://groupez.dev/api/v1/discord/%s/apply/%s";
    public static String API_RESOURCE_URL = "https://groupez.dev/api/v1/resource/%s";
    public static String RESOURCE_URL = "https://groupez.dev/resources/%s";
    public static String DASHBOARD_URL = "https://groupez.dev/dashboard/users/%s";
    public static Map<String, String> documentations = new HashMap<>();

    public static List<Plugin> plugins = new ArrayList<>();
    public static Map<Long, ChannelType> channelsWithInformations = new HashMap<>();

    public static SqlConfiguration sqlConfiguration = new SqlConfiguration("homestead", "secret", "192.168.10.10", "zsupport", 3306);
    public static long guildId = 1202649373465382923L;

    /**
     * static Singleton instance.
     */
    private static volatile Config instance;

    static {

        plugins.add(new Plugin("zAuctionHouse", 809473314077802496L, 613374375240138761L, 1, 10, "zauctionhouse"));
        plugins.add(new Plugin("zShop", 712307096607129611L, 666552224490455041L, 2, 8, "zpl"));
        plugins.add(new Plugin("zSpawner", 712306863869263942L, 613374745186009097L, 4, 6, "zspawner"));
        plugins.add(new Plugin("zNexus", 712307057092460584L, 700417597996400680L, 5, 6, "znexus"));
        plugins.add(new Plugin("zKoth", 712307144502018098L, 694224301653491782L, 9, 6, "zkoth"));
        plugins.add(new Plugin("zTotem", 712306940323037185L, 613374798243823616L, 8, 6, "ztotem"));
        plugins.add(new Plugin("zHopper", 712307379320127488L, 700417649229955154L, 6, 0, "zhopper"));
        plugins.add(new Plugin("zTournament", 731802368152174622L, 730685641972645950L, 3, 15, "ztournament"));
        plugins.add(new Plugin("zFactionRanking", 731802322211962881L, 729365118609129472L, 7, 10, "zfactionranking"));
        plugins.add(new Plugin("zMobFighter", 843564378946273350L, 843564877611794482L, 41, 8, "zmobfighter"));
        plugins.add(new Plugin("zAuctionHouse Redis", 941723116873326623L, 941723181348192277L, 210, 15, null));
        plugins.add(new Plugin("zVoteParty", 879471509930926121L, 0L, 124, 0, "zvoteparty"));
        plugins.add(new Plugin("zOldEnchant", 941724257132634152L, 0L, 221, 0, null));

        channelsWithInformations.put(774320653892976692L, ChannelType.FREE);
        channelsWithInformations.put(879813851959427112L, ChannelType.FREE);
        channelsWithInformations.put(511516467615760407L, ChannelType.GENERAL);

        documentations.put("zMenu", "https://docs.zmenu.dev/");
        documentations.put("zAuctionHouse", "https://zauctionhouse.groupez.dev/");
        documentations.put("zShop", "https://zshop.groupez.dev/");
        documentations.put("zKoth", "https://zkoth.groupez.dev/");
        documentations.put("zDrawer", "https://zdrawer.groupez.dev/");
        documentations.put("zScheduler", "https://scheduler.groupez.dev/");
    }

    /**
     * Private constructor for singleton.
     */
    private Config() {
    }

    public static Optional<Plugin> getPlugin(int plugin_id) {
        return plugins.stream().filter(e -> e.getPluginId() == plugin_id).findFirst();
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

    public static Emoji getSpigotEmoji(Guild guild){
        return guild.getEmojiById(Config.spigotEmote);
    }

}
