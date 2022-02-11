package fr.maxlego08.zsupport.verify;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.lang.BasicMessage;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.utils.commands.PlayerSender;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class VerifyManager extends ZUtils {

	/**
	 * static Singleton instance.
	 */
	private static volatile VerifyManager instance;
	private final String USER__AGENT = "Mozilla/5.0";

	/**
	 * Private constructor for singleton.
	 */
	private VerifyManager() {
	}

	/**
	 * Return a singleton instance of VerifyManager.
	 */
	public static VerifyManager getInstance() {
		// Double lock for thread safety.
		if (instance == null) {
			synchronized (VerifyManager.class) {
				if (instance == null) {
					instance = new VerifyManager();
				}
			}
		}
		return instance;
	}

	/**
	 * Allows you to verify the user
	 * 
	 * @param user
	 * @param textChannel
	 * @param sender
	 * @param delete
	 */
	public void submitData(User user, TextChannel textChannel, PlayerSender sender, boolean delete) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					sendData(user, textChannel, sender, delete);
				} catch (Exception e) {
				}
			}
		}).start();
	}

	/**
	 * Permet de v�rifier si l'utilisateur peut cr�er un ticket
	 * 
	 * @param user
	 * @param runnableSuccess
	 * @param runnableError
	 */
	public void userIsLink(User user, Runnable runnableSuccess, Runnable runnableError) {
		try {
			String url = String.format(Config.API_URL, user.getIdLong());
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			// add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", this.USER__AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.flush();
			wr.close();

			int responseCode = con.getResponseCode();

			if (responseCode != 200) {
				runnableError.run();
				return;
			}

			runnableSuccess.run();
		} catch (IOException e) {
			runnableError.run();
		}
	}

	/**
	 * 
	 * @param user
	 * @param textChannel
	 * @param sender
	 * @param delete
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void sendData(User user, TextChannel textChannel, PlayerSender sender, boolean delete) throws Exception {

		Guild guild = textChannel.getGuild();

		String urlAsString = String.format(Config.API_URL, user.getIdLong());
		URL url = new URL(urlAsString);

		HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", this.USER__AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		// If the query returned an error
		if (responseCode != 200) {
			this.sendErrorMessage(textChannel, user, guild, BasicMessage.VERIFY_ERROR, null);
			return;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		Gson gson = ZSupport.instance.getGson();

		Type listType = new TypeToken<Map<String, Object>>() {
		}.getType();

		Map<String, Object> values = gson.fromJson(response.toString(), listType);

		Map<String, Object> userAsMap = (Map<String, Object>) values.get("user");
		GUser gUser = new GUser((String) userAsMap.get("name"), ((Number) userAsMap.get("id")).intValue(),
				(String) userAsMap.get("avatar"));

		List<GPlugin> list = GPlugin.toList((List<Object>) values.get("plugins"));

		// If the user has not purchased any plugin

		if (list.size() == 0) {
			this.sendErrorMessage(textChannel, user, guild, BasicMessage.VERIFY_ERROR_EMPTY, gUser);
			return;
		}

		Member member = sender.getMember();

		List<Role> roles = list.stream().map(e -> {
			Optional<Plugin> optional = Config.getPlugin(e.getPluginId());
			if (optional.isPresent()) {
				Plugin plugin = optional.get();
				Role role = guild.getRoleById(plugin.getRole());
				if (hasRole(member, plugin.getRole())) {
					return null;
				}
				return role;
			}
			return null;
		}).filter(e -> e != null).collect(Collectors.toList());

		// If the user already has all the roles
		if (roles.size() == 0) {
			this.sendErrorMessage(textChannel, user, guild, BasicMessage.VERIFY_ERROR_ALREADY, gUser);
			return;
		}

		roles.forEach(role -> guild.addRoleToMember(member, role).queue());

		Random random = new Random();
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("GroupeZ - " + user.getAsTag(), gUser.getDashboardURL());
		builder.setColor(Color.getHSBColor(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		builder.setFooter("2022 - " + guild.getName(), guild.getIconUrl());
		builder.setThumbnail(gUser.getAvatar());

		String footer = "\n\n\nUse ``!verify`` to verify your account.";

		if (roles.size() == 1) {
			builder.setDescription("You just got the role: " + roles.get(0).getAsMention() + footer);
			System.out.println("Ajout du r�le " + roles.get(0).getName() + " � l'utilisateur " + user.getAsTag());
		} else {
			String str = toList(roles.stream().map(e -> e.getAsMention()).collect(Collectors.toList()));
			builder.setDescription("You just got the roles: " + str + footer);
			System.out.println("Ajout des r�les " + str + " � l'utilisateur " + user.getAsTag());
		}

		textChannel.sendTyping().queue();
		textChannel.sendMessageEmbeds(builder.build()).queue();
		builder.clear();

	}

	/**
	 * Send error message
	 * 
	 * @param textChannel
	 * @param user
	 * @param guild
	 * @param basicMessage
	 */
	private void sendErrorMessage(TextChannel textChannel, User user, Guild guild, BasicMessage basicMessage,
			GUser gUser) {
		EmbedBuilder builder = new EmbedBuilder();

		builder.setColor(new Color(240, 10, 10));
		builder.setFooter("2022 - " + guild.getName(), guild.getIconUrl());

		builder.setDescription(basicMessage.getMessage());

		if (gUser != null) {
			builder.setTitle("GroupeZ - " + user.getAsTag(), gUser.getDashboardURL());
			builder.setThumbnail(gUser.getAvatar());
		} else {
			builder.setTitle("GroupeZ - " + user.getAsTag());
		}

		textChannel.sendMessageEmbeds(builder.build()).queue();
	}

}
