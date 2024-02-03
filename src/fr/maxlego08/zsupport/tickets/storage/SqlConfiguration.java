package fr.maxlego08.zsupport.tickets.storage;

public class SqlConfiguration {

    private final String user;
    private final String password;
    private final String host;
    private final String dataBase;
    private final int port;

    public SqlConfiguration(String user, String password, String host, String dataBase, int port) {
        this.user = user;
        this.password = password;
        this.host = host;
        this.dataBase = dataBase;
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public String getDataBase() {
        return dataBase;
    }

    public int getPort() {
        return port;
    }
}
