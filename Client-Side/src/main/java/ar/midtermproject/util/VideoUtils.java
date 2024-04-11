package ar.midtermproject.util;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import java.io.InputStream;

public class VideoUtils {

    /**
     * Retrieves the current frame from the video capture and applies face detection to it.
     * @return The current frame with rectangles drawn around detected faces
     */
    public static Mat getCurrentFrame(VideoCapture capture, CascadeClassifier faceDetector, ImageView webcam,
                                Group overlayGroup, int currentImageIndex, boolean isOverlayVisible) {

        // Create a new Mat to store the image frame
        Mat imageFrame = new Mat();

        // Read the next frame from the video capture
        capture.read(imageFrame);

        // Flip the image horizontally
        Core.flip(imageFrame, imageFrame, 1);

        // Detect faces in the frame using the face detector
        MatOfRect faces = new MatOfRect();
        faceDetector.detectMultiScale(imageFrame, faces);

        // Clear the overlayGroup
        Platform.runLater(() -> overlayGroup.getChildren().clear());

        // Draw rectangles around each detected face and apply the filter
        for (Rect rect : faces.toArray()) {
            Imgproc.rectangle(imageFrame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);

            // Update the position and size of the overlay image according to the detected face rectangle
            if (isOverlayVisible && currentImageIndex != -1) {
                Platform.runLater(() -> {
                    // Load the image using the path and the index
                    InputStream in = VideoUtils.class.getResourceAsStream("/ar/midtermproject/filters/images/image" + currentImageIndex + ".jpg");
                    assert in != null;
                    Image overlayImage = new Image(in);

                    // Create an ImageView for each detected face
                    ImageView faceOverlay = new ImageView(overlayImage);

                    double scaleX = webcam.getBoundsInLocal().getWidth() / (imageFrame.width());
                    double scaleY = webcam.getBoundsInLocal().getHeight() / (imageFrame.height());

                    faceOverlay.setX(rect.x * scaleX);
                    faceOverlay.setY(rect.y * scaleY);

                    switch (currentImageIndex) {
                        case 0 -> // Hats
                                setPosition(overlayImage, faceOverlay, rect, scaleX, scaleY, -0.1, -0.5, 0.8);
                        case 1 -> // Hairs
                                setPosition(overlayImage, faceOverlay, rect, scaleX, scaleY, -0.05, -0.36, 1);
                        case 2 -> // Glasses
                                setPosition(overlayImage, faceOverlay, rect, scaleX, scaleY, 0.08, 0.2, 0.45);
                        case 3 -> // Mustaches
                                setPosition(overlayImage, faceOverlay, rect, scaleX, scaleY, 0.15, 0.5, 0.45);
                        case 4 -> // Beards
                                setPosition(overlayImage, faceOverlay, rect, scaleX, scaleY, -0.165, 0.57, 1);
                    }

                    // Add the faceOverlay to the overlayGroup
                    overlayGroup.getChildren().add(faceOverlay);
                });
            }
        }

        // Return the current frame with rectangles drawn around detected faces
        return imageFrame;
    }

    /**
     * Sets the position of the filter on the overlayImage so that
     * it can be overlaid in the correct place for each image.
     */
    public static void setPosition(Image overlayImage, ImageView faceOverlay, Rect rect,
                                   double scaleX, double scaleY, double xPosition, double yPosition, double ratio) {

        double newHeight;
        double newWidth;

        faceOverlay.setX((rect.x + xPosition * rect.width) * scaleX);
        faceOverlay.setY((rect.y + yPosition * rect.height) * scaleY);
        newHeight = rect.height * scaleY * ratio;
        double aspectRatio = overlayImage.getWidth() / overlayImage.getHeight();
        newWidth = newHeight * aspectRatio;

        faceOverlay.setFitWidth(newWidth);
        faceOverlay.setFitHeight(newHeight);

    }

}