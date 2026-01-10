package views.managersScenes;

import controllers.ProjectController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Project;
import models.User;
import utils.ThemeManager;
import views.globalScenes.SideBarComponent;

public class ManageProjectsScene {
    private Stage stage;
    private User currentUser;
    private Scene scene;
    private TableView<Project> projectTable;
    private ProjectController projectController;

    public ManageProjectsScene(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        this.projectController = new ProjectController();
        createScene();
    }

    private void createScene() {
        BorderPane root = new BorderPane();
        
        SideBarComponent sidebar = new SideBarComponent(currentUser, stage);
        root.setLeft(sidebar.getSidebar());
        
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("dashboard-content");
        
        Label title = new Label("Projects");
        title.getStyleClass().add("dashboard-title");
        
        HBox toolbar = createToolbar();
        projectTable = createProjectTable();
        VBox.setVgrow(projectTable, Priority.ALWAYS);
        
        content.getChildren().addAll(title, toolbar, projectTable);
        root.setCenter(content);
        
        scene = new Scene(root, stage.getWidth(), stage.getHeight());
        ThemeManager.getInstance().registerScene(scene);
        
        loadProjects();
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(8);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Button addBtn = new Button("Add Project");
        addBtn.setOnAction(e -> showProjectDialog(null));
        
        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            Project selected = projectTable.getSelectionModel().getSelectedItem();
            if (selected != null) showProjectDialog(selected);
        });
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> deleteSelectedProject());
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadProjects());
        
        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
        return toolbar;
    }

    private TableView<Project> createProjectTable() {
        TableView<Project> table = new TableView<>();
        
        TableColumn<Project, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Project, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<Project, String> langCol = new TableColumn<>("Language");
        langCol.setCellValueFactory(new PropertyValueFactory<>("language"));
        langCol.setPrefWidth(100);
        
        TableColumn<Project, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Project, Integer> starsCol = new TableColumn<>("Stars");
        starsCol.setCellValueFactory(new PropertyValueFactory<>("stars"));
        starsCol.setPrefWidth(70);
        
        TableColumn<Project, Integer> forksCol = new TableColumn<>("Forks");
        forksCol.setCellValueFactory(new PropertyValueFactory<>("forks"));
        forksCol.setPrefWidth(70);
        
        TableColumn<Project, String> repoCol = new TableColumn<>("Repository");
        repoCol.setCellValueFactory(new PropertyValueFactory<>("repoUrl"));
        repoCol.setPrefWidth(250);
        
        table.getColumns().addAll(idCol, nameCol, langCol, statusCol, starsCol, forksCol, repoCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        return table;
    }

    private void loadProjects() {
        ObservableList<Project> projects = FXCollections.observableArrayList(
            projectController.getAllProjects()
        );
        projectTable.setItems(projects);
    }

    private void showProjectDialog(Project project) {
        Dialog<Project> dialog = new Dialog<>();
        dialog.setTitle(project == null ? "Add Project" : "Edit Project");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        TextField nameField = new TextField(project != null ? project.getName() : "");
        TextArea descField = new TextArea(project != null ? project.getDescription() : "");
        descField.setPrefRowCount(3);
        TextField repoField = new TextField(project != null ? project.getRepoUrl() : "");
        TextField langField = new TextField(project != null ? project.getLanguage() : "");
        ComboBox<Project.Status> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(Project.Status.values());
        statusBox.setValue(project != null ? project.getStatus() : Project.Status.ACTIVE);
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descField, 1, 1);
        grid.add(new Label("Repository URL:"), 0, 2);
        grid.add(repoField, 1, 2);
        grid.add(new Label("Language:"), 0, 3);
        grid.add(langField, 1, 3);
        grid.add(new Label("Status:"), 0, 4);
        grid.add(statusBox, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (project != null) {
                    project.setName(nameField.getText());
                    project.setDescription(descField.getText());
                    project.setRepoUrl(repoField.getText());
                    project.setLanguage(langField.getText());
                    project.setStatus(statusBox.getValue());
                    return project;
                } else {
                    return new Project(
                        nameField.getText(),
                        descField.getText(),
                        repoField.getText(),
                        langField.getText(),
                        statusBox.getValue(),
                        currentUser.getId()
                    );
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(p -> {
            try {
                if (project != null) {
                    projectController.updateProject(p);
                } else {
                    projectController.addProject(p);
                }
                loadProjects();
            } catch (Exception e) {
                showError("Error saving project: " + e.getMessage());
            }
        });
    }

    private void deleteSelectedProject() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Delete project: " + selected.getName() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    projectController.deleteProject(selected.getProjectId());
                    loadProjects();
                } catch (Exception e) {
                    showError("Error deleting project: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        stage.setTitle("Maintainer - Projects");
        stage.setScene(scene);
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }
}
