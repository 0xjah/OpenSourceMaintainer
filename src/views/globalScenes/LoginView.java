package views.globalScenes;
import controllers.UserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.User;
import utils.ThemeManager;

public class LoginView {
    private Stage stage;
    private Scene scene;
    private ThemeManager themeManager;
    
    public LoginView(Stage stage)
    {
        this.stage = stage;
        this.themeManager = ThemeManager.getInstance();
        createLoginScene();
    }
    
    private void createLoginScene() {
        VBox formBox = new VBox(24);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(320);
        formBox.setPadding(new Insets(48));
        
        Label titleLabel = new Label("Maintainer");
        titleLabel.getStyleClass().add("dashboard-title");
        
        Label subtitleLabel = new Label("Open Source Project Management");
        subtitleLabel.getStyleClass().add("stats-card-subtitle");
        
        VBox titleBox = new VBox(4, titleLabel, subtitleLabel);
        titleBox.setAlignment(Pos.CENTER);
        
        VBox usernameBox = new VBox(6);
        Label usernameLabel = new Label("USERNAME");
        usernameLabel.getStyleClass().add("form-label");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter username");
        usernameField.setPrefHeight(40);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);
        
        VBox passwordBox = new VBox(6);
        Label passwordLabel = new Label("PASSWORD");
        passwordLabel.getStyleClass().add("form-label");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");
        passwordField.setPrefHeight(40);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);
        
        Button loginBtn = new Button("Sign In");
        loginBtn.setPrefHeight(44);
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.getStyleClass().add("button-primary");
        
        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("stats-card-subtitle");
        
        formBox.getChildren().addAll(titleBox, usernameBox, passwordBox, loginBtn, messageLabel);
        
        BorderPane root = new BorderPane();
        root.setCenter(formBox);
        
        scene = new Scene(root, 460, 520);
        themeManager.registerScene(scene);
        
        loginBtn.setOnAction(e -> handleLogin(usernameField, passwordField, messageLabel));
        passwordField.setOnAction(e -> loginBtn.fire());
        usernameField.setOnAction(e -> passwordField.requestFocus());
    }
    
    private void handleLogin(TextField usernameField, PasswordField passwordField, Label messageLabel) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Enter username and password");
            return;
        }
        
        User authenticatedUser = UserController.authenticate(username, password);
        if (authenticatedUser != null)
        {
            stage.setMaximized(true);
            new DashBoardScene(stage, authenticatedUser).show();
            messageLabel.setText("");
        } else {
            messageLabel.setText("Invalid credentials");
            passwordField.clear();
        }
    }
    
    public void show() {
        stage.setResizable(true);
        stage.setTitle("Maintainer");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }
}