package ar.midtermproject.controllers;

import ar.midtermproject.AppController;
import ar.midtermproject.connection.ApiService;
import ar.midtermproject.ApplicationSwitcher;
import ar.midtermproject.model.User;
import ar.midtermproject.util.FXMLUtils;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupController implements Initializable, AppController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button signupButton;
    @FXML private Label successText;
    @FXML private VBox vBox;
    @FXML private AnchorPane root;
    @FXML private ImageView backgroundImageView;
    @FXML private Button backButton;

    private ApplicationSwitcher application;

    /**
     * Sets the main application for this controller.
     *
     * @param application the main application
     */
    @Override
    public void setApp(ApplicationSwitcher application) {
        this.application = application;
    }

    /**
     * Initializes the controller class and sets up the signup page.
     *
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Set the action for the "Back" button to load the Login page
        backButton.setOnAction((ActionEvent event) -> {
            application.setStage((Stage) backButton.getScene().getWindow());
            application.loadLoginPage();
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close(); // Close the current page
        });

        FXMLUtils fxmlUtils = new FXMLUtils();
        fxmlUtils.setBackground(backgroundImageView, root, vBox);
        backgroundImageView = fxmlUtils.getBackGroundImageView();
        root = fxmlUtils.getAnchorPane();
        vBox = fxmlUtils.getvBox();
    }

    /**
     * Handles the sign-up process by inserting a new user into the database.
     * Uses a background task to avoid freezing the UI thread while waiting for the database to respond.
     */
    @FXML
    private void handleSignup() {

        ApiService apiService = new ApiService();

        // Set the stage to the current window
        application.setStage((Stage) signupButton.getScene().getWindow());

        // Create a new background task to perform the database operation
        Task<String> task = new Task<>() {
            @Override
            protected String call() {
                String name = usernameField.getText();
                String password = passwordField.getText();

                User user = new User(name, password);

                String response;
                try {
                    response = apiService.makePostRequest("http://localhost:8080/api/users", user);
                } catch (Exception e) {
                    response = null;
                }

                return response;
            }
        };

        // Define the action to be taken when the background task is completed
        task.setOnSucceeded(e -> {
            if (task.getValue() != null) {
                successText.setText("Inserting into Database...");

                // Use a PauseTransition to add a delay before closing the current window and loading the login page
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(event -> {
                    // Close the current window
                    application.setStage((Stage) signupButton.getScene().getWindow());
                    application.loadLoginPage();
                    Stage stage = (Stage) signupButton.getScene().getWindow();
                    stage.close(); // Close the current page
                });
                pause.play();
            } else {
                successText.setText("Username Already Exists!");
            }
        });

        // Start the background task on a separate thread
        Thread thread = new Thread(task);
        thread.start();
    }
}