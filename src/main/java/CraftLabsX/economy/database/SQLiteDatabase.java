package CraftLabsX.economy.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase extends DatabaseManager {

    private final File dataFolder;

    public SQLiteDatabase(File dataFolder) {
        this.dataFolder = dataFolder;
    }

    @Override
    public void connect() {
        try {
            if (!dataFolder.exists()) dataFolder.mkdirs();
            File dbFile = new File(dataFolder, "economy.db");
            if (!dbFile.exists()) dbFile.createNewFile();

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
            System.out.println("[CraftLabsX] Connected to SQLite!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[CraftLabsX] SQLite connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    protected String getAutoIncrementKeyword() {
        return "AUTOINCREMENT";
    }
}