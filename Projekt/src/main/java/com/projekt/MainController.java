package com.projekt;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    Stage stage;



    private void changeScene(String viewName, ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(viewName));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    @FXML
    protected void filmsButtonClick(ActionEvent event) throws IOException {
        changeScene("films-view.fxml",event);
    }

    @FXML
    protected void peopleButtonClick(ActionEvent event) throws IOException {
        changeScene("people-view.fxml",event);
    }

    @FXML
    protected void planetsButtonClick(ActionEvent event) throws IOException {
        changeScene("planets-view.fxml",event);
    }

    @FXML
    protected void speciesButtonClick(ActionEvent event) throws IOException {
        changeScene("species-view.fxml",event);
    }

    @FXML
    protected void starshipsButtonClick(ActionEvent event) throws IOException {
        changeScene("starships-view.fxml",event);
    }

    @FXML
    protected void vehiclesButtonClick(ActionEvent event) throws IOException {
        changeScene("vehicles-view.fxml",event);
    }

    public void favouritesButtonClick(ActionEvent event) throws IOException {
        changeScene("favourite-view.fxml",event);
    }
}