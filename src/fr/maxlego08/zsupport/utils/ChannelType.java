package fr.maxlego08.zsupport.utils;

public enum ChannelType {

		FREE("Community support", "You are in a community support channel for a free plugin.\nPlease check if someone hasn't already asked your question in the channel.\nPlease don't **mention the staff**. Please wait for an answer."), 
		
		GENERAL("How to get help ?", "Do you need help ? Please create a %s and do not request help here. You can ask your questions before a purchase here."),

		;

		private final String title;
		private final String description;

		/**
		 * @param title
		 * @param description
		 */
		private ChannelType(String title, String description) {
			this.title = title;
			this.description = description;
		}

		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}

		/**
		 * @return the description
		 */
		public String getDescription() {
			return description;
		}

	}