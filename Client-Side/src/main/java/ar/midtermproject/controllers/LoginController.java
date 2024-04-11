package ar.midtermproject.controllers;

import ar.midtermproject.AppController;
import ar.midtermproject.Main;

import java.net.URL;
import java.util.ResourceBundle;

import ar.midtermproject.connection.ApiService;
import ar.midtermproject.ApplicationSwitcher;
import ar.midtermproject.model.User;
import ar.midtermproject.util.FXMLUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Login Controller.
 */
public class LoginController implements Initializable, AppController {

    @FXML private AnchorPane root;
    @FXML private ImageView backgroundImageView;
    @FXML private TextField userId;
    @FXML private PasswordField password;
    @FXML private Label errorMessage;
    @FXML private VBox vBox;

    private Main main;
    private ApplicationSwitcher application;

    public void setMain(Main main) {
        this.main = main;
    }
    /**
     * Set the Main Application for the Controller.
     * @param application The Main Application.
     */
    @Override
    public void setApp(ApplicationSwitcher application){
        this.application = application;
    }

    /**
     * Initialize the login page.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set error message, prompt texts and image for the login page
        errorMessage.setText("");
        userId.setPromptText("");
        password.setPromptText("");

        FXMLUtils fxmlUtils = new FXMLUtils();
        fxmlUtils.setBackground(backgroundImageView, root, vBox);
        backgroundImageView = fxmlUtils.getBackGroundImageView();
        root = fxmlUtils.getAnchorPane();
        vBox = fxmlUtils.getvBox();
    }

    @FXML
    public void processLogin() {
        errorMessage.setText("Logging In...");

        Task<Void> loginUserTask = new Task<>() {
            @Override
            protected Void call() {
                // Set the application stage to the current window
                application.setStage((Stage) root.getScene().getWindow());

                ApiService apiService = new ApiService();

                String name = userId.getText();
                String pass = password.getText();

                User user = new User(name, pass);

                try {
                    String response = apiService.makePostRequest("http://localhost:8080/api/users/login", user);
                    main.setUserFromJSON(response);
                    Platform.runLater(() -> {
                        application.loadMainPage();
                        Stage stage = (Stage) root.getScene().getWindow();
                        stage.close(); // Close the current page
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> errorMessage.setText("Username/Password is incorrect"));
                }
                return null;
            }
        };

        // Start the task in a new thread
        new Thread(loginUserTask).start();
    }

    /**
     * Redirect the user to the signup page.
     */
    @FXML
    public void goToSignup() {
        application.setStage((Stage) root.getScene().getWindow());
        Stage stage = (Stage) root.getScene().getWindow(); // get current stage
        application.loadSignupPage();
        stage.close(); // close current window
    }
}