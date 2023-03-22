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

public class SpeciesController implements Controller {
    com.projekt.api.api api = new api();
    com.projekt.api.apiController apiController = new apiController(api);
    Stage stage;
    private JsonArray results = null;
    private JsonObject species = null;

    private JsonArray films = null;
    private ArrayList<String> filmsName = new ArrayList<String>();
    private JsonArray people = null;
    private ArrayList<String> peopleName = new ArrayList<String>();
    private JsonObject homeworld=null;
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
    private Label classificationLabel;
    @FXML
    private Label designationLabel;
    @FXML
    private Label averageHeightLabel;
    @FXML
    private Label skinColorsLabel;
    @FXML
    private Label hairColorsLabel;
    @FXML
    private Label averageLifespanLabel;
    @FXML
    private Label homeworldLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private ListView<String> filmsList;
    @FXML
    private ListView<String> peopleList;
    @FXML
    private Button favouriteButton;
    @FXML
    protected void clear() {
        favouriteButton.setDisable(true);
        results = null;
        species = null;
        homeworld=null;
        filmsName.clear();
        peopleName.clear();
        progressBar.setProgress(0);
        filmsList.getItems().clear();
        peopleList.getItems().clear();
        nameLabel.setText("Name: ");
        classificationLabel.setText("Classification: ");
        designationLabel.setText("Designation: ");
        averageHeightLabel.setText("Average height: ");
        skinColorsLabel.setText("Skin colors: ");
        hairColorsLabel.setText("Hair colors: ");
        averageLifespanLabel.setText("Average lifespan: ");
        homeworldLabel.setText("Homeworld: ");
        languageLabel.setText("Language: ");
    }
    @FXML
    protected void showSpeciesButtonClick(ActionEvent event) {
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
                JsonObject jsonObject = apiController.get("species", inputTextField.getText());
                results = jsonObject.getAsJsonArray("results");
                if (results.size() > 0) {
                    species = results.get(0).getAsJsonObject();
                    people = species.getAsJsonArray("people");
                    films = species.getAsJsonArray("films");
                    totalSize = people.size() + films.size();
                    latch = new CountDownLatch(totalSize);
                    if (!species.get("homeworld").isJsonNull()){
                        homeworld = apiController.getFromURL(species.get("homeworld").toString().replace("\"", ""));
                    }

                    for (int i = 0; i < people.size(); i++) {
                        int finalI = i;
                        new Thread(() -> {
                            try {
                                JsonObject character = apiController.getFromURL(people.get(finalI).toString().replace("\"", ""));
                                peopleName.add(character.get("name").toString().replace("\"", ""));
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
                    Collections.sort(peopleName);
                    Collections.sort(filmsName);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Platform.runLater(() -> {
                loading.setVisible(false);
                if (species != null) {
                    favouriteButton.setDisable(false);
                    nameLabel.setText("Name: "+species.get("name").toString().replace("\"", ""));
                    classificationLabel.setText("Classification: "+species.get("classification").toString().replace("\"", ""));
                    designationLabel.setText("Designation: "+species.get("designation").toString().replace("\"", ""));
                    averageHeightLabel.setText("Average height: "+species.get("average_height").toString().replace("\"", ""));
                    skinColorsLabel.setText("Skin colors: "+species.get("skin_colors").toString().replace("\"", ""));
                    hairColorsLabel.setText("Hair colors: "+species.get("hair_colors").toString().replace("\"", ""));
                    averageLifespanLabel.setText("Average lifespan: "+species.get("average_lifespan").toString().replace("\"", ""));
                    if (homeworld==null){
                        homeworldLabel.setText("Homeworld: n/a");
                    } else {
                        homeworldLabel.setText("Homeworld: "+homeworld.get("name").toString().replace("\"", ""));
                    }
                    languageLabel.setText("Language: "+species.get("language").toString().replace("\"", ""));
                    peopleList.getItems().addAll(peopleName);
                    filmsList.getItems().addAll(filmsName);
                } else {
                    NoResultsMessage.showPopupMessage("Nothing found for given species",(Stage)((Node)event.getSource()).getScene().getWindow());
                }
            });
        }).start();
    }

    @FXML
    protected void backButtonClick(ActionEvent event) throws IOException {
        Utils.backButtonClick(event);
    }

    public void onEnter(ActionEvent actionEvent) {
        showSpeciesButtonClick(actionEvent);
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
        classificationLabel.setText("Classification: "+result.getString("classification").toString().replace("\"", ""));
        designationLabel.setText("Designation: "+result.getString("designation").toString().replace("\"", ""));
        averageHeightLabel.setText("Average height: "+result.getString("average_height").toString().replace("\"", ""));
        skinColorsLabel.setText("Skin colors: "+result.getString("skin_colors").toString().replace("\"", ""));
        hairColorsLabel.setText("Hair colors: "+result.getString("hair_colors").toString().replace("\"", ""));
        averageLifespanLabel.setText("Average lifespan: "+result.getString("average_lifespan").toString().replace("\"", ""));
        homeworldLabel.setText("Homeworld: "+result.getString("homeworld").toString().replace("\"", ""));
        languageLabel.setText("Language: "+result.getString("language").toString().replace("\"", ""));
        peopleList.getItems().addAll((Arrays.asList(result.getString("people").split(", "))));
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
                case "peopleList":
                    chosenItem = peopleList.getSelectionModel().getSelectedItem();
                    Utils.passInfo(mouseEvent,chosenItem,"people-view.fxml");
                    break;


            }
        }
    }

    public void addToFavourites(ActionEvent actionEvent) throws SQLException {
//        Database db1=Database.getInstance();
//        Document testSpecies = new Document()
//                .append("_id", new ObjectId())
//                .append("name", species.get("name").toString().replace("\"", ""))
//                .append("classification",species.get("classification").toString().replace("\"", ""))
//                .append("designation",species.get("designation").toString().replace("\"", ""))
//                .append("average_height",species.get("average_height").toString().replace("\"", ""))
//                .append("skin_colors",species.get("skin_colors").toString().replace("\"", ""))
//                .append("hair_colors",species.get("hair_colors").toString().replace("\"", ""))
//                .append("average_lifespan",species.get("average_lifespan").toString().replace("\"", ""))
//                .append("language",species.get("language").toString().replace("\"", ""))
//                .append("films", filmsName)
//                .append("people",peopleName);
//        if (homeworld==null){
//            testSpecies.append("homeworld:", "n/a");
//        } else {
//            testSpecies.append("homeworld: ",homeworld.get("name").toString().replace("\"", ""));
//        }
//        db1.insertSpecies(testSpecies);

        String name = species.get("name").toString().replace("\"", "");
        String classification = species.get("classification").toString().replace("\"", "");
        String designation = species.get("designation").toString().replace("\"", "");
        String average_height = species.get("average_height").toString().replace("\"", "");
        String skin_colors = species.get("skin_colors").toString().replace("\"", "");
        String hair_colors = species.get("hair_colors").toString().replace("\"", "");
        String average_lifespan = species.get("average_lifespan").toString().replace("\"", "");
        String home=null;
        if (homeworld==null){
            home="n/a";
        } else {
            home=homeworld.get("name").toString().replace("\"", "");
        }
        String language = species.get("language").toString().replace("\"", "");
        String dbPeople = peopleName.toString().replace("[", "").replace("]", "");
        String dbFilms = filmsName.toString().replace("[", "").replace("]", "");

        Database db = Database.getInstance();
        db.insertIntoSpecies(name,classification,designation,average_height,skin_colors,hair_colors,average_lifespan,home,language,dbFilms,dbPeople);
    }

//    @Override
//    public void setFromFavourites(Document document) {
//        nameLabel.setText("Name: "+document.get("name").toString().replace("\"", ""));
//        classificationLabel.setText("Classification: "+document.get("classification").toString().replace("\"", ""));
//        designationLabel.setText("Designation: "+document.get("designation").toString().replace("\"", ""));
//        averageHeightLabel.setText("Average height: "+document.get("average_height").toString().replace("\"", ""));
//        skinColorsLabel.setText("Skin colors: "+document.get("skin_colors").toString().replace("\"", ""));
//        hairColorsLabel.setText("Hair colors: "+document.get("hair_colors").toString().replace("\"", ""));
//        averageLifespanLabel.setText("Average lifespan: "+document.get("average_lifespan").toString().replace("\"", ""));
//        homeworldLabel.setText("Homeworld: "+document.get("name").toString().replace("\"", ""));
//        languageLabel.setText("Language: "+document.get("language").toString().replace("\"", ""));
//        peopleList.getItems().addAll((ArrayList<String>) document.get("people"));
//        filmsList.getItems().addAll((ArrayList<String>) document.get("films"));
//    }
}
