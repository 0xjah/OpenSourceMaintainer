package controllers;

import models.User;
import models.User.Role;
import utils.DBHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserController {

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT UserId, Name, Role, Password, Username, Email FROM user";

        try (Connection conn = DBHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException ex) {
            System.err.println("Error fetching users: " + ex.getMessage());
            ex.printStackTrace();
        }

        return users;
    }


    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO user (Name, Role, Password, Username, Email) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getRole().name());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getEmail());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE user SET Name = ?, Role = ?, Password = ?, Username = ?, Email = ? WHERE UserId = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getName());
            ps.setString(2, user.getRole().name());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getEmail());
            ps.setInt(6, user.getId());

            ps.executeUpdate();
        }
    }

    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM user WHERE UserId = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    public static User authenticate(String username, String password) {
        String sql = "SELECT * FROM user WHERE Username = ? AND Password = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Authentication failed: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("UserId"),
                rs.getString("Name"),
                Role.valueOf(rs.getString("Role").toUpperCase()),
                rs.getString("Password"),
                rs.getString("Username"),
                rs.getString("Email")
        );
    }
}
