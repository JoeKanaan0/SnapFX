package ar.midtermproject.controllers;

import ar.midtermproject.AppController;
import ar.midtermproject.Main;
import ar.midtermproject.connection.ApiService;
import ar.midtermproject.dto.ImageRequestDTO;
import ar.midtermproject.ApplicationSwitcher;
import ar.midtermproject.model.Photo;
import ar.midtermproject.util.FilterUtils;
import ar.midtermproject.util.ImageUtils;

import ar.midtermproject.util.VideoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This class controls the main page of the application.
 */
public class FaceDetectionController implements Initializable, AppController {

    @FXML private AnchorPane root;
    @FXML private ImageView webcam;
    @FXML private SplitPane splitPane;
    @FXML private FlowPane filterPanel;
    @FXML private AnchorPane rightPanel;

    private static final int WEBCAM_INDEX = 0;
    private static final Size FRAME_SIZE = new Size(320, 240);
    private VideoCapture capture;
    private CascadeClassifier faceDetector;
    private AtomicReference<Image> imageRef;
    private boolean isOverlayVisible = false;
    private int currentImageIndex = -1;
    private volatile boolean isRunning = true;
    private Main main;
    private ApplicationSwitcher application;
    private final Group overlayGroup = new Group();

    public void setMain(Main main) {
        this.main = main;
    }

    public boolean isOverlayVisible() {
        return isOverlayVisible;
    }

    public void setOverlayVisible(boolean overlayVisible) {
        isOverlayVisible = overlayVisible;
    }

    public int getCurrentImageIndex() {
        return currentImageIndex;
    }

    public void setCurrentImageIndex(int currentImageIndex) {
        this.currentImageIndex = currentImageIndex;
    }

    public FlowPane getFilterPanel() {
        return filterPanel;
    }

    public void setFilterPanel(FlowPane filterPanel) {
        this.filterPanel = filterPanel;
    }

    /**
     * Sets the application instance for this controller.
     * @param application the Main instance of the application
     */
    @Override
    public void setApp(ApplicationSwitcher application) {
        this.application = application;
    }

    /**
     This function is called when the user takes a photo from the webcam.
     It saves the photo to a file and stores the file path in the database.
     After that, it redirects the user to the photo page.
     */
    @FXML
    private void takePhoto() {

        ApiService apiService = new ApiService();

        // Get the current image from the webcam
        Mat imageFrame = VideoUtils.getCurrentFrame(capture, faceDetector, webcam,
                                                    overlayGroup, currentImageIndex, isOverlayVisible);

        // Add the overlay to the imageFrame if a filter is added
        imageFrame = FilterUtils.addOverlayToImage(imageFrame, overlayGroup, webcam,
                                                            isOverlayVisible, currentImageIndex);

        // Save the image to a file
        Image image = ImageUtils.mat2Image(imageFrame);
        String filename = "images/image" + System.currentTimeMillis() + ".jpg";
        File imageFile = new File(filename);

        String absolutePath = imageFile.getAbsolutePath();

        try {
            Imgcodecs.imwrite(absolutePath, ImageUtils.image2Mat(image));

            // Replace backslashes with double backslashes for database storage
            absolutePath = imageFile.getAbsolutePath();
            absolutePath = absolutePath.replace("\\", "\\\\");

            // Save the image to the database
            try {

                long id = main.getUser().getId();

                ImageRequestDTO request = new ImageRequestDTO(id, absolutePath);

                String response = apiService.makePostRequest("http://localhost:8080/api/images", request);

                ObjectMapper objectMapper = new ObjectMapper();
                Photo photo = objectMapper.readValue(response, Photo.class);

                // Redirect the user to the photo page
                application.setStage((Stage) root.getScene().getWindow());
                application.loadPhotoPage(photo, false);


            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     Initializes the Face Detection Controller and sets the necessary actions for the buttons.
     Loads the OpenCV library and establishes the database connection.
     Loads the face detection classifier and sets the video capture properties.
     Starts a new thread to capture frames from the camera and updates the image view.
     Sets the actions for the logout button, save image button, and chat button.
     @param location the location of the FXML file used for initializing the controller
     @param resources the resource bundle used for initializing the controller
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        FilterUtils filter = new FilterUtils();
        filter.addFilters(this);

        try {

            // Load OpenCV library
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

            // Haar cascade path
            String path = "";

            File file = new File(path);

            // Load face detection classifier
            faceDetector = new CascadeClassifier(file.getAbsolutePath() +
                    "\\src\\main\\resources\\ar\\midtermproject\\XMLFiles\\haarcascades\\haarcascade_frontalface_default.xml");

            // Initialize image reference
            imageRef = new AtomicReference<>();

            // Open the default camera
            capture = new VideoCapture(WEBCAM_INDEX);
            capture.set(Videoio.CAP_PROP_FRAME_WIDTH, FRAME_SIZE.width);
            capture.set(Videoio.CAP_PROP_FRAME_HEIGHT, FRAME_SIZE.height);

            // Create a new thread to capture frames from the camera
            Thread thread = new Thread(() -> {
                while (isRunning) {
                    Mat imageFrame = new Mat();
                    if (capture.read(imageFrame)) {

                        splitPane.setDividerPositions(0.69);

                        // Get the current Frame
                        imageFrame = VideoUtils.getCurrentFrame(capture, faceDetector, webcam,
                                                                overlayGroup, currentImageIndex, isOverlayVisible);

                        // Convert the frame to an image and update the image view
                        Image image = ImageUtils.mat2Image(imageFrame);
                        imageRef.set(image);
                        webcam.setImage(image);
                        webcam.fitWidthProperty().bind(root.widthProperty());
                        webcam.fitHeightProperty().bind(root.heightProperty());
                        webcam.setPreserveRatio(true);

                        AnchorPane.setTopAnchor(filterPanel, rightPanel.getHeight() * 0.1);

                    }
                }
            });
            thread.setDaemon(true);
            thread.start();

            root.getChildren().addAll(overlayGroup);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadLogin() {
        isRunning = false; // Set the flag to stop the loop
        capture.release(); // Release the video capture
        application.setStage((Stage) root.getScene().getWindow());
        main.userLogout();
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close(); // Close the current page
    }

    @FXML
    private void loadChat() {
        application.setStage((Stage) root.getScene().getWindow());
        application.loadUserPage();
    }
    @FXML
    private void loadSavedImages() {
        application.setStage((Stage) root.getScene().getWindow());
        application.loadSavedImages();
    }

    @FXML
    private void loadProfile() {
        application.setStage((Stage) root.getScene().getWindow());
        application.loadProfilePage();
    }

    /**
     * Changes the cursor to a hand icon when the mouse hovers over the root node.
     */
    @FXML
    private void hovered() {
        Scene scene = root.getScene();
        scene.setCursor(Cursor.HAND);
    }

    /**
     * Changes the cursor back to the default icon when the mouse exits the root node.
     */
    @FXML
    private void exited() {
        Scene scene = root.getScene();
        scene.setCursor(Cursor.DEFAULT);
    }

}