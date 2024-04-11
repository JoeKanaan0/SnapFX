package ar.midtermproject;

import ar.midtermproject.controllers.*;
import ar.midtermproject.model.Photo;
import ar.midtermproject.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ApplicationSwitcher {

    private Main main;
    private Stage stage;

    public void setMain(Main main) {
        this.main = main;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void loadLoginPage() {
        LoginController controller = loadPage("login.fxml");
        assert controller != null;
        controller.setMain(main);
    }

    public void loadSignupPage() {
        loadPage("signup.fxml");
    }

    public void loadMainPage() {
        FaceDetectionController controller = loadPage("faceDetection.fxml");
        assert controller != null;
        controller.setMain(main);
    }

    public void loadPhotoPage(Photo image, boolean saved) {
        PhotoController controller = loadPage("photo.fxml");
        assert controller != null;
        controller.setPhoto(image, saved);
    }

    public void loadSavedImages() {
        SavedImagesController controller = loadPage("savedImages.fxml");
        assert controller != null;
        controller.setMain(main);
        controller.loadImages();
    }

    /**
     * Loads the chat page and sets the chat ID.
     * @param user The user to load the chat of.
     */
    public void loadChatPage(User user) {
        ChatController controller = loadPage("chat.fxml");
        assert controller != null;
        controller.setMain(main);
        controller.loadChat(user);
    }

    /**
     * Loads the users page and populates it with data.
     */
    public void loadUserPage() {
        UsersController controller = loadPage("users.fxml");
        assert controller != null;
        controller.setMain(main);
        controller.loadUsersData();
    }

    public void loadProfilePage() {
        ProfileController controller = loadPage("profile.fxml");
        assert controller != null;
        controller.setMain(main);
        controller.loadImage();
    }

    /**
     * Loads an FXML page and returns its controller instance.
     * @param fxml the name of the FXML file to load
     * @param <T> the type of the controller for the FXML page
     * @return the controller instance for the FXML page
     */
    private <T extends AppController> T loadPage(String fxml) {
        try {
            // Load the FXML file using the class's resource loader
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            // Load the parent node from the FXML file
            Parent root = loader.load();
            // Get the controller instance from the loader
            T controller = loader.getController();
            // Set the controller's application switcher variable
            controller.setApp(this);
            // Create a new scene using the root node
            Scene scene = new Scene(root);
            // Create a new stage for the scene
            Stage newStage = new Stage();
            // Set the scene for the stage
            newStage.setScene(scene);
            // Set the width and height of the stage to match the current stage
            newStage.setWidth(stage.getWidth());
            newStage.setHeight(stage.getHeight());
            // Set the minimum width and height of the stage
            newStage.setMinWidth(800);
            newStage.setMinHeight(600);
            // Set the title for the stage
            newStage.setTitle("Augmented Reality");
            // Show the stage
            newStage.show();
            // Return the controller instance
            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}