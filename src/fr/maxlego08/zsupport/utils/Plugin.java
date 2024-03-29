package fr.maxlego08.zsupport.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class Plugin {

    public transient static Plugin EMPTY = new Plugin("Other", 0, 0, 0, 0, "");

    private final String name;
    private final long emoteId;
    private final long role;
    private final int plugin_id;
    private final double price;
    private final String command;

    /**
     * @param name
     * @param emoteId
     * @param role
     * @param plugin_id
     * @param price
     */
    public Plugin(String name, long emoteId, long role, int plugin_id, double price, String command) {
        super();
        this.name = name;
        this.emoteId = emoteId;
        this.role = role;
        this.plugin_id = plugin_id;
        this.price = price;
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the plugin_id
     */
    public int getPluginId() {
        return plugin_id;
    }

    /**
     * @return the emoteId
     */
    public long getEmoteId() {
        return emoteId;
    }

    /**
     * @return the role
     */
    public long getRole() {
        return role;
    }

    public Emoji getEmote(Guild guild) {
        return guild.getEmojiById(this.emoteId);
    }

    public boolean isReal() {
        return this.emoteId != 0 && this.role != 0 && this.plugin_id != 0;
    }

    public boolean isPremium() {
        return this.price > 0;
    }

    public boolean hasCommand() {
        return this.command != null;
    }

}
