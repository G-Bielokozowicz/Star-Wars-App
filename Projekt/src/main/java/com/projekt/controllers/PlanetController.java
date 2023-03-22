package com.projekt.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.projekt.api.api;
import com.projekt.api.apiController;
import com.projekt.components.NoResultsMessage;
import com.projekt.database.Database;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;


public class PlanetController implements Controller {
    api api = new api();
    apiController apiController = new apiController(api);
    Stage stage;
    private JsonArray results = null;
    private JsonObject planet = null;

    private JsonArray films = null;
    private ArrayList<String> filmsName = new ArrayList<String>();
    private JsonArray residents = null;
    private ArrayList<String> residentsName = new ArrayList<String>();
    double progress = 0;
    int totalSize = 0;
    CountDownLatch latch;

    @FXML
    private AnchorPane loading;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextField inputTextField;
    @FXML
    private Label nameLabel;
    @FXML
    private Label rotationPeriodLabel;
    @FXML
    private Label orbitalPeriodLabel;
    @FXML
    private Label diameterLabel;
    @FXML
    private Label climateLabel;
    @FXML
    private Label gravityLabel;
    @FXML
    private Label terrainLabel;
    @FXML
    private Label populationLabel;
    @FXML
    private ListView<String> filmsList;
    @FXML
    private ListView<String> residentsList;

    @FXML
    private Button favouriteButton;


    @FXML
    protected void clear() {
        favouriteButton.setDisable(true);
        results = null;
        planet = null;
        filmsName.clear();
        residentsName.clear();
        progressBar.setProgress(0);
        filmsList.getItems().clear();
        residentsList.getItems().clear();
        nameLabel.setText("Name: ");
        rotationPeriodLabel.setText("Rotation period: ");
        orbitalPeriodLabel.setText("Orbital period: ");
        diameterLabel.setText("Diameter: ");
        climateLabel.setText("Climate: ");
        gravityLabel.setText("Gravity: ");
        terrainLabel.setText("Terrain: ");
        populationLabel.setText("Population: ");
    }

