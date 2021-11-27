package net.hybrid.bungee.data.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private final String host = "176.9.205.152";
    private final String port = "3306";
    private final String database = "s731_networksystems";
    private final String username = "u731_R9f3xkfVgs";
    private final String password = "Aq7^+hQmRPpP9N@SH9i.UAi.";

    private Connection connection;

    public boolean isConnected() {
        return connection != null;
    }

    public void connect() {
        if (!isConnected()) {
            try {
                connection = DriverManager.getConnection("jdbc:mysql://" +
                        host + ":" + port + "/" + database + "?useSSL=false", username, password);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}








