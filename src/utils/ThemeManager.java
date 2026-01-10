package utils;

import javafx.scene.Scene;
import java.util.ArrayList;
import java.util.List;

public class ThemeManager {
    private static ThemeManager instance;
    private String currentTheme = "dark";
    private List<Scene> registeredScenes = new ArrayList<>();
    
    private ThemeManager() {}
    
    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }
    
    public void registerScene(Scene scene) {
        if (!registeredScenes.contains(scene)) {
            registeredScenes.add(scene);
            applyThemeToScene(scene, currentTheme);
        }
    }
    
    public void unregisterScene(Scene scene) {
        registeredScenes.remove(scene);
    }
    
    public void changeTheme(String theme) {
        this.currentTheme = theme.toLowerCase();
        
        for (Scene scene : registeredScenes) {
            applyThemeToScene(scene, currentTheme);
        }
    }
    
    private void applyThemeToScene(Scene scene, String theme) {
        scene.getStylesheets().clear();
        
        try {
            // Load base.css first
            java.net.URL baseUrl = getClass().getResource("/themes/base.css");
            if (baseUrl != null) {
                scene.getStylesheets().add(baseUrl.toExternalForm());
            } else {
                System.err.println("Base theme not found: /themes/base.css");
            }
            
            // Load theme-specific CSS
            java.net.URL themeUrl = getClass().getResource("/themes/" + theme + ".css");
            if (themeUrl != null) {
                scene.getStylesheets().add(themeUrl.toExternalForm());
            } else {
                System.err.println("Theme file not found: /themes/" + theme + ".css");
            }
        } catch (Exception e) {
            System.err.println("Error loading theme: " + e.getMessage());
        }
    }
    
    public String getCurrentTheme() {
        return currentTheme;
    }
    
    public void clearRegisteredScenes() {
        registeredScenes.clear();
    }
}