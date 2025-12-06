package CraftLabsX.economy.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MultiplierDAO {

    private final DatabaseManager db;

    public MultiplierDAO(DatabaseManager db) {
        this.db = db;
    }

    public void setMultiplier(String uuid, double multiplier) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "REPLACE INTO multipliers (player_uuid, multiplier) VALUES (?, ?)"
            );
            ps.setString(1, uuid);
            ps.setDouble(2, multiplier);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public double getMultiplier(String uuid) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT multiplier FROM multipliers WHERE player_uuid = ?"
            );
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double m = rs.getDouble("multiplier");
                rs.close();
                ps.close();
                return m;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1.0;
    }
}