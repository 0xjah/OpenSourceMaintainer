package models;

import java.sql.Timestamp;

public class Issue {

    public enum Status {
        OPEN, IN_PROGRESS, RESOLVED, CLOSED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum Type {
        BUG, FEATURE, DOCS, SECURITY
    }

    private int issueId;
    private int projectId;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private Type type;
    private Integer assigneeId;
    private Integer reporterId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp closedAt;

    public Issue(int issueId, int projectId, String title, String description,
                 Status status, Priority priority, Type type, Integer assigneeId,
                 Integer reporterId, Timestamp createdAt, Timestamp updatedAt, Timestamp closedAt) {
        this.issueId = issueId;
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.type = type;
        this.assigneeId = assigneeId;
        this.reporterId = reporterId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.closedAt = closedAt;
    }

    public Issue(int projectId, String title, String description, Priority priority, Type type, Integer reporterId) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.status = Status.OPEN;
        this.priority = priority;
        this.type = type;
        this.reporterId = reporterId;
    }

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public Integer getAssigneeId() { return assigneeId; }
    public void setAssigneeId(Integer assigneeId) { this.assigneeId = assigneeId; }

    public Integer getReporterId() { return reporterId; }
    public void setReporterId(Integer reporterId) { this.reporterId = reporterId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Timestamp getClosedAt() { return closedAt; }
    public void setClosedAt(Timestamp closedAt) { this.closedAt = closedAt; }
}
