package fr.maxlego08.zsupport.verify;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.tickets.Plugin;
import fr.maxlego08.zsupport.utils.Message;
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

	private void sendData(User user, TextChannel textChannel, PlayerSender sender, boolean delete)
			throws Exception {

		String url = "https://groupez.dev/api/v1/discord";
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER__AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		String urlParameters = "discord=" + user.getAsTag();

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();

		// System.out.println("\nSending 'POST' request to URL : " + url);
		// System.out.println("Post parameters : " + urlParameters);
		// System.out.println("Response Code : " + responseCode);

		if (responseCode != 200) {
			sender.sendEmbed(Message.VERIFY_ERROR, true);
			return;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		// System.out.println(response.toString());

		Gson gson = ZSupport.instance.getGson();

		Type listType = new TypeToken<List<GPlugin>>() {
		}.getType();
		List<GPlugin> list = gson.fromJson(response.toString(), listType);

		if (list.size() == 0) {
			sender.sendEmbed(Message.VERIFY_ERROR_EMPTY, delete);
			return;
		}

		Guild guild = textChannel.getGuild();
		Member member = sender.getMember();

		List<Role> roles = list.stream().map(e -> {
			Optional<Plugin> optional = Config.getPlugin(e.getPlugin_id());
			if (optional.isPresent()) {
				Plugin plugin = optional.get();
				Role role = guild.getRoleById(plugin.getRole());
				if (hasRole(member, plugin.getRole()))
					return null;
				return role;
			}
			return null;
		}).filter(e -> e != null).collect(Collectors.toList());

		if (roles.size() == 0) {
			sender.sendEmbed(Message.VERIFY_ERROR_ALREADY, delete);
			return;
		}

		roles.forEach(role -> guild.addRoleToMember(member, role).complete());

		Random random = new Random();
		EmbedBuilder builder = new EmbedBuilder();

		builder.setTitle("GroupeZ - " + user.getAsTag());
		builder.setColor(Color.getHSBColor(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
		builder.setFooter("2021 - " + guild.getName(), guild.getIconUrl());

		if (roles.size() == 1) {
			builder.setDescription("You just got the role: " + roles.get(0).getAsMention());
			System.out.println("Ajout du rôle " + roles.get(0).getName() + " à l'utilisateur " + user.getAsTag());
		} else {
			String str = toList(roles.stream().map(e -> e.getAsMention()).collect(Collectors.toList()));
			builder.setDescription("You just got the roles: " + str);
			System.out.println("Ajout des rôles " + str + " à l'utilisateur " + user.getAsTag());
		}

		textChannel.sendTyping().queue();
		textChannel.sendMessage(builder.build()).complete();
		builder.clear();

	}

}
