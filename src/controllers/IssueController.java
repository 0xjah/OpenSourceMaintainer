package controllers;

import models.Issue;
import utils.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueController {

    public static void addIssue(Issue issue) throws SQLException {
        String sql = "INSERT INTO issue (ProjectId, Title, Description, Status, Priority, Type, AssigneeId, ReporterId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, issue.getProjectId());
            stmt.setString(2, issue.getTitle());
            stmt.setString(3, issue.getDescription());
            stmt.setString(4, issue.getStatus().name());
            stmt.setString(5, issue.getPriority().name());
            stmt.setString(6, issue.getType().name());
            
            if (issue.getAssigneeId() != null) {
                stmt.setInt(7, issue.getAssigneeId());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            if (issue.getReporterId() != null) {
                stmt.setInt(8, issue.getReporterId());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    issue.setIssueId(rs.getInt(1));
                }
            }
        }
    }

    public static List<Issue> getAllIssues() {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT * FROM issue ORDER BY CreatedAt DESC";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                issues.add(mapResultSetToIssue(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching issues: " + e.getMessage());
            e.printStackTrace();
        }

        return issues;
    }

    public static List<Issue> getIssuesByProject(int projectId) {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT * FROM issue WHERE ProjectId = ? ORDER BY Priority DESC, CreatedAt DESC";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    issues.add(mapResultSetToIssue(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching issues: " + e.getMessage());
            e.printStackTrace();
        }

        return issues;
    }

    public static List<Issue> getIssuesByAssignee(int assigneeId) {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT * FROM issue WHERE AssigneeId = ? ORDER BY Priority DESC, CreatedAt DESC";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assigneeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    issues.add(mapResultSetToIssue(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching issues: " + e.getMessage());
            e.printStackTrace();
        }

        return issues;
    }

    public static Issue getIssueById(int issueId) {
        String sql = "SELECT * FROM issue WHERE IssueId = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, issueId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToIssue(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching issue: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public static void updateIssue(Issue issue) throws SQLException {
        String sql = "UPDATE issue SET Title=?, Description=?, Status=?, Priority=?, Type=?, AssigneeId=? WHERE IssueId=?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, issue.getTitle());
            stmt.setString(2, issue.getDescription());
            stmt.setString(3, issue.getStatus().name());
            stmt.setString(4, issue.getPriority().name());
            stmt.setString(5, issue.getType().name());
            
            if (issue.getAssigneeId() != null) {
                stmt.setInt(6, issue.getAssigneeId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }
            
            stmt.setInt(7, issue.getIssueId());

            stmt.executeUpdate();
        }
    }

    public static void updateIssueStatus(int issueId, Issue.Status status) throws SQLException {
        String sql = "UPDATE issue SET Status=?, ClosedAt=? WHERE IssueId=?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());
            
            if (status == Issue.Status.CLOSED || status == Issue.Status.RESOLVED) {
                stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            
            stmt.setInt(3, issueId);

            stmt.executeUpdate();
        }
    }

    public static void deleteIssue(int issueId) throws SQLException {
        String sql = "DELETE FROM issue WHERE IssueId = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, issueId);
            stmt.executeUpdate();
        }
    }

    public static int getOpenIssuesCount() {
        String sql = "SELECT COUNT(*) FROM issue WHERE Status IN ('OPEN', 'IN_PROGRESS')";
        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCriticalIssuesCount() {
        String sql = "SELECT COUNT(*) FROM issue WHERE Priority = 'CRITICAL' AND Status NOT IN ('RESOLVED', 'CLOSED')";
        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static Issue mapResultSetToIssue(ResultSet rs) throws SQLException {
        Integer assigneeId = rs.getInt("AssigneeId");
        if (rs.wasNull()) assigneeId = null;
        
        Integer reporterId = rs.getInt("ReporterId");
        if (rs.wasNull()) reporterId = null;

        return new Issue(
            rs.getInt("IssueId"),
            rs.getInt("ProjectId"),
            rs.getString("Title"),
            rs.getString("Description"),
            Issue.Status.valueOf(rs.getString("Status")),
            Issue.Priority.valueOf(rs.getString("Priority")),
            Issue.Type.valueOf(rs.getString("Type")),
            assigneeId,
            reporterId,
            rs.getTimestamp("CreatedAt"),
            rs.getTimestamp("UpdatedAt"),
            rs.getTimestamp("ClosedAt")
        );
    }
}
