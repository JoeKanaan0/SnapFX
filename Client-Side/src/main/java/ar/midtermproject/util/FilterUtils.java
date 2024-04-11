package ar.midtermproject.util;

import ar.midtermproject.controllers.FaceDetectionController;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Objects;

public class FilterUtils {

    boolean isOverlayVisible;
    int currentImageIndex;
    FaceDetectionController controller;

    public void setController (FaceDetectionController controller) {
        this.controller = controller;
    }

    public void setOverlayVisible(boolean overlayVisible) {
        isOverlayVisible = overlayVisible;
    }

    public void setCurrentImageIndex(int currentImageIndex) {
        this.currentImageIndex = currentImageIndex;
    }

    /**
     * Creates a tile pane with filter icons and adds it to the filter panel.
     */
    public void addFilters(FaceDetectionController controller) {

        setController(controller);
        FlowPane filterPanel = controller.getFilterPanel();
        setOverlayVisible(controller.isOverlayVisible());
        setCurrentImageIndex(controller.getCurrentImageIndex());

        // Create a tile pane to hold filter icons
        TilePane tilePane = new TilePane();
        tilePane.setHgap(10);
        tilePane.setVgap(10);
        tilePane.setPadding(new Insets(10));
        tilePane.setPrefColumns(2);

        int targetWidth = 305;
        int targetHeight = 170;

        // Loop through the filter icons and add them to the tile pane
        for (int i = 0; i < 5; i++) {
            Image image = new Image(Objects.requireNonNull(FilterUtils.class.getResourceAsStream("/ar/midtermproject/filters/icons/filter" + i + ".jpg")));

            // Resize the overlay image before passing it to the getCurrentFrame function
            Image resizedImage = resizeImage(image, targetWidth, targetHeight);
            ImageView imageView = new ImageView(resizedImage);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);

            final int index = i; // store the current index in a final variable for use in the event handler
            imageView.setOnMouseClicked(e -> loadImage(index));

            tilePane.getChildren().add(imageView);
        }

        // Bind the tile pane's preferred width and height to the filter panel's width and height
        tilePane.prefWidthProperty().bind(filterPanel.widthProperty());
        tilePane.prefHeightProperty().bind(filterPanel.heightProperty());

        // Add the tile pane to the filter panel
        filterPanel.getChildren().add(tilePane);

        controller.setFilterPanel(filterPanel);

    }

    private Image resizeImage(Image inputImage, int width, int height) {
        ImageView imageView = new ImageView(inputImage);
        imageView.setPreserveRatio(false);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);
        return imageView.snapshot(snapshotParameters, null);
    }

    /**
     * @param index The current index of the filter that was clicked on
     * This function changes the index and overlay to display the clicked image
     */
    private void loadImage(int index) {

        // If the clicked image index is the same as the current image index, disable the overlay and return
        if (currentImageIndex == index) {
            isOverlayVisible = false;
            currentImageIndex = -1;
            controller.setCurrentImageIndex(currentImageIndex);
            controller.setOverlayVisible(isOverlayVisible);
            return;
        }

        currentImageIndex = index; // Update the current image index
        isOverlayVisible = true;
        controller.setCurrentImageIndex(currentImageIndex);
        controller.setOverlayVisible(isOverlayVisible);
    }

    /**
     * This function adds the filer into the Image when a screenshot is taken, since
     * in the video the image is actually not a part of the frame, but it's overriding it.
     * So we call blendWithTransparency to blend the filter and the frame into a single image.
     */
    public static Mat addOverlayToImage(Mat imageFrame, Group overlayGroup, ImageView webcam,
                                        boolean isOverlayVisible, int currentImageIndex) {

        if (isOverlayVisible && currentImageIndex != -1) {
            for (javafx.scene.Node node : overlayGroup.getChildren()) {
                if (node instanceof ImageView faceOverlay) {

                    int x = (int) (faceOverlay.getX() * (imageFrame.width() / webcam.getBoundsInLocal().getWidth()));
                    int y = (int) (faceOverlay.getY() * (imageFrame.height() / webcam.getBoundsInLocal().getHeight()));
                    int width = (int) (faceOverlay.getFitWidth() * (imageFrame.width() / webcam.getBoundsInLocal().getWidth()));
                    int height = (int) (faceOverlay.getFitHeight() * (imageFrame.height() / webcam.getBoundsInLocal().getHeight()));

                    Mat overlayWithAlpha = ImageUtils.image2Mat(faceOverlay.getImage());

                    Imgproc.resize(overlayWithAlpha, overlayWithAlpha, new Size(width, height));

                    Rect roi = new Rect(x, y, width, height);
                    imageFrame = blendWithTransparency(imageFrame, overlayWithAlpha, roi);
                }
            }
        }

        return imageFrame;
    }

    /**
     * Blends the filter and the frame into a single image
     */
    private static Mat blendWithTransparency(Mat src, Mat overlay, Rect roi) {
        Mat result = new Mat();
        src.copyTo(result);

        for (int y = Math.max(roi.y, 0); y < Math.min(roi.y + roi.height, src.rows()); ++y) {
            for (int x = Math.max(roi.x, 0); x < Math.min(roi.x + roi.width, src.cols()); ++x) {
                double[] overlayPixel = overlay.get(y - roi.y, x - roi.x);
                double alpha = overlayPixel[3] / 255.0;
                double[] srcPixel = src.get(y, x);
                double[] blendedPixel = new double[srcPixel.length];
                for (int k = 0; k < srcPixel.length; ++k) {
                    blendedPixel[k] = overlayPixel[k] * alpha + srcPixel[k] * (1.0 - alpha);
                }
                result.put(y, x, blendedPixel);
            }
        }

        return result;
    }

}
