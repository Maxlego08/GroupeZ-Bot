package fr.maxlego08.zsupport;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.security.auth.login.LoginException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.maxlego08.zsupport.command.CommandManager;
import fr.maxlego08.zsupport.listener.CommandListener;
import fr.maxlego08.zsupport.listener.MemberListener;
import fr.maxlego08.zsupport.role.RoleManager;
import fr.maxlego08.zsupport.suggestions.SuggestionManager;
import fr.maxlego08.zsupport.suggestions.listeners.SuggestInteraction;
import fr.maxlego08.zsupport.suggestions.listeners.SuggestListener;
import fr.maxlego08.zsupport.tickets.TicketListener;
import fr.maxlego08.zsupport.tickets.TicketManager;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.storage.Persist;
import fr.maxlego08.zsupport.utils.storage.Saveable;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class ZSupport implements Constant {

	private final Gson gson;
	private final JDA jda;
	private final CommandManager commandManager;
	private final CommandListener commandListener;
	private final List<Saveable> saveables = new ArrayList<Saveable>();
	private final Persist persist;
	private final TicketManager ticketManager;
	private final TicketListener ticketListener;
	private final SuggestListener suggestListener;
	private final MemberListener memberListener;
	public static ZSupport instance;
	// private final XpListener xpListener;

	public static void main(String[] args) {
		try {
			new ZSupport();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	public ZSupport() throws LoginException {

		instance = this;

		this.gson = getGsonBuilder().create();
		this.persist = new Persist(this);
		this.commandManager = new CommandManager(this);
		this.commandListener = new CommandListener(this);
		this.memberListener = new MemberListener();
		this.ticketManager = new TicketManager(this);
		this.ticketListener = new TicketListener(this.ticketManager);
		this.suggestListener = new SuggestListener();
		// xpListener = new XpListener(this);

		this.saveables.add(Config.getInstance());
		this.saveables.add(this.ticketManager);
		this.saveables.add(RoleManager.getInstance());
		this.saveables.add(new SuggestionManager());

		Thread thread = new Thread(this.commandListener, "bot");
		thread.start();

		this.saveables.forEach(save -> save.load(this.persist));

		System.out.println(PREFIX_CONSOLE + "Chargement du bot");

		List<GatewayIntent> list = new ArrayList<>();

		list.add(GatewayIntent.GUILD_MEMBERS);
		list.add(GatewayIntent.GUILD_EMOJIS);
		list.add(GatewayIntent.DIRECT_MESSAGES);
		list.add(GatewayIntent.GUILD_MESSAGE_REACTIONS);
		list.add(GatewayIntent.GUILD_MESSAGES);
		list.add(GatewayIntent.GUILD_MESSAGE_TYPING);

		JDABuilder builder = JDABuilder.create(Config.botToken, list);
		builder.setMemberCachePolicy(MemberCachePolicy.ALL);
		this.jda = builder.build();
		this.jda.getPresence().setActivity(Activity.playing("/help V" + VERSION));
		this.jda.addEventListener(this.commandListener);
		this.jda.addEventListener(this.ticketListener);
		this.jda.addEventListener(this.memberListener);
		this.jda.addEventListener(this.suggestListener);
		this.jda.addEventListener(new SuggestInteraction());

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

		Timer timer = new Timer();
		long period = 1000 * 60 * 30;
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				saveables.forEach(save -> save.save(persist));
			}
		}, period, period);
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
		this.saveables.forEach(save -> {
			System.out.println("Sauvegarde de " + save.getClass().getName());
			save.save(persist);
		});
		System.out.println("Shutdown de JDA");
		jda.shutdownNow();
	}

	public CommandListener getCommandListener() {
		return commandListener;
	}

	/**
	 * @return the saveables
	 */
	public List<Saveable> getSaveables() {
		return saveables;
	}

	/**
	 * @return the persist
	 */
	public Persist getPersist() {
		return persist;
	}

	/**
	 * @return the ticketManager
	 */
	public TicketManager getTicketManager() {
		return ticketManager;
	}

	/**
	 * @return the ticketListener
	 */
	public TicketListener getTicketListener() {
		return ticketListener;
	}

	public void save() {
		this.saveables.forEach(save -> save.save(persist));
	}

}
