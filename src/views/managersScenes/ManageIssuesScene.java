package views.managersScenes;

import controllers.IssueController;
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
import models.Issue;
import models.Project;
import models.User;
import utils.ThemeManager;
import views.globalScenes.SideBarComponent;

import java.util.List;

public class ManageIssuesScene {
    private Stage stage;
    private User currentUser;
    private Scene scene;
    private TableView<Issue> issueTable;
    private ComboBox<String> filterBox;

    public ManageIssuesScene(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
        createScene();
    }

    private void createScene() {
        BorderPane root = new BorderPane();
        
        SideBarComponent sidebar = new SideBarComponent(currentUser, stage);
        root.setLeft(sidebar.getSidebar());
        
        VBox content = new VBox(16);
        content.setPadding(new Insets(24));
        content.getStyleClass().add("dashboard-content");
        
        Label title = new Label("Issues");
        title.getStyleClass().add("dashboard-title");
        
        HBox toolbar = createToolbar();
        issueTable = createIssueTable();
        VBox.setVgrow(issueTable, Priority.ALWAYS);
        
        content.getChildren().addAll(title, toolbar, issueTable);
        root.setCenter(content);
        
        scene = new Scene(root, stage.getWidth(), stage.getHeight());
        ThemeManager.getInstance().registerScene(scene);
        
        loadIssues();
    }

    private HBox createToolbar() {
        HBox toolbar = new HBox(8);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        Button addBtn = new Button("New Issue");
        addBtn.setOnAction(e -> showIssueDialog(null));
        
        Button editBtn = new Button("Edit");
        editBtn.setOnAction(e -> {
            Issue selected = issueTable.getSelectionModel().getSelectedItem();
            if (selected != null) showIssueDialog(selected);
        });
        
        Button deleteBtn = new Button("Delete");
        deleteBtn.setOnAction(e -> deleteSelectedIssue());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        filterBox = new ComboBox<>();
        filterBox.getItems().addAll("All Issues", "Open", "In Progress", "My Issues", "Critical");
        filterBox.setValue("All Issues");
        filterBox.setOnAction(e -> loadIssues());
        
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadIssues());
        
        toolbar.getChildren().addAll(addBtn, editBtn, deleteBtn, spacer, filterBox, refreshBtn);
        return toolbar;
    }

    private TableView<Issue> createIssueTable() {
        TableView<Issue> table = new TableView<>();
        
        TableColumn<Issue, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("issueId"));
        idCol.setPrefWidth(50);
        
        TableColumn<Issue, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleCol.setPrefWidth(250);
        
        TableColumn<Issue, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);
        
        TableColumn<Issue, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setPrefWidth(80);
        
        TableColumn<Issue, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(80);
        
        TableColumn<Issue, Integer> projectCol = new TableColumn<>("Project");
        projectCol.setCellValueFactory(new PropertyValueFactory<>("projectId"));
        projectCol.setPrefWidth(70);
        
        table.getColumns().addAll(idCol, titleCol, statusCol, priorityCol, typeCol, projectCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        return table;
    }

    private void loadIssues() {
        List<Issue> issues;
        String filter = filterBox.getValue();
        
        switch (filter) {
            case "My Issues" -> issues = IssueController.getIssuesByAssignee(currentUser.getId());
            case "Open" -> issues = IssueController.getAllIssues().stream()
                .filter(i -> i.getStatus() == Issue.Status.OPEN).toList();
            case "In Progress" -> issues = IssueController.getAllIssues().stream()
                .filter(i -> i.getStatus() == Issue.Status.IN_PROGRESS).toList();
            case "Critical" -> issues = IssueController.getAllIssues().stream()
                .filter(i -> i.getPriority() == Issue.Priority.CRITICAL).toList();
            default -> issues = IssueController.getAllIssues();
        }
        
        issueTable.setItems(FXCollections.observableArrayList(issues));
    }

    private void showIssueDialog(Issue issue) {
        Dialog<Issue> dialog = new Dialog<>();
        dialog.setTitle(issue == null ? "New Issue" : "Edit Issue");
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        ComboBox<Project> projectBox = new ComboBox<>();
        projectBox.getItems().addAll(new ProjectController().getAllProjects());
        projectBox.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Project p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "" : p.getName());
            }
        });
        projectBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Project p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "" : p.getName());
            }
        });
        
        TextField titleField = new TextField(issue != null ? issue.getTitle() : "");
        TextArea descField = new TextArea(issue != null ? issue.getDescription() : "");
        descField.setPrefRowCount(3);
        
        ComboBox<Issue.Status> statusBox = new ComboBox<>();
        statusBox.getItems().addAll(Issue.Status.values());
        statusBox.setValue(issue != null ? issue.getStatus() : Issue.Status.OPEN);
        
        ComboBox<Issue.Priority> priorityBox = new ComboBox<>();
        priorityBox.getItems().addAll(Issue.Priority.values());
        priorityBox.setValue(issue != null ? issue.getPriority() : Issue.Priority.MEDIUM);
        
        ComboBox<Issue.Type> typeBox = new ComboBox<>();
        typeBox.getItems().addAll(Issue.Type.values());
        typeBox.setValue(issue != null ? issue.getType() : Issue.Type.BUG);
        
        if (issue != null) {
            projectBox.getItems().stream()
                .filter(p -> p.getProjectId() == issue.getProjectId())
                .findFirst()
                .ifPresent(projectBox::setValue);
        }
        
        grid.add(new Label("Project:"), 0, 0);
        grid.add(projectBox, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Description:"), 0, 2);
        grid.add(descField, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusBox, 1, 3);
        grid.add(new Label("Priority:"), 0, 4);
        grid.add(priorityBox, 1, 4);
        grid.add(new Label("Type:"), 0, 5);
        grid.add(typeBox, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK && projectBox.getValue() != null) {
                if (issue != null) {
                    issue.setProjectId(projectBox.getValue().getProjectId());
                    issue.setTitle(titleField.getText());
                    issue.setDescription(descField.getText());
                    issue.setStatus(statusBox.getValue());
                    issue.setPriority(priorityBox.getValue());
                    issue.setType(typeBox.getValue());
                    return issue;
                } else {
                    return new Issue(
                        projectBox.getValue().getProjectId(),
                        titleField.getText(),
                        descField.getText(),
                        priorityBox.getValue(),
                        typeBox.getValue(),
                        currentUser.getId()
                    );
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(i -> {
            try {
                if (issue != null) {
                    IssueController.updateIssue(i);
                } else {
                    IssueController.addIssue(i);
                }
                loadIssues();
            } catch (Exception e) {
                showError("Error saving issue: " + e.getMessage());
            }
        });
    }

    private void deleteSelectedIssue() {
        Issue selected = issueTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setContentText("Delete issue: " + selected.getTitle() + "?");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    IssueController.deleteIssue(selected.getIssueId());
                    loadIssues();
                } catch (Exception e) {
                    showError("Error deleting issue: " + e.getMessage());
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
        stage.setTitle("Maintainer - Issues");
        stage.setScene(scene);
        stage.show();
    }

    public Scene getScene() {
        return scene;
    }
}
