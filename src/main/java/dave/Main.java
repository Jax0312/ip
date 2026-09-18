package dave;

import java.io.IOException;

import dave.gui.MainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Main application class for the Dave GUI.
 */
public class Main extends Application {

    private final Dave dave = new Dave();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane anchorPane = fxmlLoader.load();
            Scene scene = new Scene(anchorPane);
            stage.setScene(scene);
            stage.setTitle("Dave - Personal Task Assistant");
            stage.setResizable(false);
            fxmlLoader.<MainWindow>getController().setDave(this.dave);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
