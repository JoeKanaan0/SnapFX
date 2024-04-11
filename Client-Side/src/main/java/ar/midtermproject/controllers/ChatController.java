package ar.midtermproject.controllers;

import ar.midtermproject.AppController;
import ar.midtermproject.Main;
import ar.midtermproject.connection.ApiService;
import ar.midtermproject.ApplicationSwitcher;
import ar.midtermproject.model.Chat;
import ar.midtermproject.model.User;
import ar.midtermproject.util.ChatUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * This class is responsible for the chat screen.
 * It handles the functionality of sending messages, displaying messages and returning to the users screen.
 */
public class ChatController implements Initializable, AppController {

    @FXML private AnchorPane root;
    @FXML private AnchorPane upperPane;
    @FXML private AnchorPane buttonBox;
    @FXML private AnchorPane textField;
    @FXML private ListView<Chat> view;
    @FXML private Button send;
    @FXML private TextField text;
    @FXML private Button backButton;

    private Main main; // Reference to the Main application
    private ApplicationSwitcher application;
    private User user; // The user that was clicked on to chat with
    private ChatUtils chatUtils;

    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Sets the application instance for this controller.
     *
     * @param application The instance of the main application.
     */
    @Override
    public void setApp(ApplicationSwitcher application) {
        this.application = application;
    }

    /**
     * Initializes the chat page view.
     * This function sets the dimensions of the various anchor panes, buttons, and text fields that make up the chat window.
     * It anchors the bottom text field to the bottom of the root pane, sets the default button to the send button,
     * and adds a change listener to the size of the root pane to ensure that the width and height of the upper pane
     * and text field are set to the same size as the root pane.
     */
    public void initialize(URL location, ResourceBundle resources) {

        // Set the preferred heights of the button box and text field.
        buttonBox.setPrefHeight(50);
        textField.setPrefHeight(50);

        // Anchor the bottom of the text field to the bottom of the root pane and the left of the text field to the left of the root pane.
        AnchorPane.setBottomAnchor(textField, 0.0);
        AnchorPane.setLeftAnchor(textField, 0.0);

        // Set the preferred width of the button box to the computed size and set its max width to Double.MAX_VALUE.
        buttonBox.setPrefWidth(Control.USE_COMPUTED_SIZE);
        buttonBox.setMaxWidth(Double.MAX_VALUE);

        // Set the HBox grow priority to ALWAYS for the button box.
        HBox.setHgrow(buttonBox, Priority.ALWAYS);

        // Set the action for the back button to return to the user page and close the current window.
        backButton.setOnAction((ActionEvent event) -> {
            application.setStage((Stage) backButton.getScene().getWindow());
            Stage stage = (Stage) backButton.getScene().getWindow(); // get current stage
            application.loadUserPage();
            stage.close(); // close current window
        });

        // Set the default button to the send button which allows the user to send message with "enter".
        send.setDefaultButton(true);

        // Use a ChangeListener to modify the VBox size when the screen size changes.
        ChangeListener<Number> sizeListener = (observableValue, oldSize, newSize) -> {

            // Set the preferred width and height of the upper pane and text field to match the root pane.
            upperPane.setPrefWidth(root.getWidth());
            textField.setPrefWidth(root.getWidth());
            upperPane.setPrefHeight(root.getHeight());
            textField.setPrefHeight(root.getHeight());

            // Anchor the bottom of the upper pane to 10% of the height of the root pane and the top of the text field to 90% of the height of the root pane.
            AnchorPane.setBottomAnchor(upperPane, root.getHeight() * 0.1);
            AnchorPane.setTopAnchor(textField, root.getHeight() * 0.9);
        };

        // Add the size listener to the width and height properties of the root pane.
        root.widthProperty().addListener(sizeListener);
        root.heightProperty().addListener(sizeListener);

    }

    /**
     * Loads chat messages between two users from the database and displays them in the chat view.
     * @param user the user to load chat messages with
     */
    public void loadChat(User user) {

        ApiService apiService = new ApiService();

        try {
            this.user = user;

            String url = "http://localhost:8080/api/chats/get/" + main.getUser().getId() + "/" + user.getId();
            String response = apiService.makeGetRequest(url);
            ObjectMapper objectMapper = new ObjectMapper();
            List<Chat> chatList = objectMapper.readValue(response, new TypeReference<>() {
            });

            // Add each name or image to the ListView
            for (Chat message : chatList) {
                chatUtils = new ChatUtils();
                chatUtils.addMessage(message, view, main);
                view = chatUtils.getView();
                main = chatUtils.getMain();

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     Sends a message from the current user to the selected user
     */
    @FXML
    private void sendMessage() {

        ApiService apiService = new ApiService();
        try {
            String message = text.getText();

            Chat newMessage = new Chat(message, main.getUser(), user);
            chatUtils.addMessage(newMessage, view, main);
            view = chatUtils.getView();
            main = chatUtils.getMain();

            apiService.makePostRequest("http://localhost:8080/api/chats", newMessage);
            text.setText("");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is called when the user wants to upload an image.
     * It opens a file chooser dialog to allow the user to select an image file.
     * If a file is selected, the image path is added to the chat list view and also inserted into the database.
     */
    @FXML
    private void uploadImage() {

        ApiService apiService = new ApiService();

        FileChooser fileChooser = new FileChooser();

        // Set the extension filter to only allow image files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files (*.jpg, *.png, *.gif)", "*.jpg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show the file chooser dialog and get the selected file
        File file = fileChooser.showOpenDialog(root.getScene().getWindow());

        if (file != null) {
            // Add the image path to the ListView

            Chat newMessage = new Chat(file.getPath(), main.getUser(), user);
            chatUtils.addMessage(newMessage, view, main);
            view = chatUtils.getView();
            main = chatUtils.getMain();

            // Insert the image path into the database
            try {
                apiService.makePostRequest("http://localhost:8080/api/chats", newMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}