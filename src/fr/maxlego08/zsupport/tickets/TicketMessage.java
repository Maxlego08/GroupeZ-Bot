package fr.maxlego08.zsupport.tickets;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TicketMessage {

	private final long createdAt;
	private final long userId;
	private final String username;
	private final String content;

	/**
	 * @param userId
	 * @param username
	 * @param content
	 */
	public TicketMessage(long userId, String username, String content) {	
		super();
		this.createdAt = System.currentTimeMillis();
		this.userId = userId;
		this.username = username;
		this.content = content;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @return the createdAt
	 */
	public long getCreatedAt() {
		return createdAt;
	}
	
	@Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = formatter.format(new Date(this.createdAt));
        return formattedDate + ", (" + this.userId + ") " + this.username + ": " + this.content;
    }

}
