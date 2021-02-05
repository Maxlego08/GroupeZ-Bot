package fr.maxlego08.zsupport.utils;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.maxlego08.zsupport.lang.Lang;
import fr.maxlego08.zsupport.lang.Message;
import fr.maxlego08.zsupport.tickets.LangType;
import net.dv8tion.jda.api.entities.Member;

public class ZUtils {

	protected void schedule(long delay, Runnable runnable) {
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				if (runnable != null)
					runnable.run();
			}
		}, delay);
	}

	/**
	 * 
	 * @param items
	 * @return
	 */
	protected int getMaxPage(Collection<?> items) {
		return (items.size() / 45) + 1;
	}

	/**
	 * 
	 * @param items
	 * @param a
	 * @return
	 */
	protected int getMaxPage(Collection<?> items, int a) {
		return (items.size() / a) + 1;
	}

	/**
	 * 
	 * @param value
	 * @param total
	 * @return
	 */
	protected double percent(double value, double total) {
		return (double) ((value * 100) / total);
	}

	/**
	 * 
	 * @param total
	 * @param percent
	 * @return
	 */
	protected double percentNum(double total, double percent) {
		return (double) (total * (percent / 100));
	}

	/**
	 * 
	 * @param type
	 * @param message
	 * @return
	 */
	protected String getMessage(LangType type, Message message) {
		return Lang.getInstance().getMessage(type, message);
	}

	protected boolean hasRole(Member member, long id) {
		return member.getRoles().stream().filter(role -> role.getIdLong() == id).findAny().isPresent();
	}

	/**
	 * @param list
	 * @param color
	 * @param color2
	 * @return
	 */
	protected String toList(List<String> list) {
		if (list == null || list.size() == 0)
			return null;
		if (list.size() == 1)
			return list.get(0);
		String str = "";
		for (int a = 0; a != list.size(); a++) {
			if (a == list.size() - 1 && a != 0)
				str += " and ";
			else if (a != 0)
				str += ", ";
			str += list.get(a);
		}
		return str;
	}

}
