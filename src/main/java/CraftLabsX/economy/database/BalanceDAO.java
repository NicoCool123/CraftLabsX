package CraftLabsX.economy.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BalanceDAO {

    private final DatabaseManager db;

    public BalanceDAO(DatabaseManager db) {
        this.db = db;
    }

    public void setBalance(String uuid, double balance) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "REPLACE INTO balances (player_uuid, balance) VALUES (?, ?)"
            );
            ps.setString(1, uuid);
            ps.setDouble(2, balance);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getBalance(String uuid) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT balance FROM balances WHERE player_uuid = ?"
            );
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double bal = rs.getDouble("balance");
                rs.close();
                ps.close();
                return bal;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}