package org.override.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.override.AcademicResultsApplication;

import java.io.IOException;
import java.net.URL;

public class MainController {
    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private void handleShowView(ActionEvent e) {
        String view = (String) ((Button) e.getSource()).getUserData();
        loadFXML(AcademicResultsApplication.class.getResource(view));
    }

    private void loadFXML(URL url) {
        try {
            FXMLLoader loader = new FXMLLoader(url);
            mainBorderPane.setCenter(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
//            e.printStackTrace();
        }
    }
}
