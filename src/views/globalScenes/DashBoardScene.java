package views.globalScenes;

import controllers.DashboardController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.User;
import utils.ThemeManager;
import views.managersScenes.ManageProjectsScene;
import views.managersScenes.ManageIssuesScene;
import views.managersScenes.ViewStatsScene;

public class DashBoardScene {
    private Stage stage;
    private User currentUser;
    private BorderPane mainLayout;
    private Scene scene;
    private VBox mainContent;
    private HBox statsRow;
    private ThemeManager themeManager;

    public DashBoardScene(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.themeManager = ThemeManager.getInstance();
        this.mainLayout = new BorderPane();
        this.scene = createDashboard();
        themeManager.registerScene(scene);
    }

    private Scene createDashboard() {
        SideBarComponent sidebar = new SideBarComponent(currentUser, stage);
        mainLayout.setLeft(sidebar.getSidebar());
        mainContent = createMainContent();
        mainLayout.setCenter(mainContent);
        return new Scene(mainLayout, stage.getWidth(), stage.getHeight());
    }

    private VBox createMainContent() {
        VBox content = new VBox(28);
        content.setPadding(new Insets(32));
        content.setFillWidth(true);
        content.getStyleClass().add("dashboard-content");

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        VBox titleBox = new VBox(4);
        Label titleLabel = new Label("Dashboard");
        titleLabel.getStyleClass().add("dashboard-title");
        Label subtitleLabel = new Label("Overview of your projects and issues");
        subtitleLabel.getStyleClass().add("stats-card-subtitle");
        titleBox.getChildren().addAll(titleLabel, subtitleLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        ComboBox<String> themeSwitcher = createThemeSwitcher();
        topBar.getChildren().addAll(titleBox, spacer, themeSwitcher);

        statsRow = createStatsCards();
        VBox quickActions = createQuickActionsSection();

        content.getChildren().addAll(topBar, statsRow, quickActions);
        return content;
    }

    private ComboBox<String> createThemeSwitcher() {
        ComboBox<String> themeBox = new ComboBox<>();
        themeBox.getItems().addAll("Light", "Dark");
        themeBox.setPrefWidth(100);
        
        String currentTheme = themeManager.getCurrentTheme();
        themeBox.setValue(currentTheme.substring(0, 1).toUpperCase() + currentTheme.substring(1));

        themeBox.setOnAction(e -> {
            String selected = themeBox.getValue().toLowerCase();
            themeManager.changeTheme(selected);
        });

        return themeBox;
    }

    private HBox createStatsCards() {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);

        row.getChildren().addAll(
            createStatsCard("PROJECTS", String.valueOf(DashboardController.getTotalProjects()), "Total repositories"),
            createStatsCard("ACTIVE", String.valueOf(DashboardController.getActiveProjects()), "In development"),
            createStatsCard("OPEN ISSUES", String.valueOf(DashboardController.getOpenIssues()), "Pending resolution"),
            createStatsCard("CRITICAL", String.valueOf(DashboardController.getCriticalIssues()), "High priority")
        );

        if (currentUser.isAdmin()) {
            row.getChildren().add(
                createStatsCard("USERS", String.valueOf(DashboardController.getActiveUsers()), "Team members")
            );
        }

        return row;
    }

    private VBox createStatsCard(String title, String mainValue, String subtitle) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stats-card");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stats-card-title");

        Label valueLabel = new Label(mainValue);
        valueLabel.getStyleClass().add("stats-card-value");

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("stats-card-subtitle");

        card.getChildren().addAll(titleLabel, valueLabel, subtitleLabel);
        return card;
    }

    private VBox createQuickActionsSection() {
        VBox section = new VBox(16);
        section.setPadding(new Insets(8, 0, 0, 0));

        Label sectionTitle = new Label("Quick Actions");
        sectionTitle.getStyleClass().add("section-title");

        HBox actions = new HBox(16);
        actions.setAlignment(Pos.CENTER_LEFT);

        if (currentUser.isAdmin()) {
            actions.getChildren().addAll(
                createQuickActionButton("New Project", "Add a new repository to track", "new-project"),
                createQuickActionButton("Statistics", "View analytics and reports", "view-stats")
            );
        }

        actions.getChildren().addAll(
            createQuickActionButton("New Issue", "Report a bug or request feature", "new-issue"),
            createQuickActionButton("My Issues", "View issues assigned to you", "my-issues")
        );

        section.getChildren().addAll(sectionTitle, actions);
        return section;
    }

    private VBox createQuickActionButton(String title, String description, String type) {
        VBox action = new VBox(8);
        action.getStyleClass().add("quick-action");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("quick-action-title");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("quick-action-desc");

        action.getChildren().addAll(titleLabel, descLabel);

        action.setOnMouseClicked(e -> {
            switch (type) {
                case "new-project" -> new ManageProjectsScene(stage, currentUser).show();
                case "view-stats" -> {
                    stage.setScene(new ViewStatsScene(stage, currentUser).getScene());
                    stage.setTitle("Maintainer - Statistics");
                }
                case "new-issue", "my-issues" -> new ManageIssuesScene(stage, currentUser).show();
            }
        });

        return action;
    }

    public void show() {
        stage.setTitle("Maintainer - " + currentUser.getName());
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public void refreshData() {
        if (statsRow != null && mainContent != null) {
            mainContent.getChildren().remove(statsRow);
            statsRow = createStatsCards();
            mainContent.getChildren().add(1, statsRow);
        }
    }
}