package fr.maxlego08.zsupport.tickets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import fr.maxlego08.zsupport.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

public class TranscriptManager {

	public static void sendTranscript(Ticket ticket, JDA jda) {

		try {

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(baos);
			System.out.println(ticket.getMessages());
			if (ticket.getMessages().isEmpty()) {
				writer.println("Aucun message !");
			} else {
				ticket.getMessages().stream().map(TicketMessage::toString).forEach(writer::println);
			}
			writer.close();

			InputStream is = new ByteArrayInputStream(baos.toByteArray());

			TextChannel channel = jda.getTextChannelById(Config.ticketLogChannel);

			channel.sendMessage("Transcription du ticket: " + ticket.getName()).addFile(is, ticket.getName() + ".log")
					.queue(m -> {
						System.out.println("Envoie effectué avec succès !");
					});

		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

}
