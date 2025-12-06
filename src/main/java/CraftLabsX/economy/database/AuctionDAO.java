package CraftLabsX.economy.database;

import CraftLabsX.economy.manager.AuctionListing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuctionDAO {

    private final DatabaseManager db;

    public AuctionDAO(DatabaseManager db) {
        this.db = db;
    }

    public boolean insertAuction(AuctionListing auction) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "INSERT INTO auctions (seller_uuid, seller_name, item_data, price, created_at, expiry) VALUES (?, ?, ?, ?, ?, ?)"
            );
            ps.setString(1, auction.getSellerUUID());
            ps.setString(2, auction.getSellerName());
            ps.setString(3, auction.getItemData());
            ps.setDouble(4, auction.getPrice());
            ps.setLong(5, auction.getCreatedAt());
            ps.setLong(6, auction.getExpiry());
            ps.executeUpdate();
            ps.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public AuctionListing getAuctionById(int id) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT * FROM auctions WHERE id = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                AuctionListing listing = new AuctionListing(
                        rs.getInt("id"),
                        rs.getString("seller_uuid"),
                        rs.getString("seller_name"),
                        rs.getString("item_data"),
                        rs.getDouble("price"),
                        rs.getLong("created_at"),
                        rs.getLong("expiry")
                );
                rs.close();
                ps.close();
                return listing;
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<AuctionListing> getActiveAuctions() {
        List<AuctionListing> list = new ArrayList<>();
        try {
            long now = System.currentTimeMillis();
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT * FROM auctions WHERE expiry > ? ORDER BY created_at DESC"
            );
            ps.setLong(1, now);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new AuctionListing(
                        rs.getInt("id"),
                        rs.getString("seller_uuid"),
                        rs.getString("seller_name"),
                        rs.getString("item_data"),
                        rs.getDouble("price"),
                        rs.getLong("created_at"),
                        rs.getLong("expiry")
                ));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AuctionListing> getPlayerAuctions(String uuid) {
        List<AuctionListing> list = new ArrayList<>();
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT * FROM auctions WHERE seller_uuid = ? ORDER BY created_at DESC"
            );
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new AuctionListing(
                        rs.getInt("id"),
                        rs.getString("seller_uuid"),
                        rs.getString("seller_name"),
                        rs.getString("item_data"),
                        rs.getDouble("price"),
                        rs.getLong("created_at"),
                        rs.getLong("expiry")
                ));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AuctionListing> getAuctionsSortedBy(String sortType) {
        List<AuctionListing> list = new ArrayList<>();
        String orderBy;

        switch (sortType.toUpperCase()) {
            case "CHEAPEST":
                orderBy = "price ASC";
                break;
            case "EXPENSIVE":
                orderBy = "price DESC";
                break;
            case "NEWEST":
                orderBy = "created_at DESC";
                break;
            case "OLDEST":
                orderBy = "created_at ASC";
                break;
            default:
                orderBy = "created_at DESC";
        }

        try {
            long now = System.currentTimeMillis();
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT * FROM auctions WHERE expiry > ? ORDER BY " + orderBy
            );
            ps.setLong(1, now);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new AuctionListing(
                        rs.getInt("id"),
                        rs.getString("seller_uuid"),
                        rs.getString("seller_name"),
                        rs.getString("item_data"),
                        rs.getDouble("price"),
                        rs.getLong("created_at"),
                        rs.getLong("expiry")
                ));
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteAuction(int id) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "DELETE FROM auctions WHERE id = ?"
            );
            ps.setInt(1, id);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteExpiredAuctions() {
        try {
            long now = System.currentTimeMillis();
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "DELETE FROM auctions WHERE expiry <= ?"
            );
            ps.setLong(1, now);
            int deleted = ps.executeUpdate();
            ps.close();

            if (deleted > 0) {
                System.out.println("[CraftLabsX] Deleted " + deleted + " expired auctions.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int countPlayerAuctions(String uuid) {
        try {
            PreparedStatement ps = db.getConnection().prepareStatement(
                    "SELECT COUNT(*) FROM auctions WHERE seller_uuid = ? AND expiry > ?"
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
