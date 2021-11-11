package org.override;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        Text text = new Text(50, 150, "-------->");
        Group root = new Group();

        ObservableList list = root.getChildren();
        list.add(text);

        Scene scene = new Scene(root, 600, 300);
        scene.setFill(Color.BEIGE);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
    }
}