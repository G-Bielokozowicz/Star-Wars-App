package com.projekt.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.projekt.Main;
import com.projekt.api.api;
import com.projekt.api.apiController;
import com.projekt.components.NoResultsMessage;
import com.projekt.database.Database;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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

public class FilmsController implements Controller {


    api api = new api();
    apiController apiController = new apiController(api);
    Stage stage;
    private JsonArray results = null;
    private JsonObject film = null;
    private JsonArray characters = null;
    private ArrayList<String> charactersName = new ArrayList<String>();
    private JsonArray starships = null;
    private ArrayList<String> starshipsName = new ArrayList<String>();
    private JsonArray planets = null;
    private ArrayList<String> planetsName = new ArrayList<String>();
    double progress = 0;
    int totalSize = 0;
    CountDownLatch latch;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private TextField inputTextField;
    @FXML
    private Label directorLabel;
    @FXML
    private Label producerLabel;
    @FXML
    private Label releaseDateLabel;

    @FXML
    private AnchorPane loading;
    @FXML
    private ListView<String> charactersList;

    @FXML
    private ListView<String> starshipsList;

    @FXML
    private ListView<String> planetsList;

    @FXML
    private Label titleLabel;
    @FXML
    private Button favouriteButton;

    @FXML
    protected void clear() {
        favouriteButton.setDisable(true);
        results = null;
        film = null;
        charactersName.clear();
        planetsName.clear();
        starshipsName.clear();
        progressBar.setProgress(0);
        charactersList.getItems().clear();
        starshipsList.getItems().clear();
        planetsList.getItems().clear();
        titleLabel.setText("Title: ");
        directorLabel.setText("Director: ");
        producerLabel.setText("Producers: ");
        releaseDateLabel.setText("Release date: ");
    }

