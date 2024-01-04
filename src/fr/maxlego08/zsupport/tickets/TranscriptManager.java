package fr.maxlego08.zsupport.tickets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;

import fr.maxlego08.zsupport.Config;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.utils.FileUpload;

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
			
			FileUpload fileUpload = FileUpload.fromData(is, ticket.getName() + ".log");
			channel.sendMessage("Transcription du ticket: " + ticket.getName()).addFiles(fileUpload)
					.queue(m -> {
						System.out.println("Envoie effectué avec succès !");
					});

		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

}
