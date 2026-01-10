package views.globalScenes;

import models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import views.managersScenes.ManageProjectsScene;
import views.managersScenes.ManageIssuesScene;
import views.managersScenes.ManageUsersScene;
import views.managersScenes.ViewStatsScene;

public class SideBarComponent {

    private VBox sidebar;
    private User currentUser;
    private Stage stage;

    public SideBarComponent(User currentUser, Stage stage) {
        this.currentUser = currentUser;
        this.stage = stage;
        createSidebar();
    }

    private void createSidebar() {
        sidebar = new VBox();
        sidebar.setPrefWidth(240);
        sidebar.setMinWidth(240);
        sidebar.setMaxWidth(240);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setSpacing(4);

        createUserInfoSection();

        Separator separator = new Separator();
        separator.getStyleClass().add("sidebar-separator");
        sidebar.getChildren().add(separator);

        createMenuItems();
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);
        
        createLogoutSection();
    }

    private void createUserInfoSection() {
        VBox userInfo = new VBox(6);
        userInfo.setPadding(new Insets(8, 0, 20, 0));
        userInfo.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("SIGNED IN AS");
        welcomeLabel.getStyleClass().add("sidebar-welcome-label");

        Label userNameLabel = new Label(currentUser.getName());
        userNameLabel.getStyleClass().add("sidebar-username-label");

        Label roleLabel = new Label(currentUser.getRole().toString());
        roleLabel.getStyleClass().add("sidebar-role-label");

        userInfo.getChildren().addAll(welcomeLabel, userNameLabel, roleLabel);
        sidebar.getChildren().add(userInfo);
    }

    private void createMenuItems() {
        VBox menuSection = new VBox(4);
        menuSection.setAlignment(Pos.TOP_LEFT);
        menuSection.setPadding(new Insets(8, 0, 0, 0));

        menuSection.getChildren().add(
            createMenuButton("Dashboard", this::navigateToDashboard)
        );

        Separator sep1 = new Separator();
        sep1.getStyleClass().add("sidebar-separator");
        menuSection.getChildren().add(sep1);

        menuSection.getChildren().addAll(
            createMenuButton("Projects", this::navigateToManageProjects),
            createMenuButton("Issues", this::navigateToManageIssues)
        );
        
        if (currentUser.isAdmin()) {
            Separator sep2 = new Separator();
            sep2.getStyleClass().add("sidebar-separator");
            menuSection.getChildren().add(sep2);
            
            menuSection.getChildren().addAll(
                createMenuButton("Statistics", this::navigateToStats),
                createMenuButton("Users", this::navigateToManageUsers)
            );
        }

        sidebar.getChildren().add(menuSection);
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.getStyleClass().add("sidebar-menu-button");
        button.setOnAction(e -> action.run());
        return button;
    }

    private void createLogoutSection() {
        VBox logoutSection = new VBox(8);
        logoutSection.setPadding(new Insets(0, 0, 8, 0));
        logoutSection.setAlignment(Pos.BOTTOM_LEFT);

        Separator separator = new Separator();
        separator.getStyleClass().add("sidebar-separator");

        Button logoutButton = createMenuButton("Sign Out", this::logout);
        logoutButton.getStyleClass().addAll("sidebar-menu-button", "sidebar-logout-button");

        logoutSection.getChildren().addAll(separator, logoutButton);
        sidebar.getChildren().add(logoutSection);
    }

    private void navigateToDashboard() {
        new DashBoardScene(stage, currentUser).show();
    }

    private void navigateToManageProjects() {
        new ManageProjectsScene(stage, currentUser).show();
    }

    private void navigateToManageIssues() {
        new ManageIssuesScene(stage, currentUser).show();
    }

    private void navigateToStats() {
        stage.setScene(new ViewStatsScene(stage, currentUser).getScene());
        stage.setTitle("Maintainer - Statistics");
        stage.show();
    }

    private void navigateToManageUsers() {
        new ManageUsersScene(stage, currentUser).show();
    }

    private void logout() {
        new LoginView(stage).show();
    }

    public VBox getSidebar() {
        return sidebar;
    }
}
