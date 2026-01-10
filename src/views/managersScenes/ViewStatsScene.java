package views.managersScenes;

import controllers.StatisticsController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.User;
import utils.ThemeManager;
import views.globalScenes.SideBarComponent;

public class ViewStatsScene {
    private Stage stage;
    private User currentUser;
    private Scene scene;
    private StatisticsController statsController;
    private TableView<StatisticsController.IssueSummary> issueTable;
    private TableView<StatisticsController.ProjectSummary> projectTable;

    public ViewStatsScene(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.statsController = new StatisticsController();
        createScene();
    }

    private void createScene() {
        BorderPane root = new BorderPane();
        
        SideBarComponent sidebar = new SideBarComponent(currentUser, stage);
        root.setLeft(sidebar.getSidebar());
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("dashboard-content");
        
        Label title = new Label("Statistics");
        title.getStyleClass().add("dashboard-title");
        
        HBox filterBar = createFilterBar();
        
        Label issueLabel = new Label("Issue Summary");
        issueLabel.getStyleClass().add("section-title");
        issueTable = statsController.createIssueSummaryTable();
        issueTable.setPrefHeight(150);
        
        Label projectLabel = new Label("Project Overview");
        projectLabel.getStyleClass().add("section-title");
        projectTable = statsController.createProjectSummaryTable();
        VBox.setVgrow(projectTable, Priority.ALWAYS);
        
        content.getChildren().addAll(title, filterBar, issueLabel, issueTable, projectLabel, projectTable);
        root.setCenter(content);
        
        scene = new Scene(root, stage.getWidth(), stage.getHeight());
        ThemeManager.getInstance().registerScene(scene);
        
        loadStats("All Time");
    }

    private HBox createFilterBar() {
        HBox bar = new HBox(8);
        bar.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Period:");
        
        ComboBox<String> periodBox = new ComboBox<>();
        periodBox.getItems().addAll("Today", "This Week", "This Month", "All Time");
        periodBox.setValue("All Time");
        periodBox.setOnAction(e -> loadStats(periodBox.getValue()));
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadStats(periodBox.getValue()));
        
        bar.getChildren().addAll(filterLabel, periodBox, refreshBtn);
        return bar;
    }

    private void loadStats(String period) {
        issueTable.setItems(statsController.getIssueSummary(period));
        projectTable.setItems(statsController.getProjectSummaries());
    }

    public Scene getScene() {
        return scene;
    }
}
