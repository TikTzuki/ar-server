package org.override;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;


public class AcademicResultsApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        URL resouce = getClass().getResource("main-view.fxml");

        System.out.println(resouce);

        FXMLLoader fxmlLoader = new FXMLLoader(resouce);

        BorderPane pane = fxmlLoader.load();
        Scene scene = new Scene(pane);

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/education/alarm-clock.png"))));
        stage.setTitle("Have a good day, sweetie!!!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        AcademicResultsApplication.launch(args);
    }
}