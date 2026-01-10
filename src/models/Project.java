package models;

import java.sql.Timestamp;

public class Project {

    public enum Status {
        ACTIVE, ARCHIVED, MAINTENANCE
    }

    private int projectId;
    private String name;
    private String description;
    private String repoUrl;
    private String language;
    private Status status;
    private int stars;
    private int forks;
    private int ownerId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Project(int projectId, String name, String description, String repoUrl, 
                   String language, Status status, int stars, int forks, int ownerId,
                   Timestamp createdAt, Timestamp updatedAt) {
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.repoUrl = repoUrl;
        this.language = language;
        this.status = status;
        this.stars = stars;
        this.forks = forks;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Project(String name, String description, String repoUrl, String language, 
                   Status status, int ownerId) {
        this.name = name;
        this.description = description;
        this.repoUrl = repoUrl;
        this.language = language;
        this.status = status;
        this.ownerId = ownerId;
        this.stars = 0;
        this.forks = 0;
    }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRepoUrl() { return repoUrl; }
    public void setRepoUrl(String repoUrl) { this.repoUrl = repoUrl; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public int getStars() { return stars; }
    public void setStars(int stars) { this.stars = stars; }

    public int getForks() { return forks; }
    public void setForks(int forks) { this.forks = forks; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
