package fr.maxlego08.zsupport.plugins;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.time.OffsetDateTime;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fr.maxlego08.zsupport.Config;
import fr.maxlego08.zsupport.ZSupport;
import fr.maxlego08.zsupport.utils.Constant;
import fr.maxlego08.zsupport.utils.Plugin;
import fr.maxlego08.zsupport.utils.ZUtils;
import fr.maxlego08.zsupport.utils.image.ImageHelper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.internal.interactions.ButtonImpl;

public class PluginManager extends ZUtils implements Constant {

	/**
	 * Display plugins informations
	 * 
	 * @param guild
	 */
	public void displayPlugins(Guild guild) {
		Thread thread = new Thread(() -> Config.plugins.forEach(plugin -> this.displayPlugin(guild, plugin)));
		thread.start();
	}

	/**
	 * Display plugin informations
	 * 
	 * @param guild
	 * @param plugin
	 */
	public void displayPlugin(Guild guild, Plugin plugin) {

		TextChannel channel = guild.getTextChannelById(Config.pluginsChannel);

		try {

			String urlAsString = String.format(Config.API_RESOURCE_URL, plugin.getPlugin_id());
			URL url = new URL(urlAsString);

			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

			// add reuqest header
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			int responseCode = con.getResponseCode();

			if (responseCode != 200) {
				System.out.println("Erreur : " + responseCode);
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

			Type resourceType = new TypeToken<Resource>() {
			}.getType();

			Resource resource = gson.fromJson(response.toString(), resourceType);
			
			int[] colorRGB = ImageHelper.getHexColor(resource.getLogo());

			EmbedBuilder builder = new EmbedBuilder();
			builder.setColor(new Color(colorRGB[0], colorRGB[1], colorRGB[2]));
			builder.setTimestamp(OffsetDateTime.now());
			builder.setFooter("2022 - " + guild.getName(), guild.getIconUrl());

			builder.setThumbnail(resource.getLogo());
			builder.setTitle(resource.getName(), resource.getResourceUrl());
			builder.setDescription(resource.getTag());

			Button button = new ButtonImpl("btn:link:resource", "Access to the plugin", ButtonStyle.LINK,
					resource.getResourceUrl(), false, Emoji.fromEmote(plugin.getEmote(guild)));
			channel.sendMessageEmbeds(builder.build()).setActionRow(button).queue();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
