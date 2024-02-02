package fr.maxlego08.zsupport.tickets.storage;

import fr.maxlego08.zsupport.faq.Faq;
import fr.maxlego08.zsupport.lang.LangType;
import fr.maxlego08.zsupport.tickets.Ticket;
import fr.maxlego08.zsupport.tickets.TicketStatus;
import fr.maxlego08.zsupport.tickets.TicketType;
import net.dv8tion.jda.api.entities.Message;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SqlManager {

    public static final ExecutorService service = Executors.newFixedThreadPool(4);
    private final SqlConnection connection = new SqlConnection();

    private final String createRequest = "CREATE TABLE IF NOT EXISTS tickets ( id BIGINT AUTO_INCREMENT PRIMARY KEY, langType VARCHAR(255) NOT NULL, channelId BIGINT NOT NULL, userId BIGINT NOT NULL, ticketStatus VARCHAR(255) NOT NULL, ticketType VARCHAR(255) NOT NULL, pluginId BIGINT, createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);";
    private final String createMessageRequest = "CREATE TABLE IF NOT EXISTS ticket_messages (id BIGINT AUTO_INCREMENT PRIMARY KEY, ticketId BIGINT NOT NULL, messageId BIGINT NOT NULL, userId BIGINT NOT NULL, messageText TEXT NOT NULL, createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (ticketId) REFERENCES tickets(id) ON DELETE CASCADE ON UPDATE CASCADE);";
    private final String createPluginRequest = "CREATE TABLE IF NOT EXISTS ticket_plugins (id BIGINT AUTO_INCREMENT PRIMARY KEY, ticketId BIGINT NOT NULL, pluginVersion VARCHAR(255) NOT NULL,  isLastVersion BOOLEAN NOT NULL, createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (ticketId) REFERENCES tickets(id) ON DELETE CASCADE);";
    private final String createAttachementRequest = "CREATE TABLE IF NOT EXISTS ticket_attachments (id BIGINT AUTO_INCREMENT PRIMARY KEY, ticketId BIGINT NOT NULL, messageId BIGINT NOT NULL, fileContent LONGBLOB NOT NULL, createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (ticketId) REFERENCES tickets(id) ON DELETE CASCADE);";
    private final String createFAQRequest = "CREATE TABLE IF NOT EXISTS ticket_faqs (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255) NOT NULL, title VARCHAR(255) NOT NULL, answer TEXT NOT NULL);";
    private final String insertRequest = "INSERT INTO tickets (langType, channelId, userId, ticketStatus, ticketType, pluginId) VALUES (?, ?, ?, ?, ?, ?)";
    private final String insertMessageRequest = "INSERT INTO ticket_messages (ticketId, messageId, userId, messageText) VALUES (?, ?, ?, ?)";
    private final String updateRequest = "UPDATE tickets SET ticketStatus = ?, ticketType = ?, pluginId = ?, updatedAt = NOW() WHERE id = ?";

    public SqlConnection getSqlConnection() {
        return connection;
    }

    public Connection getConnection() {
        return connection.getConnection();
    }

    public void createTable(Consumer<List<Ticket>> consumer) {
        service.execute(() -> {

            create(createRequest);
            create(createMessageRequest);
            create(createPluginRequest);
            create(createAttachementRequest);
            create(createFAQRequest);

            selectTicketsNotClosed(consumer);
        });
    }

    private void create(String sql) {
        try (PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void createTicket(Ticket ticket, Runnable runnable, Runnable errorRunnable) {
        service.execute(() -> {
            try (Connection connection = getConnection()) {

                PreparedStatement preparedStatement = connection.prepareStatement(insertRequest, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, ticket.getLangType().name());
                preparedStatement.setLong(2, ticket.getChannelId());
                preparedStatement.setLong(3, ticket.getUserId());
                preparedStatement.setString(4, ticket.getTicketStatus().name());
                preparedStatement.setString(5, ticket.getTicketType().name());
                preparedStatement.setLong(6, ticket.getPluginId());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            long ticketId = generatedKeys.getLong(1);
                            ticket.setId(ticketId);
                            runnable.run();
                        }
                    }
                    System.out.println("A new ticket has been inserted successfully.");
                } else {
                    System.out.println("A problem occurred while inserting a new ticket.");
                    errorRunnable.run();
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to insert a new ticket into the database.");
                errorRunnable.run();
            }
        });
    }

    public void updateTicket(Ticket ticket) {
        ticket.update();
        service.execute(() -> {
            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(updateRequest)) {

                preparedStatement.setString(1, ticket.getTicketStatus().name());
                preparedStatement.setString(2, ticket.getTicketType().name());
                preparedStatement.setLong(3, ticket.getPluginId());
                preparedStatement.setLong(4, ticket.getId());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Ticket updated successfully.");
                } else {
                    System.out.println("Could not update the ticket.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to update the ticket in the database.");
            }
        });
    }

    public void selectTicketsNotClosed(Consumer<List<Ticket>> consumer) {
        service.execute(() -> {
            String sql = "SELECT * FROM tickets WHERE ticketStatus <> 'CLOSE'";
            List<Ticket> tickets = new ArrayList<>();

            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String langType = resultSet.getString("langType");
                    long channelId = resultSet.getLong("channelId");
                    long userId = resultSet.getLong("userId");
                    long pluginId = resultSet.getLong("pluginId");
                    String ticketStatus = resultSet.getString("ticketStatus");
                    String ticketType = resultSet.getString("ticketType");
                    Timestamp createdAt = resultSet.getTimestamp("createdAt");
                    Timestamp updatedAt = resultSet.getTimestamp("updatedAt");

                    Ticket ticket = new Ticket(id, LangType.valueOf(langType), channelId, userId, createdAt.getTime(), updatedAt.getTime(), TicketStatus.valueOf(ticketStatus), TicketType.valueOf(ticketType), pluginId);
                    tickets.add(ticket);
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
                System.out.println("Failed to select tickets from the database.");
            }

            consumer.accept(tickets);
        });
    }

    public void addMessageToTicket(Ticket ticket, Message message) {
        service.execute(() -> {
            try (Connection connection = getConnection()) {

                PreparedStatement preparedStatement = connection.prepareStatement(insertMessageRequest);
                preparedStatement.setLong(1, ticket.getId());
                preparedStatement.setLong(2, message.getIdLong());
                preparedStatement.setLong(3, message.getAuthor().getIdLong());
                preparedStatement.setString(4, message.getContentRaw());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("A new message has been added successfully to ticket ID: " + ticket.getId());
                } else {
                    System.out.println("A problem occurred while adding a new message to the ticket.");
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
                System.out.println("Failed to add a new message to the ticket in the database.");
            }
        });
    }

    public void insertPluginForTicket(long ticketId, String pluginVersion, boolean isLastVersion) {
        service.execute(() -> {
            String sql = "INSERT INTO ticket_plugins (ticketId, pluginVersion, isLastVersion) VALUES (?, ?, ?)";

            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setLong(1, ticketId);
                preparedStatement.setString(2, pluginVersion);
                preparedStatement.setBoolean(3, isLastVersion);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("A new plugin entry has been added successfully for ticket ID: " + ticketId);
                } else {
                    System.out.println("A problem occurred while adding a new plugin entry for the ticket.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Failed to add a new plugin entry for the ticket in the database.");
            }
        });
    }

    public void addAttachment(long ticketId, long messageId, InputStream fileContent) {
        service.execute(() -> {
            String sqlRequest = "INSERT INTO ticket_attachments (ticketId, messageId, fileContent) VALUES (?, ?, ?)";
            try (Connection connection = getConnection()) {

                PreparedStatement preparedStatement = connection.prepareStatement(sqlRequest);
                preparedStatement.setLong(1, ticketId);
                preparedStatement.setLong(2, messageId);
                preparedStatement.setBinaryStream(3, fileContent);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Attachment added successfully to ticket ID: " + messageId);
                } else {
                    System.out.println("Failed to add the attachment to the ticket.");
                }
            } catch (SQLException exception) {
                exception.printStackTrace();
                System.out.println("Error occurred while adding the attachment.");
            }
        });
    }

    public void addFaq(String faqName, String faqAnswer) {
        service.execute(() -> {
            try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO faqs (faqName, faqAnswer) VALUES (?, ?)")) {
                preparedStatement.setString(1, faqName);
                preparedStatement.setString(2, faqAnswer);
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    public List<Faq> getAllFaqs() {
        List<Faq> faqs = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ticket_faqs");
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Faq faq = new Faq(resultSet.getLong("id"), resultSet.getString("name"), resultSet.getString("title"), resultSet.getString("answer"));
                faqs.add(faq);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return faqs;
    }

}
