package dave.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Custom control representing a dialog box containing an avatar and a text message.
 */
public class DialogBox extends HBox {

    /** Radius in pixels for avatar clipping circles. */
    private static final double AVATAR_RADIUS = 37.5;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Constructs a new DialogBox with the specified text message and display image.
     *
     * @param text Text message to display.
     * @param img Image to use as the avatar, or null if no avatar is available.
     */
    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        setAlignment(Pos.CENTER_RIGHT);
        this.dialog.setText(text);
        if (img != null) {
            this.displayPicture.setImage(img);
            clipAvatar(img);
        } else {
            this.displayPicture.setVisible(false);
            this.displayPicture.setManaged(false);
        }
    }

    /**
     * Clips the display picture as a circle centered on the avatar.
     * If the avatar image is non-square, dynamically computes viewport/center to prevent clipping.
     *
     * @param img Avatar image to clip.
     */
    private void clipAvatar(Image img) {
        Circle clip = new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS);
        this.displayPicture.setClip(clip);
    }

    /**
     * Flips the dialog box such that the image is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.CENTER_LEFT);
        this.dialog.getStyleClass().add("reply-label");
    }

    /**
     * Creates a dialog box representing the user's message.
     *
     * @param text Text sent by the user.
     * @param img Avatar image of the user.
     * @return DialogBox configured for user input.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    /**
     * Creates a dialog box representing Dave's response message.
     *
     * @param text Text response from Dave.
     * @param img Avatar image of Dave.
     * @return DialogBox configured for Dave's response.
     */
    public static DialogBox getDaveDialog(String text, Image img) {
        DialogBox db = new DialogBox(text, img);
        db.flip();
        return db;
    }
}
