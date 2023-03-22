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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class PeopleController implements Controller {


    com.projekt.api.api api = new api();
    com.projekt.api.apiController apiController = new apiController(api);
    Stage stage;
    private JsonArray results = null;
    private JsonObject character = null;


    private JsonArray films = null;
    private ArrayList<String> filmsName = new ArrayList<String>();
    private JsonArray starships = null;
    private ArrayList<String> starshipsName = new ArrayList<String>();
    private JsonArray vehicles = null;
    private ArrayList<String> vehiclesName = new ArrayList<String>();
    private JsonObject homeworld = null;

    double progress = 0;
    int totalSize = 0;
    CountDownLatch latch;

    @FXML
    private AnchorPane loading;

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label nameLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private Label hairColorLabel;
    @FXML
    private Label eyeColorLabel;
    @FXML
    private Label birthYearLabel;
    @FXML
    private Label genderLabel;
    @FXML
    private Label homeworldLabel;
    @FXML
    private TextField inputTextField;
    @FXML
    private ListView<String> filmsList;

    @FXML
    private ListView<String> starshipsList;

    @FXML
    private ListView<String> vehiclesList;
    @FXML
    private Button favouriteButton;

    @FXML
    protected void clear() {
        favouriteButton.setDisable(true);
        results = null;
        character = null;
        filmsName.clear();
        vehiclesName.clear();
        starshipsName.clear();
        homeworld = null;
        progressBar.setProgress(0);
        filmsList.getItems().clear();
        starshipsList.getItems().clear();
        vehiclesList.getItems().clear();
        nameLabel.setText("Name: ");
        heightLabel.setText("Height: ");
        hairColorLabel.setText("Hair color: ");
        eyeColorLabel.setText("Eye color: ");
        birthYearLabel.setText("Birth year: ");
        genderLabel.setText("Gender: ");
        homeworldLabel.setText("Homeworld: ");
    }

    @FXML
    protected void showPeopleButtonClick(ActionEvent event) {
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
                JsonObject jsonObject = apiController.get("people", inputTextField.getText());
                results = jsonObject.getAsJsonArray("results");
                if (results.size() > 0) {
                    character = results.get(0).getAsJsonObject();
                    films = character.getAsJsonArray("films");
                    vehicles = character.getAsJsonArray("vehicles");
                    starships = character.getAsJsonArray("starships");
                    totalSize = vehicles.size() + films.size() + starships.size();
                    latch = new CountDownLatch(totalSize);
                    homeworld = apiController.getFromURL(character.get("homeworld").toString().replace("\"", ""));
                    for (int i = 0; i < films.size(); i++) {
                        int finalI = i;
                        new Thread(() -> {
                            try {
                                JsonObject film = apiController.getFromURL(films.get(finalI).toString().replace("\"", ""));
                                filmsName.add(film.get("title").toString().replace("\"", ""));
                                latch.countDown();
                                progress += 100.0 / totalSize;
                                progressBar.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    for (int i = 0; i < vehicles.size(); i++) {
                        int finalI = i;
                        new Thread(() -> {
                            try {
                                JsonObject vehicle = apiController.getFromURL(vehicles.get(finalI).toString().replace("\"", ""));
                                vehiclesName.add(vehicle.get("name").toString().replace("\"", ""));
                                latch.countDown();
                                progress += 100.0 / totalSize;

                                progressBar.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    for (int i = 0; i < starships.size(); i++) {
                        int finalI = i;
                        new Thread(() -> {
                            try {
                                JsonObject character = apiController.getFromURL(starships.get(finalI).toString().replace("\"", ""));
                                starshipsName.add(character.get("name").toString().replace("\"", ""));
                                latch.countDown();
                                progress += 100.0 / totalSize;

                                progressBar.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    latch.await();
                    rotate.stop();
                    Collections.sort(filmsName);
                    Collections.sort(starshipsName);
                    Collections.sort(vehiclesName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                loading.setVisible(false);
                if (character != null) {
                    favouriteButton.setDisable(false);
                    nameLabel.setText("Name: " + character.get("name").toString().replace("\"", ""));
                    heightLabel.setText("Height: " + character.get("height").toString().replace("\"", "") + " cm");
                    hairColorLabel.setText("Hair: " + character.get("hair_color").toString().replace("\"", ""));
                    eyeColorLabel.setText("Eye color: " + character.get("eye_color").toString().replace("\"", ""));
                    birthYearLabel.setText("Birth year: " + character.get("birth_year").toString().replace("\"", ""));
                    genderLabel.setText("Gender: " + character.get("gender").toString().replace("\"", ""));
                    homeworldLabel.setText("Homeworld: " + homeworld.get("name").toString().replace("\"", ""));
                    filmsList.getItems().addAll(filmsName);
                    starshipsList.getItems().addAll(starshipsName);
                    vehiclesList.getItems().addAll(vehiclesName);
                } else {
                    NoResultsMessage.showPopupMessage("Nothing found for given character",(Stage)((Node)event.getSource()).getScene().getWindow());
                }
            });
        }).start();
    }

    @FXML
    public void onEnter(ActionEvent event) {
        showPeopleButtonClick(event);
    }

    @FXML
    protected void backButtonClick(ActionEvent event) throws IOException {
        Utils.backButtonClick(event);
    }

    @Override
    public void setInput(String input) {
        inputTextField.setText(input);
    }

    @Override
    public void setFromFavourites(ResultSet result,int row) throws SQLException {
        result.absolute(row);
        inputTextField.setText(result.getString("name").toString().replace("\"", ""));
        nameLabel.setText("Name: " + result.getString("name").toString().replace("\"", ""));
        heightLabel.setText("Height: " + result.getString("height").toString().replace("\"", "") + " cm");
        hairColorLabel.setText("Hair: " + result.getString("hair_color").toString().replace("\"", ""));
        eyeColorLabel.setText("Eye color: " + result.getString("eye_color").toString().replace("\"", ""));
        birthYearLabel.setText("Birth year: " + result.getString("birth_year").toString().replace("\"", ""));
        genderLabel.setText("Gender: " + result.getString("gender").toString().replace("\"", ""));
        homeworldLabel.setText("Homeworld: " + result.getString("homeworld").toString().replace("\"", ""));
        filmsList.getItems().addAll((Arrays.asList(result.getString("films").split(", "))));
        starshipsList.getItems().addAll((Arrays.asList(result.getString("starships").split(", "))));
        vehiclesList.getItems().addAll((Arrays.asList(result.getString("vehicles").split(", "))));
    }


    public void onListClick(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount() == 2) {
            String source = ((Control) mouseEvent.getSource()).getId();
            String chosenItem;
            switch (source) {
                case "filmsList":
                    chosenItem = filmsList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent, chosenItem, "films-view.fxml");
                    break;
                case "starshipsList":
                    chosenItem = starshipsList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent, chosenItem, "starships-view.fxml");
                    break;
                case "vehiclesList":
                    chosenItem = vehiclesList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent, chosenItem, "vehicles-view.fxml");
                    break;

            }
        }
    }

    public void addToFavourites(ActionEvent actionEvent) throws SQLException {

        String name = character.get("name").toString().replace("\"", "");
        String height = character.get("height").toString().replace("\"", "");
        String hair_color = character.get("hair_color").toString().replace("\"", "");
        String eye_color = character.get("eye_color").toString().replace("\"", "");
        String birth_year = character.get("birth_year").toString().replace("\"", "");
        String gender = character.get("gender").toString().replace("\"", "");
        String home = homeworld.get("name").toString().replace("\"", "");
        String dbFilms = filmsName.toString().replace("[", "").replace("]", "");
        String dbStarships = starshipsName.toString().replace("[", "").replace("]", "");
        String dbVehicles = vehiclesName.toString().replace("[", "").replace("]", "");
        Database db = Database.getInstance();
        db.insertIntoPeople(name,height,hair_color,eye_color,birth_year,gender,home,dbFilms,dbStarships,dbVehicles);
    }

//    @Override
//    public void setFromFavourites(Document document) {
//        nameLabel.setText("Name: " + document.get("name").toString().replace("\"", ""));
//        heightLabel.setText("Height: " + document.get("height").toString().replace("\"", "") + " cm");
//        hairColorLabel.setText("Hair: " + document.get("hair_color").toString().replace("\"", ""));
//        eyeColorLabel.setText("Eye color: " + document.get("eye_color").toString().replace("\"", ""));
//        birthYearLabel.setText("Birth year: " + document.get("birth_year").toString().replace("\"", ""));
//        genderLabel.setText("Gender: " + document.get("gender").toString().replace("\"", ""));
//        homeworldLabel.setText("Homeworld: " + document.get("name").toString().replace("\"", ""));
//        filmsList.getItems().addAll((ArrayList<String>) document.get("films"));
//        starshipsList.getItems().addAll((ArrayList<String>) document.get("starships"));
//        vehiclesList.getItems().addAll((ArrayList<String>) document.get("vehicles"));
//    }
}