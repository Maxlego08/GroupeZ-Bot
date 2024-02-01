package fr.maxlego08.zsupport.utils;

import fr.maxlego08.zsupport.lang.Lang;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.lang.Message;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class ZUtils {

    protected void schedule(long delay, Runnable runnable) {
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                if (runnable != null) runnable.run();
            }
        }, delay);
    }

    protected int getMaxPage(Collection<?> items) {
        return (items.size() / 45) + 1;
    }

    protected int getMaxPage(Collection<?> items, int a) {
        return (items.size() / a) + 1;
    }

    protected double percent(double value, double total) {
        return (value * 100) / total;
    }

    protected double percentNum(double total, double percent) {
        return total * (percent / 100);
    }

    protected String getMessage(LangType type, Message message, Object... args) {
        return Lang.getInstance().getMessage(type, message, args);
    }

    protected boolean hasRole(Member member, long id) {
        return member.getRoles().stream().anyMatch(role -> role.getIdLong() == id || id == 0);
    }

    protected String toList(List<String> list) {
        if (list == null || list.size() == 0) return null;
        if (list.size() == 1) return list.get(0);
        StringBuilder str = new StringBuilder();
        for (int a = 0; a != list.size(); a++) {
            if (a == list.size() - 1) str.append(" and ");
            else if (a != 0) str.append(", ");
            str.append(list.get(a));
        }
        return str.toString();
    }

    public void setEmbedFooter(Guild guild, EmbedBuilder builder) {
        this.setEmbedFooter(guild, builder, new Color(45, 45, 45));
    }

    public void setEmbedFooter(Guild guild, EmbedBuilder builder, Color color) {
        builder.setColor(color);
        builder.setTimestamp(OffsetDateTime.now());
        builder.setFooter(Calendar.getInstance().get(Calendar.YEAR) + " - " + guild.getName(), guild.getIconUrl());
    }

    public void setDescription(EmbedBuilder embedBuilder, String... description) {
        setDescription(embedBuilder, Arrays.asList(description));
    }

    public void setDescription(EmbedBuilder embedBuilder, List<String> description) {
        embedBuilder.setDescription(description.stream().map(line -> line + "\n").collect(Collectors.joining()).trim());
    }

}
