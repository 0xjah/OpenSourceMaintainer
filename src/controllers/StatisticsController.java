package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import utils.DBHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class StatisticsController {

    public static class IssueSummary {
        private String period;
        private int opened;
        private int resolved;
        private int critical;

        public IssueSummary(String period, int opened, int resolved, int critical) {
            this.period = period;
            this.opened = opened;
            this.resolved = resolved;
            this.critical = critical;
        }

        public String getPeriod() { return period; }
        public int getOpened() { return opened; }
        public int getResolved() { return resolved; }
        public int getCritical() { return critical; }
    }

    public static class ProjectSummary {
        private String name;
        private int openIssues;
        private int contributors;
        private String status;

        public ProjectSummary(String name, int openIssues, int contributors, String status) {
            this.name = name;
            this.openIssues = openIssues;
            this.contributors = contributors;
            this.status = status;
        }

        public String getName() { return name; }
        public int getOpenIssues() { return openIssues; }
        public int getContributors() { return contributors; }
        public String getStatus() { return status; }
    }

    public TableView<IssueSummary> createIssueSummaryTable() {
        TableView<IssueSummary> table = new TableView<>();

        TableColumn<IssueSummary, String> periodCol = new TableColumn<>("Period");
        periodCol.setCellValueFactory(new PropertyValueFactory<>("period"));
        periodCol.setPrefWidth(150);

        TableColumn<IssueSummary, Integer> openedCol = new TableColumn<>("Opened");
        openedCol.setCellValueFactory(new PropertyValueFactory<>("opened"));
        openedCol.setPrefWidth(100);

        TableColumn<IssueSummary, Integer> resolvedCol = new TableColumn<>("Resolved");
        resolvedCol.setCellValueFactory(new PropertyValueFactory<>("resolved"));
        resolvedCol.setPrefWidth(100);

        TableColumn<IssueSummary, Integer> criticalCol = new TableColumn<>("Critical");
        criticalCol.setCellValueFactory(new PropertyValueFactory<>("critical"));
        criticalCol.setPrefWidth(100);

        table.getColumns().addAll(periodCol, openedCol, resolvedCol, criticalCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    public TableView<ProjectSummary> createProjectSummaryTable() {
        TableView<ProjectSummary> table = new TableView<>();

        TableColumn<ProjectSummary, String> nameCol = new TableColumn<>("Project");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<ProjectSummary, Integer> issuesCol = new TableColumn<>("Open Issues");
        issuesCol.setCellValueFactory(new PropertyValueFactory<>("openIssues"));
        issuesCol.setPrefWidth(100);

        TableColumn<ProjectSummary, Integer> contribCol = new TableColumn<>("Contributors");
        contribCol.setCellValueFactory(new PropertyValueFactory<>("contributors"));
        contribCol.setPrefWidth(100);

        TableColumn<ProjectSummary, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        table.getColumns().addAll(nameCol, issuesCol, contribCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    public ObservableList<IssueSummary> getIssueSummary(String range) {
        ObservableList<IssueSummary> summaries = FXCollections.observableArrayList();
        String openedQuery = "";
        String resolvedQuery = "";
        String criticalQuery = "";

        switch (range) {
            case "Today" -> {
                openedQuery = "SELECT COUNT(*) FROM issue WHERE DATE(CreatedAt) = CURDATE()";
                resolvedQuery = "SELECT COUNT(*) FROM issue WHERE DATE(ClosedAt) = CURDATE()";
                criticalQuery = "SELECT COUNT(*) FROM issue WHERE Priority = 'CRITICAL' AND DATE(CreatedAt) = CURDATE()";
            }
            case "This Week" -> {
                openedQuery = "SELECT COUNT(*) FROM issue WHERE CreatedAt >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
                resolvedQuery = "SELECT COUNT(*) FROM issue WHERE ClosedAt >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
                criticalQuery = "SELECT COUNT(*) FROM issue WHERE Priority = 'CRITICAL' AND CreatedAt >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
            }
            case "This Month" -> {
                openedQuery = "SELECT COUNT(*) FROM issue WHERE YEAR(CreatedAt) = YEAR(CURDATE()) AND MONTH(CreatedAt) = MONTH(CURDATE())";
                resolvedQuery = "SELECT COUNT(*) FROM issue WHERE YEAR(ClosedAt) = YEAR(CURDATE()) AND MONTH(ClosedAt) = MONTH(CURDATE())";
                criticalQuery = "SELECT COUNT(*) FROM issue WHERE Priority = 'CRITICAL' AND YEAR(CreatedAt) = YEAR(CURDATE()) AND MONTH(CreatedAt) = MONTH(CURDATE())";
            }
            case "All Time" -> {
                openedQuery = "SELECT COUNT(*) FROM issue";
                resolvedQuery = "SELECT COUNT(*) FROM issue WHERE Status IN ('RESOLVED', 'CLOSED')";
                criticalQuery = "SELECT COUNT(*) FROM issue WHERE Priority = 'CRITICAL'";
            }
            default -> {
                return summaries;
            }
        }

        try (Connection conn = DBHelper.connect()) {
            int opened = executeCount(conn, openedQuery);
            int resolved = executeCount(conn, resolvedQuery);
            int critical = executeCount(conn, criticalQuery);
            summaries.add(new IssueSummary(range, opened, resolved, critical));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return summaries;
    }

    public ObservableList<ProjectSummary> getProjectSummaries() {
        ObservableList<ProjectSummary> summaries = FXCollections.observableArrayList();
        String query = """
            SELECT p.Name, p.Status,
                   (SELECT COUNT(*) FROM issue i WHERE i.ProjectId = p.ProjectId AND i.Status IN ('OPEN', 'IN_PROGRESS')) as open_issues,
                   (SELECT COUNT(*) FROM contributor c WHERE c.ProjectId = p.ProjectId) as contributors
            FROM project p
            ORDER BY open_issues DESC
            """;

        try (Connection conn = DBHelper.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                summaries.add(new ProjectSummary(
                    rs.getString("Name"),
                    rs.getInt("open_issues"),
                    rs.getInt("contributors"),
                    rs.getString("Status")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return summaries;
    }

    private int executeCount(Connection conn, String query) throws Exception {
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
}
