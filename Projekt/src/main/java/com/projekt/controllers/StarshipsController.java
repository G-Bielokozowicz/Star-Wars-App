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
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class StarshipsController implements Controller{
    com.projekt.api.api api = new api();
    com.projekt.api.apiController apiController = new apiController(api);
    Stage stage;
    private JsonArray results = null;
    private JsonObject starship = null;

    private JsonArray films = null;
    private ArrayList<String> filmsName = new ArrayList<String>();
    private JsonArray pilots = null;
    private ArrayList<String> pilotsName = new ArrayList<String>();
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
    private Label modelLabel;
    @FXML
    private Label manufacturerLabel;
    @FXML
    private Label costLabel;
    @FXML
    private Label lengthLabel;
    @FXML
    private Label maxAtmospheringSpeedLabel;
    @FXML
    private Label crewLabel;
    @FXML
    private Label passengersLabel;
    @FXML
    private Label cargoCapacityLabel;
    @FXML
    private Label hyperdriveRatingLabel;
    @FXML
    private Label starshipClassLabel;
    @FXML
    private ListView<String> filmsList;
    @FXML
    private ListView<String> pilotsList;
    @FXML
    private Button favouriteButton;
    @FXML
    protected void clear() {
        favouriteButton.setDisable(true);
        results = null;
        starship = null;
        filmsName.clear();
        pilotsName.clear();
        progressBar.setProgress(0);
        filmsList.getItems().clear();
        pilotsList.getItems().clear();
        nameLabel.setText("Name: ");
        modelLabel.setText("Model: ");
        manufacturerLabel.setText("Manufacturer: ");
        costLabel.setText("Cost: ");
        lengthLabel.setText("Length: ");
        maxAtmospheringSpeedLabel.setText("Max atmosphering speed: ");
        crewLabel.setText("Crew: ");
        passengersLabel.setText("Passengers: ");
        cargoCapacityLabel.setText("Cargo capacity: ");
        hyperdriveRatingLabel.setText("Hyperdrive rating: ");
        starshipClassLabel.setText("Starship class: ");
    }
    @FXML
    protected void showStarshipsButtonClick(ActionEvent event) {

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
                JsonObject jsonObject = apiController.get("starships", inputTextField.getText());
                results = jsonObject.getAsJsonArray("results");
                if (results.size() > 0) {
                    starship = results.get(0).getAsJsonObject();
                    pilots = starship.getAsJsonArray("pilots");
                    films = starship.getAsJsonArray("films");
                    totalSize = pilots.size() + films.size();
                    latch = new CountDownLatch(totalSize);
                    for (int i = 0; i < pilots.size(); i++) {
                        int finalI = i;
                        new Thread(() -> {
                            try {
                                JsonObject character = apiController.getFromURL(pilots.get(finalI).toString().replace("\"", ""));
                                pilotsName.add(character.get("name").toString().replace("\"", ""));
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
                    Collections.sort(pilotsName);
                    Collections.sort(filmsName);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                loading.setVisible(false);
                if (starship != null) {
                    favouriteButton.setDisable(false);
                    nameLabel.setText("Name: "+starship.get("name").toString().replace("\"", ""));
                    modelLabel.setText("Model: "+starship.get("model").toString().replace("\"", ""));
                    manufacturerLabel.setText("Manufacturer: "+starship.get("manufacturer").toString().replace("\"", ""));
                    costLabel.setText("Cost: "+starship.get("cost_in_credits").toString().replace("\"", ""));
                    lengthLabel.setText("Length: "+starship.get("length").toString().replace("\"", ""));
                    maxAtmospheringSpeedLabel.setText("Max atmosphering speed: "+starship.get("max_atmosphering_speed").toString().replace("\"", ""));
                    crewLabel.setText("Crew: "+starship.get("crew").toString().replace("\"", ""));
                    passengersLabel.setText("Passengers: "+starship.get("passengers").toString().replace("\"", ""));
                    cargoCapacityLabel.setText("Cargo capacity: "+starship.get("cargo_capacity").toString().replace("\"", ""));
                    hyperdriveRatingLabel.setText("Hyperdrive rating: "+starship.get("hyperdrive_rating").toString().replace("\"", ""));
                    starshipClassLabel.setText("Starship class: "+starship.get("starship_class").toString().replace("\"", ""));
                    pilotsList.getItems().addAll(pilotsName);
                    filmsList.getItems().addAll(filmsName);
                } else {
                    NoResultsMessage.showPopupMessage("Nothing found for given starship",(Stage)((Node)event.getSource()).getScene().getWindow());
                }
            });
        }).start();
    }

    @FXML
    protected void backButtonClick(ActionEvent event) throws IOException {
        Utils.backButtonClick(event);
    }

    public void onEnter(ActionEvent actionEvent) {
        showStarshipsButtonClick(actionEvent);
    }

    @Override
    public void setInput(String input){
        inputTextField.setText(input);
    }

    @Override
    public void setFromFavourites(ResultSet result,int row)throws SQLException {
        result.absolute(row);
        inputTextField.setText(result.getString("name").toString().replace("\"", ""));
        nameLabel.setText("Name: "+result.getString("name").toString().replace("\"", ""));
        modelLabel.setText("Model: "+result.getString("model").toString().replace("\"", ""));
        manufacturerLabel.setText("Manufacturer: "+result.getString("manufacturer").toString().replace("\"", ""));
        costLabel.setText("Cost: "+result.getString("cost").toString().replace("\"", ""));
        lengthLabel.setText("Length: "+result.getString("length").toString().replace("\"", ""));
        maxAtmospheringSpeedLabel.setText("Max atmosphering speed: "+result.getString("max_atmosphering_speed").toString().replace("\"", ""));
        crewLabel.setText("Crew: "+result.getString("crew").toString().replace("\"", ""));
        passengersLabel.setText("Passengers: "+result.getString("passengers").toString().replace("\"", ""));
        cargoCapacityLabel.setText("Cargo capacity: "+result.getString("cargo_capacity").toString().replace("\"", ""));
        hyperdriveRatingLabel.setText("Hyperdrive rating: "+result.getString("hyperdrive_rating").toString().replace("\"", ""));
        starshipClassLabel.setText("Starship class: "+result.getString("starship_class").toString().replace("\"", ""));
        pilotsList.getItems().addAll((Arrays.asList(result.getString("pilots").split(", "))));
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
                case "pilotsList":
                    chosenItem = pilotsList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent,chosenItem,"people-view.fxml");
                    break;

            }
        }
    }
