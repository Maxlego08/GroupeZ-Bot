package fr.maxlego08.zsupport.tickets.storage;

import fr.maxlego08.zsupport.tickets.Ticket;

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

public class SqlManager {

    public final ExecutorService service = Executors.newFixedThreadPool(4);
    private final SqlConnection connection = new SqlConnection();

    private final String createRequest = "CREATE TABLE IF NOT EXISTS tickets ( id BIGINT AUTO_INCREMENT PRIMARY KEY, langType VARCHAR(255) NOT NULL, channelId BIGINT NOT NULL, userId BIGINT NOT NULL, ticketStatus VARCHAR(255) NOT NULL, ticketType VARCHAR(255) NOT NULL, pluginId BIGINT, createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, updatedAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);";

    private final String insertRequest = "INSERT INTO tickets (langType, channelId, userId, ticketStatus, ticketType, pluginId) VALUES (?, ?, ?, ?, ?, ?)";

    private final String updateRequest = "UPDATE tickets SET ticketStatus = ?, ticketType = ?, pluginId = ?, updatedAt = NOW() WHERE id = ?";

    public SqlConnection getSqlConnection() {
        return connection;
    }

    public Connection getConnection() {
        return connection.getConnection();
    }

    public void createTable() {
        service.execute(() -> {
            try (PreparedStatement preparedStatement = this.connection.getConnection().prepareStatement(createRequest)) {
                preparedStatement.execute();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }

    public void createTicket(Ticket ticket, Runnable runnable, Runnable errorRunnable) {

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
    }

    public void updateTicket(Ticket ticket) {
        this.service.execute(() -> {
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

    public List<Ticket> selectTicketsNotClosed() {
        String sql = "SELECT * FROM tickets WHERE ticketStatus <> 'CLOSE'";
        List<Ticket> tickets = new ArrayList<>();

        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql); ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                long id = rs.getLong("id");
                String langType = rs.getString("langType");
                long channelId = rs.getLong("channelId");
                long userId = rs.getLong("userId");
                String ticketStatus = rs.getString("ticketStatus");
                String ticketType = rs.getString("ticketType");
                Timestamp createdAt = rs.getTimestamp("createdAt");
                Timestamp updatedAt = rs.getTimestamp("updatedAt");

                // Ticket ticket = new Ticket(id, langType, channelId, userId, ticketStatus, ticketType, createdAt, updatedAt);
                // tickets.add(ticket);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to select tickets from the database.");
        }

        return tickets;
    }


}
