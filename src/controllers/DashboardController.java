package controllers;

import utils.DBHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    public static int getTotalProjects() {
        String query = "SELECT COUNT(*) FROM project";
        return executeCountQuery(query);
    }

    public static int getActiveProjects() {
        String query = "SELECT COUNT(*) FROM project WHERE Status = 'ACTIVE'";
        return executeCountQuery(query);
    }

    public static int getOpenIssues() {
        String query = "SELECT COUNT(*) FROM issue WHERE Status IN ('OPEN', 'IN_PROGRESS')";
        return executeCountQuery(query);
    }

    public static int getCriticalIssues() {
        String query = "SELECT COUNT(*) FROM issue WHERE Priority = 'CRITICAL' AND Status NOT IN ('RESOLVED', 'CLOSED')";
        return executeCountQuery(query);
    }

    public static int getTotalContributors() {
        String query = "SELECT COUNT(DISTINCT ContributorId) FROM contributor";
        return executeCountQuery(query);
    }

    public static int getActiveUsers() {
        String query = "SELECT COUNT(*) FROM user";
        return executeCountQuery(query);
    }

    public static int getResolvedThisWeek() {
        String query = "SELECT COUNT(*) FROM issue WHERE Status IN ('RESOLVED', 'CLOSED') AND ClosedAt >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY)";
        return executeCountQuery(query);
    }

    private static int executeCountQuery(String query) {
        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error executing count query: " + e.getMessage());
        }
        return 0;
    }
}