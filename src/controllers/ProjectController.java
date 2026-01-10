package controllers;

import models.Project;
import utils.DBHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectController {

    public void addProject(Project project) throws SQLException {
        String sql = "INSERT INTO project (Name, Description, RepoUrl, Language, Status, Stars, Forks, OwnerId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getRepoUrl());
            stmt.setString(4, project.getLanguage());
            stmt.setString(5, project.getStatus().name());
            stmt.setInt(6, project.getStars());
            stmt.setInt(7, project.getForks());
            stmt.setInt(8, project.getOwnerId());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    project.setProjectId(rs.getInt(1));
                }
            }
        }
    }

    public List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM project ORDER BY UpdatedAt DESC";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                projects.add(mapResultSetToProject(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching projects: " + e.getMessage());
            e.printStackTrace();
        }

        return projects;
    }

    public List<Project> getProjectsByOwner(int ownerId) {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT * FROM project WHERE OwnerId = ? ORDER BY UpdatedAt DESC";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ownerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projects.add(mapResultSetToProject(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching projects: " + e.getMessage());
            e.printStackTrace();
        }

        return projects;
    }

    public Project getProjectById(int projectId) {
        String sql = "SELECT * FROM project WHERE ProjectId = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProject(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching project: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public void updateProject(Project project) throws SQLException {
        String sql = "UPDATE project SET Name=?, Description=?, RepoUrl=?, Language=?, Status=?, Stars=?, Forks=? WHERE ProjectId=?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, project.getName());
            stmt.setString(2, project.getDescription());
            stmt.setString(3, project.getRepoUrl());
            stmt.setString(4, project.getLanguage());
            stmt.setString(5, project.getStatus().name());
            stmt.setInt(6, project.getStars());
            stmt.setInt(7, project.getForks());
            stmt.setInt(8, project.getProjectId());

            stmt.executeUpdate();
        }
    }

    public void deleteProject(int projectId) throws SQLException {
        String sql = "DELETE FROM project WHERE ProjectId = ?";

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, projectId);
            stmt.executeUpdate();
        }
    }

    public static int getTotalProjects() {
        String sql = "SELECT COUNT(*) FROM project";
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

    public static int getActiveProjects() {
        String sql = "SELECT COUNT(*) FROM project WHERE Status = 'ACTIVE'";
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

    private Project mapResultSetToProject(ResultSet rs) throws SQLException {
        return new Project(
            rs.getInt("ProjectId"),
            rs.getString("Name"),
            rs.getString("Description"),
            rs.getString("RepoUrl"),
            rs.getString("Language"),
            Project.Status.valueOf(rs.getString("Status")),
            rs.getInt("Stars"),
            rs.getInt("Forks"),
            rs.getInt("OwnerId"),
            rs.getTimestamp("CreatedAt"),
            rs.getTimestamp("UpdatedAt")
        );
    }
}
