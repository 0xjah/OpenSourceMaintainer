package controllers;

import models.Contributor;
import utils.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContributorController {

    public static void addContributor(Contributor contributor) throws SQLException {
        String sql = "INSERT INTO contributor (ProjectId, Name, GithubHandle, Commits, Additions, Deletions) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, contributor.getProjectId());
            stmt.setString(2, contributor.getName());
            stmt.setString(3, contributor.getGithubHandle());
            stmt.setInt(4, contributor.getCommits());
            stmt.setInt(5, contributor.getAdditions());
            stmt.setInt(6, contributor.getDeletions());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    contributor.setContributorId(rs.getInt(1));
                }
            }
        }
    }

    public static List<Contributor> getContributorsByProject(int projectId) {
        List<Contributor> contributors = new ArrayList<>();
        String sql = "SELECT * FROM contributor WHERE ProjectId = ? ORDER BY Commits DESC";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    contributors.add(mapResultSetToContributor(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching contributors: " + e.getMessage());
            e.printStackTrace();
        }

        return contributors;
    }

    public static void updateContributor(Contributor contributor) throws SQLException {
        String sql = "UPDATE contributor SET Name=?, GithubHandle=?, Commits=?, Additions=?, Deletions=?, LastContribution=? WHERE ContributorId=?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, contributor.getName());
            stmt.setString(2, contributor.getGithubHandle());
            stmt.setInt(3, contributor.getCommits());
            stmt.setInt(4, contributor.getAdditions());
            stmt.setInt(5, contributor.getDeletions());
            stmt.setTimestamp(6, contributor.getLastContribution());
            stmt.setInt(7, contributor.getContributorId());

            stmt.executeUpdate();
        }
    }

    public static void deleteContributor(int contributorId) throws SQLException {
        String sql = "DELETE FROM contributor WHERE ContributorId = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contributorId);
            stmt.executeUpdate();
        }
    }

    public static int getTotalContributors() {
        String sql = "SELECT COUNT(DISTINCT ContributorId) FROM contributor";
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

    private static Contributor mapResultSetToContributor(ResultSet rs) throws SQLException {
        return new Contributor(
            rs.getInt("ContributorId"),
            rs.getInt("ProjectId"),
            rs.getString("Name"),
            rs.getString("GithubHandle"),
            rs.getInt("Commits"),
            rs.getInt("Additions"),
            rs.getInt("Deletions"),
            rs.getTimestamp("LastContribution")
        );
    }
}
