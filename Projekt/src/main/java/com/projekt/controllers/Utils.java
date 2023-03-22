package com.projekt.controllers;

import com.projekt.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Utils {
    private static Stage stage;
    @FXML
    public static void backButtonClick(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public static void passInfo(MouseEvent event, String chosenItem, String view) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(view));
        Scene scene = new Scene(loader.load());
        Controller controller;

        controller = loader.getController();
        controller.setInput(chosenItem);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    public static void passFromFavourites(MouseEvent event, String chosenItem, String view, ResultSet result,int row) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(view));
        Scene scene = new Scene(loader.load());
        Controller controller;
        controller = loader.getController();
        controller.setFromFavourites(result,row);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
