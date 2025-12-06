package CraftLabsX.economy.database;

import CraftLabsX.economy.manager.OrderRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private final DatabaseManager db;

    public OrderDAO(DatabaseManager db) {
        this.db = db;
    }

    public boolean insertOrder(OrderRequest order) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "INSERT INTO orders (buyer_uuid, buyer_name, item_type, item_amount, offered_price, created_at, expiry, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, order.getBuyerUUID());
            ps.setString(2, order.getBuyerName());
            ps.setString(3, order.getItemType());
            ps.setInt(4, order.getItemAmount());
            ps.setDouble(5, order.getOfferedPrice());
            ps.setLong(6, order.getCreatedAt());
            ps.setLong(7, order.getExpiry());
            ps.setString(8, order.getStatus());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public OrderRequest getOrderById(int id) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT * FROM orders WHERE id = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                OrderRequest order = new OrderRequest(
                        rs.getInt("id"),
                        rs.getString("buyer_uuid"),
                        rs.getString("buyer_name"),
                        rs.getString("item_type"),
                        rs.getInt("item_amount"),
                        rs.getDouble("offered_price"),
                        rs.getLong("created_at"),
                        rs.getLong("expiry"),
                        rs.getString("status")
                );
                rs.close();
                ps.close();
                return order;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<OrderRequest> getActiveOrders() {
        List<OrderRequest> list = new ArrayList<>();
        try {
            long now = System.currentTimeMillis();
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT * FROM orders WHERE status = 'ACTIVE' AND expiry > ? ORDER BY created_at DESC"
            );
            ps.setLong(1, now);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new OrderRequest(
                        rs.getInt("id"),
                        rs.getString("buyer_uuid"),
                        rs.getString("buyer_name"),
                        rs.getString("item_type"),
                        rs.getInt("item_amount"),
                        rs.getDouble("offered_price"),
                        rs.getLong("created_at"),
                        rs.getLong("expiry"),
                        rs.getString("status")
                ));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<OrderRequest> getOrdersByBuyer(String uuid) {
        List<OrderRequest> list = new ArrayList<>();
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT * FROM orders WHERE buyer_uuid = ? ORDER BY created_at DESC"
            );
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new OrderRequest(
                        rs.getInt("id"),
                        rs.getString("buyer_uuid"),
                        rs.getString("buyer_name"),
                        rs.getString("item_type"),
                        rs.getInt("item_amount"),
                        rs.getDouble("offered_price"),
                        rs.getLong("created_at"),
                        rs.getLong("expiry"),
                        rs.getString("status")
                ));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateOrderStatus(int id, String status) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "UPDATE orders SET status = ? WHERE id = ?"
            );
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrder(int id) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "DELETE FROM orders WHERE id = ?"
            );
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteExpiredOrders() {
        try {
            long now = System.currentTimeMillis();
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "DELETE FROM orders WHERE expiry <= ? OR status != 'ACTIVE'"
            );
            ps.setLong(1, now);
            int deleted = ps.executeUpdate();
            ps.close();

            if (deleted > 0) {
                System.out.println("[CraftLabsX] Deleted " + deleted + " expired/completed orders.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countPlayerOrders(String uuid) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT COUNT(*) FROM orders WHERE buyer_uuid = ? AND status = 'ACTIVE' AND expiry > ?"
            );
            ps.setString(1, uuid);
            ps.setLong(2, System.currentTimeMillis());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                ps.close();
                return count;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
