package ar.midtermproject.util;

import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class FXMLUtils {

    private ImageView backGroundImageView;
    private AnchorPane root;
    private VBox vBox;

    public ImageView getBackGroundImageView() {
        return backGroundImageView;
    }

    public void setBackGroundImageView(ImageView backGroundImageView) {
        this.backGroundImageView = backGroundImageView;
    }

    public AnchorPane getAnchorPane() {
        return root;
    }

    public void setAnchorPane(AnchorPane anchorPane) {
        this.root = anchorPane;
    }

    public VBox getvBox() {
        return vBox;
    }

    public void setvBox(VBox vBox) {
        this.vBox = vBox;
    }

    public void setBackground(ImageView backgroundImageView, AnchorPane root, VBox vBox) {

        // Set the background image for the AnchorPane
        backgroundImageView.setImage(new Image(Objects.requireNonNull(FXMLUtils.class.getResourceAsStream("/ar/midtermproject/ResourceImages/background.jpg"))));

        // Scale the background image to fit the size of the AnchorPane
        backgroundImageView.fitWidthProperty().bind(root.widthProperty());
        backgroundImageView.fitHeightProperty().bind(root.heightProperty());
        backgroundImageView.setPreserveRatio(false);

        // Set the background color for the VBox
        vBox.setStyle("-fx-background-color: #FFFFFF");

        // Set the initial size of the VBox
        vBox.setPrefWidth(root.getWidth() * 0.5);
        vBox.setPrefHeight(root.getHeight() * 0.8);

        // Use a ChangeListener to modify the VBox size when the screen size changes
        ChangeListener<Number> sizeListener = (observableValue, oldSize, newSize) -> {

            // Define the anchors for the VBox to be centered in the middle of the AnchorPane
            double topAnchor = root.getHeight() * 0.15;
            double bottomAnchor = root.getHeight() * 0.1;
            double leftAnchor = root.getWidth() * 0.35;
            double rightAnchor = root.getWidth() * 0.35;

            // Update the size of the VBox
            vBox.setPrefWidth(root.getWidth() * 0.5);
            vBox.setPrefHeight(root.getHeight() * 0.8);

            // Set the VBox to be centered in the middle of the AnchorPane
            AnchorPane.setTopAnchor(vBox, topAnchor);
            AnchorPane.setBottomAnchor(vBox, bottomAnchor);
            AnchorPane.setLeftAnchor(vBox, leftAnchor);
            AnchorPane.setRightAnchor(vBox, rightAnchor);
        };

        // Add a listener for changes in the size of the AnchorPane
        root.widthProperty().addListener(sizeListener);
        root.heightProperty().addListener(sizeListener);

        setBackGroundImageView(backgroundImageView);
        setvBox(vBox);
        setAnchorPane(root);

    }

}
