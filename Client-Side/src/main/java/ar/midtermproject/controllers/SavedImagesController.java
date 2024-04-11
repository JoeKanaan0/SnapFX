/**
 * Controller for the saved images page.
 */
package ar.midtermproject.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import ar.midtermproject.AppController;
import ar.midtermproject.Main;
import ar.midtermproject.connection.ApiService;
import ar.midtermproject.ApplicationSwitcher;
import ar.midtermproject.model.Photo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class SavedImagesController implements Initializable, AppController {

    @FXML private Label label;
    @FXML private FlowPane flowPane;
    @FXML private Button backButton;
    @FXML private Label placeholder;

    private Main main;
    private ApplicationSwitcher application;

    public void setMain(Main main) {
        this.main = main;
    }
    /**
     * Sets the main application.
     * @param application the main application.
     */
    @Override
    public void setApp(ApplicationSwitcher application) {
        this.application = application;
    }

    /**
     * Initializes the saved images page.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Set label style
        label.setText("Saved Images");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        label.setPadding(new Insets(10, 0, 10, 0));

        // Set FlowPane style and properties
        flowPane.setOrientation(Orientation.HORIZONTAL);
        flowPane.setPrefHeight(200);
        flowPane.setPadding(new Insets(0, 10, 0, 10));
        flowPane.setStyle("-fx-background-color: #F5F5F5;");

        // Enable the scrollbar if there are more than 6 images
        if (flowPane.getChildren().size() > 6) {
            ScrollPane scrollPane = (ScrollPane) flowPane.getParent().getParent();
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        }
    }

    /**
     This function loads images into the flowPane.
     If an application exists, it will load only the images for the user with the corresponding ID.
     If no application exists, it will load all the images.
     It also adds an event handler to each image so that when clicked, it will open the image in the PhotoPage.
     */
    public void loadImages() {
        // Retrieve the user ID from the application object and clear the existing children of the flowPane

        ApiService apiService = new ApiService();

        long userId = main.getUser().getId();
        flowPane.getChildren().clear();
        try {

            String response = apiService.makeGetRequest("http://localhost:8080/api/images/user/" + userId);

            // Deserialize the JSON string into a list of Photo objects
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType photoListType = objectMapper.getTypeFactory().constructCollectionType(List.class, Photo.class);
            List<Photo> photos = objectMapper.readValue(response, photoListType);

            // Iterate through the list of photos and display them
            for (Photo photo : photos) {
                // Retrieve the path of the image and create a new ImageView object
                String imagePath = photo.path();
                Image image = new Image(imagePath);
                ImageView imageView = new ImageView(image);
                // Set the fitHeight and fitWidth properties of the ImageView
                imageView.setFitHeight(150);
                imageView.setFitWidth(150);
                Insets insets = new Insets(10);
                FlowPane.setMargin(imageView, insets);
                // Set the onMouseClicked event of the ImageView to open the photo page for the image
                imageView.setOnMouseClicked(event -> {
                    // Call the loadPhotoPage function of the application object
                    application.setStage((Stage) backButton.getScene().getWindow());
                    application.loadPhotoPage(photo, true);
                    Stage stage = (Stage) backButton.getScene().getWindow();
                    stage.close();
                });
                // Add the ImageView to the flowPane
                flowPane.getChildren().add(imageView);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        // Show/hide the placeholder label based on the number of images
        if (flowPane.getChildren().isEmpty()) {
            placeholder.setVisible(true);
        } else {
            placeholder.setVisible(false);
        }
    }

    /**
     Handles the "goBack" button click event.
     Closes the current window and sets the application stage to the previous window.
     Also closes the database connection.
     @throws Exception if there is an error closing the connection or closing the window
     */
    @FXML
    private void goBack() throws Exception {
        try {
            application.setStage((Stage) backButton.getScene().getWindow());
            Stage stage = (Stage) backButton.getScene().getWindow(); // get current stage
            stage.close(); // close current window
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Error closing connection or closing window");
        }
    }
}
