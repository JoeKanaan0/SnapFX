package ar.midtermproject.controllers;

import ar.midtermproject.AppController;
import ar.midtermproject.Main;
import ar.midtermproject.connection.ApiService;
import ar.midtermproject.ApplicationSwitcher;
import ar.midtermproject.model.User;
import ar.midtermproject.util.FXMLUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable, AppController {

    @FXML private AnchorPane root;
    @FXML private ImageView profileView;
    @FXML private Button backButton;
    @FXML private Button saveButton;
    @FXML private Button changeButton;
    @FXML private Label saveMessageLabel;
    @FXML private ImageView backgroundImageView;
    @FXML private VBox vBox;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private Main main;
    private ApplicationSwitcher application;
    private File selectedImageFile;

    public void setMain(Main main) {
        this.main = main;
    }
    /**
     * Set the application instance.
     *
     * @param application the application instance.
     */
    @Override
    public void setApp(ApplicationSwitcher application) {
        this.application = application;
    }

    /**
     * Initialize the profile page.
     *
     */
    public void initialize(URL url, ResourceBundle rb) {

        backButton.setOnAction((ActionEvent event) -> {
            application.setStage((Stage) backButton.getScene().getWindow());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
        });

        changeButton.setOnAction((ActionEvent event) -> {
            // Create a file chooser to choose the new profile photo
            FileChooser fileChooser = new FileChooser();

            // Set the extension filter to only allow image files
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.jpg, *.png, *.gif)", "*.jpg", "*.png", "*.gif");
            fileChooser.getExtensionFilters().add(extFilter);

            // Show the file chooser dialog and get the selected file
            selectedImageFile = fileChooser.showOpenDialog(root.getScene().getWindow());

            if (selectedImageFile != null) {
                Image image = new Image(selectedImageFile.toURI().toString());
                profileView.setImage(image);
            }
        });

        saveButton.setOnAction((ActionEvent event) -> {
            try {
                long id = main.getUser().getId();
                User newUser = new User(id, usernameField.getText(), passwordField.getText(),
                        selectedImageFile != null ? selectedImageFile.getPath() : main.getUser().getProfile());

                main.setUser(newUser);

                ApiService apiService = new ApiService();
                String response = apiService.makePutRequest("http://localhost:8080/api/users/" + id, newUser);

                // Set the text of the saveMessageLabel to "Changes saved"
                saveMessageLabel.setText("Changes saved");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        FXMLUtils fxmlUtils = new FXMLUtils();
        fxmlUtils.setBackground(backgroundImageView, root, vBox);
        backgroundImageView = fxmlUtils.getBackGroundImageView();
        root = fxmlUtils.getAnchorPane();
        vBox = fxmlUtils.getvBox();

    }

    /**
     * Load the user's profile image from the database and display it in the profileView ImageView.
     */
    public void loadImage() {

        // Get the user from the Main application
        User user = main.getUser();

        try {
            Image image = new Image(new File(user.getProfile()).toURI().toString());
            profileView.setImage(image);
            usernameField.setText(user.getName());
            passwordField.setText(user.getPassword());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
