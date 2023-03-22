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
import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

public class VehiclesController implements Controller{
    com.projekt.api.api api = new api();
    com.projekt.api.apiController apiController = new apiController(api);
    Stage stage;
    private JsonArray results = null;
    private JsonObject vehicle = null;

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
    private Label vehicleClassLabel;
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
        vehicle = null;
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
        vehicleClassLabel.setText("Vehicle class: ");
    }
    @FXML
    protected void showVehiclesButtonClick(ActionEvent event) {
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
                JsonObject jsonObject = apiController.get("vehicles", inputTextField.getText());
                results = jsonObject.getAsJsonArray("results");
                if (results.size() > 0) {
                    vehicle = results.get(0).getAsJsonObject();
                    pilots = vehicle.getAsJsonArray("pilots");
                    films = vehicle.getAsJsonArray("films");
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
                if (vehicle != null) {
                    nameLabel.setText("Name: "+vehicle.get("name").toString().replace("\"", ""));
                    modelLabel.setText("Model: "+vehicle.get("model").toString().replace("\"", ""));
                    manufacturerLabel.setText("Manufacturer: "+vehicle.get("manufacturer").toString().replace("\"", ""));
                    costLabel.setText("Cost: "+vehicle.get("cost_in_credits").toString().replace("\"", ""));
                    lengthLabel.setText("Length: "+vehicle.get("length").toString().replace("\"", ""));
                    maxAtmospheringSpeedLabel.setText("Max atmosphering speed: "+vehicle.get("max_atmosphering_speed").toString().replace("\"", ""));
                    crewLabel.setText("Crew: "+vehicle.get("crew").toString().replace("\"", ""));
                    passengersLabel.setText("Passengers: "+vehicle.get("passengers").toString().replace("\"", ""));
                    cargoCapacityLabel.setText("Cargo capacity: "+vehicle.get("cargo_capacity").toString().replace("\"", ""));
                    vehicleClassLabel.setText("Vehicle class: "+vehicle.get("vehicle_class").toString().replace("\"", ""));
                    pilotsList.getItems().addAll(pilotsName);
                    filmsList.getItems().addAll(filmsName);
                    favouriteButton.setDisable(false);
                } else {
                    NoResultsMessage.showPopupMessage("Nothing found for given vehicle",(Stage)((Node)event.getSource()).getScene().getWindow());
                }
            });
        }).start();
    }

    @FXML
    protected void backButtonClick(ActionEvent event) throws IOException {
        Utils.backButtonClick(event);
    }

    public void onEnter(ActionEvent actionEvent) {
        showVehiclesButtonClick(actionEvent);
    }

    @Override
    public void setInput(String input){
        inputTextField.setText(input);
    }

    @Override
    public void setFromFavourites(ResultSet result,int row) throws SQLException {
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
        vehicleClassLabel.setText("Vehicle class: "+result.getString("vehicle_class").toString().replace("\"", ""));
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

    public void addToFavourites(ActionEvent actionEvent) throws SQLException {
//        Database db1=Database.getInstance();
//        Document testVehicle = new Document()
//                .append("_id", new ObjectId())
//                .append("name", vehicle.get("name").toString().replace("\"", ""))
//                .append("model",vehicle.get("model").toString().replace("\"", ""))
//                .append("manufacturer",vehicle.get("manufacturer").toString().replace("\"", ""))
//                .append("cost",vehicle.get("cost_in_credits").toString().replace("\"", ""))
//                .append("length",vehicle.get("length").toString().replace("\"", ""))
//                .append("max_atmosphering_speed",vehicle.get("max_atmosphering_speed").toString().replace("\"", ""))
//                .append("crew",vehicle.get("crew").toString().replace("\"", ""))
//                .append("passengers",vehicle.get("passengers").toString().replace("\"", ""))
//                .append("cargo_capacity",vehicle.get("cargo_capacity").toString().replace("\"", ""))
//                .append("vehicle_class",vehicle.get("vehicle_class").toString().replace("\"", ""))
//                .append("films", filmsName)
//                .append("pilots",pilotsName);
//        db1.insertVehicle(testVehicle);


        String name = vehicle.get("name").toString().replace("\"", "");
        String model = vehicle.get("model").toString().replace("\"", "");
        String manufacturer = vehicle.get("manufacturer").toString().replace("\"", "");
        String cost = vehicle.get("cost_in_credits").toString().replace("\"", "");
        String length = vehicle.get("length").toString().replace("\"", "");
        String max_atmosphering_speed = vehicle.get("max_atmosphering_speed").toString().replace("\"", "");
        String crew = vehicle.get("crew").toString().replace("\"", "");
        String passengers = vehicle.get("passengers").toString().replace("\"", "");
        String cargo_capacity = vehicle.get("cargo_capacity").toString().replace("\"", "");
        String vehicle_class = vehicle.get("vehicle_class").toString().replace("\"", "");

        String dbPilots = pilotsName.toString().replace("[", "").replace("]", "");
        String dbFilms = filmsName.toString().replace("[", "").replace("]", "");
        Database db = Database.getInstance();
        db.insertIntoVehicles(name,model,manufacturer,cost,length,max_atmosphering_speed,crew,passengers,cargo_capacity,vehicle_class,dbFilms,dbPilots);
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
//        vehicleClassLabel.setText("Vehicle class: "+document.get("vehicle_class").toString().replace("\"", ""));
//        pilotsList.getItems().addAll((ArrayList<String>) document.get("pilots"));
//        filmsList.getItems().addAll((ArrayList<String>) document.get("films"));
//    }
}
