package fr.maxlego08.zsupport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.listener.CommandListener;
import fr.maxlego08.zsupport.listener.MemberListener;
import fr.maxlego08.zsupport.role.RoleManager;
import fr.maxlego08.zsupport.tickets.TicketListener;
import fr.maxlego08.zsupport.tickets.TicketManager;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Savable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ZSupport implements Constant {

    public static ZSupport instance;
    private final Gson gson;
    private final JDA jda;
    private final CommandManager commandManager;
    private final CommandListener commandListener;
    private final TicketManager ticketManager;
    private final List<Savable> savables = new ArrayList<>();
    private final Persist persist;
    private final MemberListener memberListener;
    // private final XpListener xpListener;

    public ZSupport() throws LoginException {

        instance = this;

        this.gson = getGsonBuilder().create();
        this.persist = new Persist(this);

        this.savables.add(Config.getInstance());
        this.savables.add(RoleManager.getInstance());

        this.savables.forEach(save -> save.load(this.persist));

        this.ticketManager = new TicketManager(this);
        this.commandManager = new CommandManager(this);
        this.commandListener = new CommandListener(this);
        this.memberListener = new MemberListener();
        // xpListener = new XpListener(this);

        Thread thread = new Thread(this.commandListener, "bot");
        thread.start();

        System.out.println(PREFIX_CONSOLE + "Chargement du bot");

        List<GatewayIntent> list = new ArrayList<>();

        list.add(GatewayIntent.GUILD_MEMBERS);
        list.add(GatewayIntent.GUILD_EMOJIS_AND_STICKERS);
        list.add(GatewayIntent.DIRECT_MESSAGES);
        list.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
        list.add(GatewayIntent.GUILD_MESSAGES);
        list.add(GatewayIntent.GUILD_MESSAGE_TYPING);

        JDABuilder builder = JDABuilder.create(Config.botToken, list);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
        this.jda = builder.build();
        this.jda.getPresence().setActivity(Activity.playing("/help V" + VERSION));
        this.jda.addEventListener(this.commandListener);
        this.jda.addEventListener(this.memberListener);
        this.jda.addEventListener(new TicketListener(this.ticketManager));

        /**
         *
         * JDA jda= this.textChannel.getJDA(); Emoji emoji =
         * Emoji.fromEmote(jda.getEmoteById(941374457312850011l));
         * .setActionRow(new ButtonImpl("test", "test", ButtonStyle.SECONDARY,
         * false, emoji))
         *
         */

        /*
         * CommandListUpdateAction action = jda.updateCommands();
         *
         * action.addCommands(new CommandData("hello", "test"));
         * action.addCommands(new CommandData("info", "test"));
         *
         * action.queue();
         */

        System.out.println(PREFIX_CONSOLE + "Bot lancé avec succés !");

        this.ticketManager.load();

        Timer timer = new Timer();
        long period = 1000 * 60 * 30;
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                savables.forEach(save -> save.save(persist));
            }
        }, period, period);
    }

    public static void main(String[] args) {
        try {
            new ZSupport();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public GsonBuilder getGsonBuilder() {
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.VOLATILE);
    }

    public Gson getGson() {
        return gson;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public JDA getJda() {
        return jda;
    }

    public void onDisable() {
        this.savables.forEach(save -> {
            System.out.println("Sauvegarde de " + save.getClass().getName());
            save.save(persist);
        });
        this.ticketManager.save();
        System.out.println("Shutdown de JDA");
        jda.shutdownNow();
    }

    public CommandListener getCommandListener() {
        return commandListener;
    }

    /**
     * @return the saveables
     */
    public List<Savable> getSaveables() {
        return savables;
    }

    /**
     * @return the persist
     */
    public Persist getPersist() {
        return persist;
    }


    public void save() {
        this.savables.forEach(save -> save.save(persist));
    }

    public TicketManager getTicketManager() {
        return ticketManager;
    }
}
