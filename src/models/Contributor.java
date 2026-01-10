package models;

import java.sql.Timestamp;

public class Contributor {

    private int contributorId;
    private int projectId;
    private String name;
    private String githubHandle;
    private int commits;
    private int additions;
    private int deletions;
    private Timestamp lastContribution;

    public Contributor(int contributorId, int projectId, String name, String githubHandle,
                       int commits, int additions, int deletions, Timestamp lastContribution) {
        this.contributorId = contributorId;
        this.projectId = projectId;
        this.name = name;
        this.githubHandle = githubHandle;
        this.commits = commits;
        this.additions = additions;
        this.deletions = deletions;
        this.lastContribution = lastContribution;
    }

    public Contributor(int projectId, String name, String githubHandle) {
        this.projectId = projectId;
        this.name = name;
        this.githubHandle = githubHandle;
        this.commits = 0;
        this.additions = 0;
        this.deletions = 0;
    }

    public int getContributorId() { return contributorId; }
    public void setContributorId(int contributorId) { this.contributorId = contributorId; }

    public int getProjectId() { return projectId; }
    public void setProjectId(int projectId) { this.projectId = projectId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGithubHandle() { return githubHandle; }
    public void setGithubHandle(String githubHandle) { this.githubHandle = githubHandle; }

    public int getCommits() { return commits; }
    public void setCommits(int commits) { this.commits = commits; }

    public int getAdditions() { return additions; }
    public void setAdditions(int additions) { this.additions = additions; }

    public int getDeletions() { return deletions; }
    public void setDeletions(int deletions) { this.deletions = deletions; }

    public Timestamp getLastContribution() { return lastContribution; }
    public void setLastContribution(Timestamp lastContribution) { this.lastContribution = lastContribution; }

    public int getNetLines() { return additions - deletions; }
}
