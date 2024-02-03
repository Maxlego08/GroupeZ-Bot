package fr.maxlego08.zsupport.tickets;

public class ChannelInfo {

	private long messageId;
	private long messageAt;

	/**
	 * @return the messageId
	 */
	public long getMessageId() {
		return messageId;
	}

	/**
	 * @return the messageAt
	 */
	public long getMessageAt() {
		return messageAt;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	/**
	 * @param messageAt the messageAt to set
	 */
	public void setMessageAt(long messageAt) {
		this.messageAt = messageAt;
	}

}