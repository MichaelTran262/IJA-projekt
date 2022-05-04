package ija.project.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("Main.fxml"));
        primaryStage.getIcons().add(new Image("images/app_icon.png"));
        primaryStage.setTitle("Diagram editor");
        Scene scene = new Scene(root);
        String css = this.getClass().getClassLoader().getResource("application.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}