    @FXML
    protected void showFilmsButtonClick(ActionEvent event) {


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
                JsonObject jsonObject = apiController.get("films", inputTextField.getText());
                results = jsonObject.getAsJsonArray("results");
                if (results.size() > 0) {
                    film = results.get(0).getAsJsonObject();
                    characters = film.getAsJsonArray("characters");
                    planets = film.getAsJsonArray("planets");
                    starships = film.getAsJsonArray("starships");
                    totalSize = characters.size() + planets.size() + starships.size();
                    latch = new CountDownLatch(totalSize);
                    for (int i = 0; i < characters.size(); i++) {
                        int finalI = i;
                        new Thread(() -> {
                            try {
                                JsonObject character = apiController.getFromURL(characters.get(finalI).toString().replace("\"", ""));
                                charactersName.add(character.get("name").toString().replace("\"", ""));
                                latch.countDown();
                                progress += 100.0/totalSize;
                                progressBar.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    for (int i = 0; i < planets.size(); i++) {
                        int finalI = i;
                        new Thread(() -> {
                            try {
                                JsonObject character = apiController.getFromURL(planets.get(finalI).toString().replace("\"", ""));
                                planetsName.add(character.get("name").toString().replace("\"", ""));
                                latch.countDown();
                                progress += 100.0/totalSize;
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
                                progress += 100.0/totalSize;
                                progressBar.setProgress(progress);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                    latch.await();
                    Collections.sort(charactersName);
                    Collections.sort(starshipsName);
                    Collections.sort(planetsName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                loading.setVisible(false);
                if (film != null) {
                    favouriteButton.setDisable(false);
                    titleLabel.setText("Title: " + film.get("title").toString().replace("\"", ""));
                    directorLabel.setText("Director: " + film.get("director").toString().replace("\"", ""));
                    producerLabel.setText("Producers: " + film.get("producer").toString().replace("\"", ""));
                    releaseDateLabel.setText("Release date: " + film.get("release_date").toString().replace("\"", ""));
                    charactersList.getItems().addAll(charactersName);
                    starshipsList.getItems().addAll(starshipsName);
                    planetsList.getItems().addAll(planetsName);
                } else {
                    NoResultsMessage.showPopupMessage("Nothing found for given film",(Stage)((Node)event.getSource()).getScene().getWindow());
                }
            });
        }).start();
    }

    @FXML
    public void onEnter(ActionEvent event) {
        showFilmsButtonClick(event);
    }

    @FXML
    protected void backButtonClick(ActionEvent event) throws IOException {
        Utils.backButtonClick(event);
    }

    @Override
    public void setInput(String input){
        inputTextField.setText(input);
    }

    @Override
    public void setFromFavourites(ResultSet result,int row) throws SQLException {
        result.absolute(row);
        inputTextField.setText(result.getString("name").toString().replace("\"", ""));
        titleLabel.setText("Title: " + result.getString("name").toString().replace("\"", ""));
        directorLabel.setText("Director: " + result.getString("director").toString().replace("\"", ""));
        producerLabel.setText("Producers: " + result.getString("producer").toString().replace("\"", ""));
        releaseDateLabel.setText("Release date: " + result.getString("release_date").toString().replace("\"", ""));
        charactersList.getItems().addAll((Arrays.asList(result.getString("people").split(", "))));
        starshipsList.getItems().addAll((Arrays.asList(result.getString("starships").split(", "))));
        planetsList.getItems().addAll((Arrays.asList(result.getString("planets").split(", "))));

    }

    public void onListClick(MouseEvent mouseEvent) throws IOException {
        if (mouseEvent.getClickCount()==2){
            String source = ((Control)mouseEvent.getSource()).getId();
            String chosenItem;
            switch(source){
                case "charactersList":
                    chosenItem = charactersList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent,chosenItem,"people-view.fxml");
                    break;
                case "starshipsList":
                    chosenItem = starshipsList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent,chosenItem,"starships-view.fxml");
                    break;
                case "planetsList":
                    chosenItem = planetsList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent,chosenItem,"planets-view.fxml");
                    break;

            }
        }
    }

    public void addToFavourites(ActionEvent actionEvent) throws SQLException {
//        Database db1=Database.getInstance();
//        Document testFilm = new Document()
//                .append("_id", new ObjectId())
//                .append("name", film.get("title").toString().replace("\"", ""))
//                .append("director",film.get("director").toString().replace("\"", ""))
//                .append("producer",film.get("producer").toString().replace("\"", ""))
//                .append("release_date",film.get("release_date").toString().replace("\"", ""))
//                .append("people", charactersName)
//                .append("starships",starshipsName)
//                .append("planets",planetsName);
//        db1.insertFilm(testFilm);
        String name = film.get("title").toString().replace("\"", "");
        String director = film.get("director").toString().replace("\"", "");
        String producer = film.get("producer").toString().replace("\"", "");
        String release_date = film.get("release_date").toString().replace("\"", "");
        String dbCharacters = charactersName.toString().replace("[", "").replace("]", "");
        String dbStarships = starshipsName.toString().replace("[", "").replace("]", "");
        String dbPlanets = planetsName.toString().replace("[", "").replace("]", "");
        Database db = Database.getInstance();
        db.insertIntoFilms(name,director,producer,release_date,dbCharacters,dbStarships,dbPlanets);
    }

//    public void setFromFavourites(Document document){
//        inputTextField.setText(document.get("name").toString().replace("\"", ""));
//        titleLabel.setText("Title: " + document.get("name").toString().replace("\"", ""));
//        directorLabel.setText("Director: " + document.get("director").toString().replace("\"", ""));
//        producerLabel.setText("Producers: " + document.get("producer").toString().replace("\"", ""));
//        releaseDateLabel.setText("Release date: " + document.get("release_date").toString().replace("\"", ""));
//        charactersList.getItems().addAll((ArrayList<String>) document.get("people"));
//        starshipsList.getItems().addAll((ArrayList<String>) document.get("starships"));
//        planetsList.getItems().addAll((ArrayList<String>) document.get("planets"));
//
//    }
}
