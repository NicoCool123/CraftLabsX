package CraftLabsX.economy.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseManager {

    protected Connection connection;

    public abstract void connect();
    public abstract void disconnect();
    public abstract Connection getConnection();
    protected abstract String getAutoIncrementKeyword();

    public void initTables() {
        try {
            String autoIncrement = getAutoIncrementKeyword();

            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS auctions (" +
                            "id INTEGER PRIMARY KEY " + autoIncrement + "," +
                            "seller_uuid VARCHAR(36) NOT NULL," +
                            "seller_name VARCHAR(16)," +
                            "item_data TEXT NOT NULL," +
                            "price DOUBLE NOT NULL," +
                            "created_at BIGINT NOT NULL," +
                            "expiry BIGINT NOT NULL)"
            );

            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS orders (" +
                            "id INTEGER PRIMARY KEY " + autoIncrement + "," +
                            "buyer_uuid VARCHAR(36) NOT NULL," +
                            "buyer_name VARCHAR(16)," +
                            "item_type VARCHAR(64) NOT NULL," +
                            "item_amount INT NOT NULL," +
                            "offered_price DOUBLE NOT NULL," +
                            "created_at BIGINT NOT NULL," +
                            "expiry BIGINT NOT NULL," +
                            "status VARCHAR(20) DEFAULT 'ACTIVE')"
            );

            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS multipliers (" +
                            "player_uuid VARCHAR(36) PRIMARY KEY," +
                            "multiplier DOUBLE DEFAULT 1.0)"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}