//
    public void addToFavourites(ActionEvent actionEvent) throws SQLException {
//        Database db1=Database.getInstance();
//        Document testStarship = new Document()
//                .append("_id", new ObjectId())
//                .append("name", starship.get("name").toString().replace("\"", ""))
//                .append("model",starship.get("model").toString().replace("\"", ""))
//                .append("manufacturer",starship.get("manufacturer").toString().replace("\"", ""))
//                .append("cost",starship.get("cost_in_credits").toString().replace("\"", ""))
//                .append("length",starship.get("length").toString().replace("\"", ""))
//                .append("max_atmosphering_speed",starship.get("max_atmosphering_speed").toString().replace("\"", ""))
//                .append("crew",starship.get("crew").toString().replace("\"", ""))
//                .append("passengers",starship.get("passengers").toString().replace("\"", ""))
//                .append("cargo_capacity",starship.get("cargo_capacity").toString().replace("\"", ""))
//                .append("starship_class",starship.get("starship_class").toString().replace("\"", ""))
//                .append("hyperdrive_rating",starship.get("hyperdrive_rating").toString().replace("\"", ""))
//                .append("films", filmsName)
//                .append("pilots",pilotsName);
//        db1.insertStarship(testStarship);

        String name = starship.get("name").toString().replace("\"", "");
        String model = starship.get("model").toString().replace("\"", "");
        String manufacturer = starship.get("manufacturer").toString().replace("\"", "");
        String cost = starship.get("cost_in_credits").toString().replace("\"", "");
        String length = starship.get("length").toString().replace("\"", "");
        String max_atmosphering_speed = starship.get("max_atmosphering_speed").toString().replace("\"", "");
        String crew = starship.get("crew").toString().replace("\"", "");
        String passengers = starship.get("passengers").toString().replace("\"", "");
        String cargo_capacity = starship.get("cargo_capacity").toString().replace("\"", "");
        String hyperdrive_rating = starship.get("hyperdrive_rating").toString().replace("\"", "");
        String starship_class = starship.get("starship_class").toString().replace("\"", "");

        String dbPilots = pilotsName.toString().replace("[", "").replace("]", "");
        String dbFilms = filmsName.toString().replace("[", "").replace("]", "");

        Database db = Database.getInstance();
        db.insertIntoStarships(name,model,manufacturer,cost,length,max_atmosphering_speed,crew,passengers,cargo_capacity,hyperdrive_rating,starship_class,dbFilms,dbPilots);
    }

//    @Override
//    public void setFromFavourites(Document document) {
//        nameLabel.setText("Name: "+document.get("name").toString().replace("\"", ""));
//        modelLabel.setText("Model: "+document.get("model").toString().replace("\"", ""));
//        manufacturerLabel.setText("Manufacturer: "+document.get("manufacturer").toString().replace("\"", ""));
//        costLabel.setText("Cost: "+document.get("cost").toString().replace("\"", ""));
//        lengthLabel.setText("Length: "+document.get("length").toString().replace("\"", ""));
//        maxAtmospheringSpeedLabel.setText("Max atmosphering speed: "+document.get("max_atmosphering_speed").toString().replace("\"", ""));
//        crewLabel.setText("Crew: "+document.get("crew").toString().replace("\"", ""));
//        passengersLabel.setText("Passengers: "+document.get("passengers").toString().replace("\"", ""));
//        cargoCapacityLabel.setText("Cargo capacity: "+document.get("cargo_capacity").toString().replace("\"", ""));
//        hyperdriveRatingLabel.setText("Hyperdrive rating: "+document.get("hyperdrive_rating").toString().replace("\"", ""));
//        starshipClassLabel.setText("Starship class: "+document.get("starship_class").toString().replace("\"", ""));
//        pilotsList.getItems().addAll((ArrayList<String>) document.get("pilots"));
//        filmsList.getItems().addAll((ArrayList<String>) document.get("films"));
//    }
}
