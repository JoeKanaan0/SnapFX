package ar.midtermproject.controllers;

import ar.midtermproject.AppController;
import ar.midtermproject.Main;
import ar.midtermproject.connection.ApiService;
import ar.midtermproject.ApplicationSwitcher;
import ar.midtermproject.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The UsersController class is responsible for populating the user list view
 * and handling user interactions with it.
 */
public class UsersController implements Initializable, AppController {

    @FXML private AnchorPane root;
    @FXML private ListView<User> view;
    @FXML private Button backButton;

    private Main main;
    private ApplicationSwitcher application;

    public void setMain(Main main) {
        this.main = main;
    }
    /**
     * Sets the Main application instance for this class.
     *
     * @param application the instance of the Main class
     */
    @Override
    public void setApp(ApplicationSwitcher application) {
        this.application = application;
    }

    /**
     * Initializes the UsersController class.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Handle the back button press
        backButton.setOnAction((ActionEvent event) -> {
            application.setStage((Stage) backButton.getScene().getWindow());
            Stage stage = (Stage) backButton.getScene().getWindow(); // get current stage
            stage.close(); // close current window
        });

        try {
            // Set a custom ListCell factory to include the image and username
            view.setCellFactory(listView -> new UserCell());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Changes the cursor to a hand when hovering over the user list view.
     */
    @FXML
    private void hovered() {
        Scene scene = root.getScene();
        scene.setCursor(Cursor.HAND);
    }

    /**
     * Changes the cursor back when hovering off the user list view.
     */
    @FXML
    private void exited() {
        // Set the cursor of the root scene to the default cursor
        Scene scene = root.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }


    public class UserCell extends ListCell<User> {
        private final ImageView imageView = new ImageView();
        private final Label label = new Label();
        private final HBox hbox = new HBox(imageView, label);

        public UserCell() {
            imageView.setFitHeight(50);
            imageView.setFitWidth(50);
            imageView.setClip(new Circle(25, 25, 25));
            hbox.setSpacing(10);
        }

        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);

            if (empty || user == null) {
                setText(null);
                setGraphic(null);
            } else {

                setOnMouseClicked(event -> {
                    application.loadChatPage(user);
                    Stage stage = (Stage) backButton.getScene().getWindow(); // get current stage
                    stage.close();
                });

                Image userImage = new Image(user.getProfile());
                imageView.setImage(userImage);

                label.setText(user.getName());
                setGraphic(hbox);
            }
        }
    }

    /**
     * Load data from the database and add each user to the ListView.
     */
    public void loadUsersData() {

        ApiService apiService = new ApiService();

        long currentUserId = main.getUser().getId();

        try {
            String response = apiService.makeGetRequest("http://localhost:8080/api/users/except/" + currentUserId);
            ObjectMapper objectMapper = new ObjectMapper();
            List<User> users = objectMapper.readValue(response, new TypeReference<>() {
            });

            for (User user: users) {
                view.getItems().add(user);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
