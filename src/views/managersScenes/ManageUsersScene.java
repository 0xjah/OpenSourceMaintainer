package views.managersScenes;

import controllers.UserController;
import models.User;
import models.User.Role;
import views.globalScenes.SideBarComponent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import utils.ThemeManager;

public class ManageUsersScene {

    private final UserController controller = new UserController();

    private final User currentUser;
    private final Stage stage;

    public ManageUsersScene(Stage stage, User currentUser) {
        this.stage = stage;
        this.currentUser = currentUser;
    }

    public void show() {
        SideBarComponent sidebar = new SideBarComponent(currentUser, stage);

        VBox mainContent = new VBox(16);
        mainContent.setPadding(new Insets(24));
        mainContent.getStyleClass().add("dashboard-content");

        Label title = new Label("Users");
        title.getStyleClass().add("dashboard-title");

        TableView<User> tableView = listUsersTable();

        Button addButton = new Button("Add User");
        addButton.setOnAction(e -> showAddUserForm(tableView));

        HBox toolbar = new HBox(8, addButton);
        toolbar.setPadding(new Insets(0, 0, 8, 0));

        loadUsers(tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        mainContent.getChildren().addAll(title, toolbar, tableView);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(sidebar.getSidebar());
        mainLayout.setCenter(mainContent);

        Scene scene = new Scene(mainLayout, stage.getWidth(), stage.getHeight());
        ThemeManager.getInstance().registerScene(scene);

        stage.setTitle("Maintainer - Users");
        stage.setScene(scene);
        stage.show();
    }

    private TableView<User> listUsersTable() {
        TableView<User> tableView = new TableView<>();

        TableColumn<User, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        idCol.setPrefWidth(50);

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        nameCol.setPrefWidth(150);

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getUsername()));
        usernameCol.setPrefWidth(120);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEmail()));
        emailCol.setPrefWidth(180);

        TableColumn<User, Role> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getRole()));
        roleCol.setPrefWidth(100);

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            private final HBox pane = new HBox(4, editBtn, deleteBtn);

            {
                editBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditUserForm(user, getTableView());
                });

                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    try {
                        controller.deleteUser(user.getId());
                        getTableView().getItems().remove(user);
                    } catch (SQLException ex) {
                        showAlert("Delete failed: " + ex.getMessage());
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });

        tableView.getColumns().addAll(idCol, nameCol, usernameCol, emailCol, roleCol, actionsCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return tableView;
    }

    private void loadUsers(TableView<User> tableView) {
        List<User> users = controller.getAllUsers();
        ObservableList<User> data = FXCollections.observableArrayList(users);
        tableView.setItems(data);
    }

    private void showAddUserForm(TableView<User> tableView) {
        Stage formStage = new Stage();
        formStage.setTitle("Add User");

        TextField nameField = new TextField();
        TextField usernameField = new TextField();
        TextField emailField = new TextField();
        PasswordField pwdField = new PasswordField();

        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().setAll(Role.values());
        roleBox.setValue(Role.MAINTAINER);

        Button saveBtn = new Button("Save");
        saveBtn.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String username = usernameField.getText();
                String email = emailField.getText();
                String password = pwdField.getText();
                Role role = roleBox.getValue();

                if (role == null) {
                    showAlert("Select a role");
                    return;
                }

                User newUser = new User(0, name, role, password, username, email);
                controller.addUser(newUser);
                loadUsers(tableView);
                formStage.close();

            } catch (SQLException sqlEx) {
                if (sqlEx.getMessage().contains("UNIQUE") || sqlEx.getMessage().contains("Duplicate")) {
                    showAlert("Username already exists");
                } else {
                    showAlert("Database error: " + sqlEx.getMessage());
                }
            } catch (Exception ex) {
                showAlert("Error: " + ex.getMessage());
            }
        });

        VBox form = new VBox(8,
                new Label("Name:"), nameField,
                new Label("Username:"), usernameField,
                new Label("Email:"), emailField,
                new Label("Password:"), pwdField,
                new Label("Role:"), roleBox,
                saveBtn
        );
        form.setPadding(new Insets(16));
        Scene scene = new Scene(form, 300, 420);
        ThemeManager.getInstance().registerScene(scene);
        formStage.setScene(scene);
        formStage.show();
    }

    private void showEditUserForm(User user, TableView<User> tableView) {
        Stage formStage = new Stage();
        formStage.setTitle("Edit User");

        TextField nameField = new TextField(user.getName());
        TextField usernameField = new TextField(user.getUsername());
        TextField emailField = new TextField(user.getEmail() != null ? user.getEmail() : "");
        PasswordField pwdField = new PasswordField();
        pwdField.setPromptText("Leave blank to keep current");

        ComboBox<Role> roleBox = new ComboBox<>();
        roleBox.getItems().setAll(Role.values());
        roleBox.setValue(user.getRole());

        Button saveBtn = new Button("Update");
        saveBtn.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String usrName = usernameField.getText();
                String email = emailField.getText();
                String pwd = pwdField.getText();
                Role role = roleBox.getValue();

                if (role == null) {
                    showAlert("Select a role");
                    return;
                }

                String passwordToSave = pwd.isEmpty() ? user.getPassword() : pwd;

                User updated = new User(user.getId(), name, role, passwordToSave, usrName, email);
                controller.updateUser(updated);

                loadUsers(tableView);
                formStage.close();
            } catch (Exception ex) {
                showAlert("Update failed: " + ex.getMessage());
            }
        });

        VBox form = new VBox(8,
                new Label("Name:"), nameField,
                new Label("Username:"), usernameField,
                new Label("Email:"), emailField,
                new Label("Password:"), pwdField,
                new Label("Role:"), roleBox,
                saveBtn
        );
        form.setPadding(new Insets(16));
        Scene scene = new Scene(form, 300, 420);
        ThemeManager.getInstance().registerScene(scene);
        formStage.setScene(scene);
        formStage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
