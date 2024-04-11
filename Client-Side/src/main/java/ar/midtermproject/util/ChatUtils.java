package ar.midtermproject.util;

import ar.midtermproject.Main;
import ar.midtermproject.model.Chat;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class ChatUtils {

    private ListView<Chat> view;
    private Main main;

    public ListView<Chat> getView() {
        return view;
    }

    public Main getMain() {
        return main;
    }

    /**
     * Adds a message to the ListView and sets up the ListView cells to display the message content.
     * @param message An object that hold information about the message
     */
    public void addMessage(Chat message, ListView<Chat> view, Main main) {

        this.view = view;
        this.main = main;

        // Add the message to the chat ListView
        view.getItems().add(message);

        // Set the cell factory for the ListView to define how each message should be displayed
        view.setCellFactory(new Callback<>() {
            @Override
            public ListCell<Chat> call(ListView<Chat> param) {
                return new ListCell<>() {
                    // Create an ImageView object to display images in the message
                    private final ImageView imageView = new ImageView();

                    @Override
                    public void updateItem(Chat message, boolean empty) {
                        super.updateItem(message, empty);
                        if (empty || message == null) {
                            // If the message is empty or null, clear the text and graphics of the cell
                            setText(null);
                            setGraphic(null);
                            setAlignment(null);
                        } else {
                            // If the message has content, set the text and/or graphics of the cell
                            String item = message.text();
                            if (verifyImage(item)) {
                                // If the message is an image, display it in the ImageView
                                imageView.setImage(new Image(item)); // Check later
                                imageView.setFitWidth(100);
                                imageView.setPreserveRatio(true);
                                setGraphic(imageView);
                            } else {
                                // If the message is text, display it as the cell text
                                setText(item);
                            }
                            // Set the alignment of the cell based on the sender of the message
                            if (message.sender().getId() == main.getUser().getId()) {
                                setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
                            } else {
                                setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                            }
                        }
                    }
                };
            }
        });
    }

    /**
     * Verify whether a text string represents an image by checking if the file extension is .jpg, .png, or .jpeg
     * @param text the text string to check
     * @return true if the text represents an image, false otherwise
     */
    private boolean verifyImage(String text) {
        return text.endsWith(".jpg") || text.endsWith(".png") || text.endsWith(".jpeg");
    }

}