    @FXML
    protected void showPlanetsButtonClick(ActionEvent event) throws InterruptedException {
        clear();
        new Thread(() -> {
            try {
                RotateTransition rotate = new RotateTransition();
                rotate.setNode(loading);
                rotate.setDuration(Duration.millis(1000));
                rotate.setByAngle(360);
                rotate.setCycleCount(TranslateTransition.INDEFINITE);
                rotate.play();
                loading.setVisible(true);
                JsonObject jsonObject = apiController.get("planets", inputTextField.getText());
                results = jsonObject.getAsJsonArray("results");
                if (results.size() > 0) {
                    planet = results.get(0).getAsJsonObject();
                    residents = planet.getAsJsonArray("residents");
                    films = planet.getAsJsonArray("films");
                    totalSize = residents.size() + films.size();
                    latch = new CountDownLatch(totalSize);
                    for (int i = 0; i < residents.size(); i++) {
                        int finalI = i;
                        new Thread(() -> {
                            try {
                                JsonObject character = apiController.getFromURL(residents.get(finalI).toString().replace("\"", ""));
                                residentsName.add(character.get("name").toString().replace("\"", ""));
                                latch.countDown();
                                progress += 100.0/totalSize;
                                progressBar.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    for (int i = 0; i < films.size(); i++) {
                        int finalI = i;
                        new Thread(() -> {
                            try {
                                JsonObject film = apiController.getFromURL(films.get(finalI).toString().replace("\"", ""));
                                filmsName.add(film.get("title").toString().replace("\"", ""));
                                latch.countDown();
                                progress += 100.0/totalSize;
                                progressBar.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    latch.await();
                    Collections.sort(residentsName);
                    Collections.sort(filmsName);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                loading.setVisible(false);
                if (planet != null) {
                    favouriteButton.setDisable(false);
                    nameLabel.setText("Name: "+planet.get("name").toString().replace("\"", ""));
                    rotationPeriodLabel.setText("Rotation period: "+planet.get("rotation_period").toString().replace("\"", "")+" standard hours");
                    orbitalPeriodLabel.setText("Orbital period: "+planet.get("orbital_period").toString().replace("\"", "")+ " standard days");
                    diameterLabel.setText("Diameter: "+planet.get("diameter").toString().replace("\"", "")+" km");
                    climateLabel.setText("Climate: "+planet.get("climate").toString().replace("\"", ""));
                    gravityLabel.setText("Gravity: "+planet.get("gravity").toString().replace("\"", ""));
                    terrainLabel.setText("Terrain: "+planet.get("terrain").toString().replace("\"", ""));
                    populationLabel.setText("Population: "+planet.get("population").toString().replace("\"", ""));
                    residentsList.getItems().addAll(residentsName);
                    filmsList.getItems().addAll(filmsName);
                } else {
                    NoResultsMessage.showPopupMessage("Nothing found for given planet",(Stage)((Node)event.getSource()).getScene().getWindow());
                }
            });
        }).start();
    }
    @FXML
    protected void backButtonClick(ActionEvent event) throws IOException {
        Utils.backButtonClick(event);
    }

    public void onEnter(ActionEvent actionEvent) throws InterruptedException {
        showPlanetsButtonClick(actionEvent);
    }

    @Override
    public void setInput(String input){
        inputTextField.setText(input);
    }

    @Override
    public void setFromFavourites(ResultSet result,int row) throws SQLException{
        result.absolute(row);
        inputTextField.setText(result.getString("name").toString().replace("\"", ""));
        nameLabel.setText("Name: "+result.getString("name").toString().replace("\"", ""));
        rotationPeriodLabel.setText("Rotation period: "+result.getString("rotation_period").toString().replace("\"", "")+" standard hours");
        orbitalPeriodLabel.setText("Orbital period: "+result.getString("orbital_period").toString().replace("\"", "")+ " standard days");
        diameterLabel.setText("Diameter: "+result.getString("diameter").toString().replace("\"", "")+" km");
        climateLabel.setText("Climate: "+result.getString("climate").toString().replace("\"", ""));
        gravityLabel.setText("Gravity: "+result.getString("gravity").toString().replace("\"", ""));
        terrainLabel.setText("Terrain: "+result.getString("terrain").toString().replace("\"", ""));
        populationLabel.setText("Population: "+result.getString("population").toString().replace("\"", ""));
        residentsList.getItems().addAll((Arrays.asList(result.getString("residents").split(", "))));
        filmsList.getItems().addAll((Arrays.asList(result.getString("films").split(", "))));

    }


    public void onListClick(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount()==2){
            String source = ((Control)mouseEvent.getSource()).getId();
            String chosenItem;
            switch(source){
                case "filmsList":
                    chosenItem = filmsList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent,chosenItem,"films-view.fxml");
                    break;
                case "residentsList":
                    chosenItem = residentsList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent,chosenItem,"people-view.fxml");
                    break;


            }
        }
    }

    public void addToFavourites(ActionEvent actionEvent) throws SQLException {
//        Database db1=Database.getInstance();
//        Document testplanet = new Document()
//                .append("_id", new ObjectId())
//                .append("name", planet.get("name").toString().replace("\"", ""))
//                .append("rotation_period",planet.get("rotation_period").toString().replace("\"", ""))
//                .append("orbital_period",planet.get("orbital_period").toString().replace("\"", ""))
//                .append("diameter",planet.get("diameter").toString().replace("\"", ""))
//                .append("climate",planet.get("climate").toString().replace("\"", ""))
//                .append("gravity",planet.get("gravity").toString().replace("\"", ""))
//                .append("terrain",planet.get("terrain").toString().replace("\"", ""))
//                .append("population",planet.get("population").toString().replace("\"", ""))
//                .append("films", filmsName)
//                .append("residents",residentsName);
//        db1.insertPlanet(testplanet);
        String name = planet.get("name").toString().replace("\"", "");
        String rotation_period = planet.get("rotation_period").toString().replace("\"", "");
        String orbital_period = planet.get("orbital_period").toString().replace("\"", "");
        String diameter = planet.get("diameter").toString().replace("\"", "");
        String climate = planet.get("climate").toString().replace("\"", "");
        String gravity = planet.get("gravity").toString().replace("\"", "");
        String terrain = planet.get("terrain").toString().replace("\"", "");
        String population = planet.get("population").toString().replace("\"", "");
        String dbResidents = residentsName.toString().replace("[", "").replace("]", "");
        String dbFilms = filmsName.toString().replace("[", "").replace("]", "");
        Database db = Database.getInstance();
        db.insertIntoPlanets(name,rotation_period,orbital_period,diameter,climate,gravity,terrain,population,dbFilms,dbResidents);
    }

//    @Override
//    public void setFromFavourites(Document document) {
//        nameLabel.setText("Name: "+document.get("name").toString().replace("\"", ""));
//        rotationPeriodLabel.setText("Rotation period: "+document.get("rotation_period").toString().replace("\"", "")+" standard hours");
//        orbitalPeriodLabel.setText("Orbital period: "+document.get("orbital_period").toString().replace("\"", "")+ " standard days");
//        diameterLabel.setText("Diameter: "+document.get("diameter").toString().replace("\"", "")+" km");
//        climateLabel.setText("Climate: "+document.get("climate").toString().replace("\"", ""));
//        gravityLabel.setText("Gravity: "+document.get("gravity").toString().replace("\"", ""));
//        terrainLabel.setText("Terrain: "+document.get("terrain").toString().replace("\"", ""));
//        populationLabel.setText("Population: "+document.get("population").toString().replace("\"", ""));
//        residentsList.getItems().addAll((ArrayList<String>) document.get("residents"));
//        filmsList.getItems().addAll((ArrayList<String>) document.get("films"));
//    }
}
