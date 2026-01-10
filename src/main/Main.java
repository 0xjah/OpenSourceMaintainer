package main;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import views.globalScenes.*;

/**
 * Open Source Maintainer Application
 */
public class Main extends Application
{  
    @Override
    public void init() {
        // Load Iosevka font
        try {
            Font.loadFont(getClass().getResourceAsStream("/fonts/SGr-IosevkaTerm-Regular.ttc"), 13);
        } catch (Exception e) {
            System.err.println("Could not load Iosevka font: " + e.getMessage());
        }
    }
    
    @Override
    public void start(Stage primaryStage)
    {
        new LoginView(primaryStage).show();   
    }
  
    public static void main(String[] args) {
        launch(args);
    }
}
