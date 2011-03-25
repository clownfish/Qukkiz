package nl.blaatz0r.Trivia;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private Connection connection = null;
    private static boolean connected = false;

    public Database() {
    }

    public static boolean isConnected() {
        return connected;
    }

    public Connection getConnection() {
        return connection;
    }

    public void init() {
        try {
            Connection conn = getConnection();
            Statement stat = conn.createStatement();
            stat.executeUpdate("CREATE TABLE IF NOT EXISTS scores (name, score);");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean connect(File database) {
        if (connection != null) {
            return true;
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + database.getAbsolutePath());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        connected = true;

        return true;
    }
}