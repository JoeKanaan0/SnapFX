package ar.midtermproject.controllers;

import ar.midtermproject.AppController;
import ar.midtermproject.connection.ApiService;
import ar.midtermproject.ApplicationSwitcher;
import ar.midtermproject.model.Photo;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Photo Controller.
 */
public class PhotoController implements Initializable, AppController {

    @FXML private AnchorPane root;
    @FXML private Button discard;
    @FXML private Button keep;
    @FXML private ImageView photoView;

    private ApplicationSwitcher application;
    private long id;
    private String path;
    private boolean saved;

    /**
     * Initializes the controller class.
     */
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        // Add a ChangeListener to the root AnchorPane's width and height properties
        ChangeListener<Number> sizeListener = (observableValue, oldSize, newSize) -> {
            // Set the fitWidth and fitHeight properties of the ImageView to the new width and height of the root AnchorPane
            photoView.setFitWidth(root.getWidth());
            photoView.setFitHeight(root.getHeight());

            photoView.setX(root.getWidth() / 7);
        };
        root.widthProperty().addListener(sizeListener);
        root.heightProperty().addListener(sizeListener);
    }

    /**
     * Sets the main application.
     *
     * @param application The main application instance.
     */
    @Override
    public void setApp(ApplicationSwitcher application) {
        this.application = application;
    }

    /**
     * Sets the photo to be displayed and indicates whether the photo has already been saved.
     *
     * @param photo The Saved photo.
     * @param saved Indicates whether the photo has already been saved.
     */
    public void setPhoto(Photo photo, boolean saved) {
        this.id = photo.id();
        this.saved = saved;
        this.path = photo.path();
        File file = new File(path);
        Image image = new Image(file.toURI().toString());
        photoView.setImage(image);
    }

    /**
     * Handles the discard button click event.
     */
    @FXML
    private void discardClicked() {

        ApiService apiService = new ApiService();

        // Delete the image from the database
        try {
            apiService.makeDeleteRequest("http://localhost:8080/api/images/delete/" + id);

        } catch (IOException e) {
            e.printStackTrace();
        }

        application.setStage((Stage) discard.getScene().getWindow());
        Stage stage = (Stage) discard.getScene().getWindow();
        stage.close(); // Close the current page

        // Delete the file
        File file = new File(path);
        if (!file.delete()) {
            System.out.println("Failed to delete the file.");
        }

        if (saved) {
            application.loadSavedImages();
        }
    }

    /**
     * Handles the keep button click event.
     */
    @FXML
    private void keepClicked() {
        application.setStage((Stage) keep.getScene().getWindow());
        application.loadSavedImages();
        Stage stage = (Stage) keep.getScene().getWindow();
        stage.close(); // Close the current page
    }

}
