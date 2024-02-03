package fr.maxlego08.zsupport.tickets.storage;

import fr.maxlego08.zsupport.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {

    private Connection connection;

    private boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed() && connection.isValid(1);
    }

    public void disconnect() {
        try {
            if (isConnected()) connection.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void connection() {
        try {
            if (!isConnected()) {
                SqlConfiguration configuration = Config.sqlConfiguration;
                String url = "jdbc:mysql://" + configuration.getHost() + ":" + configuration.getPort() + "/" + configuration.getDataBase();
                connection = DriverManager.getConnection(url, configuration.getUser(), configuration.getPassword());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public Connection getConnection() {
        this.connection();
        return connection;
    }
}
