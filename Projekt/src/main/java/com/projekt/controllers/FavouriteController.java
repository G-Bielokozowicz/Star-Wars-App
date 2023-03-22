package com.projekt.controllers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.projekt.database.Database;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FavouriteController implements Controller, Initializable {
    @FXML
    private Button loadButton;
    @FXML
    private ListView<String> peopleList;
    @FXML
    private ListView<String> starshipsList;
    @FXML
    private ListView<String> vehiclesList;
    @FXML
    private ListView<String> planetsList;
    @FXML
    private ListView<String> speciesList;
    @FXML
    private ListView<String> filmsList;

    private ArrayList<String> filmsName = new ArrayList<String>();
    private ArrayList<String> peopleName = new ArrayList<String>();
    private ArrayList<String> planetsName = new ArrayList<String>();
    private ArrayList<String> speciesName = new ArrayList<String>();
    private ArrayList<String> starshipsName = new ArrayList<String>();
    private ArrayList<String> vehiclesName = new ArrayList<String>();

    //
//    private FindIterable<Document> films =null;
//    private FindIterable<Document> people =null;
//    private FindIterable<Document> planets =null;
//    private FindIterable<Document> species =null;
//    private FindIterable<Document> starships =null;
//    private FindIterable<Document> vehicles =null;
//    Database db1=Database.getInstance();

    ResultSet films = null;
    ResultSet people = null;
    ResultSet planets = null;
    ResultSet species = null;
    ResultSet starships = null;
    ResultSet vehicles = null;
    Database db=Database.getInstance();
    @Override
    public void setInput(String input) {

    }

    @Override
    public void setFromFavourites(ResultSet resultSet,int row) {

    }

    public void backButtonClick(ActionEvent actionEvent) throws IOException {
        Utils.backButtonClick(actionEvent);
    }



        private void getVehicles() throws SQLException {
        vehiclesName.clear();
        vehiclesList.getItems().clear();
        vehicles = db.getAll("vehicles");
        while (vehicles.next()){
            vehiclesName.add(vehicles.getString("name"));
        }
        vehiclesList.getItems().addAll(vehiclesName);
    }

    private void getStarships() throws SQLException {
        starshipsName.clear();
        starshipsList.getItems().clear();
        starships = db.getAll("starships");
        while (starships.next()){
            starshipsName.add(starships.getString("name"));
        }
        starshipsList.getItems().addAll(starshipsName);
    }

    private void getSpecies() throws SQLException {
        speciesName.clear();
        speciesList.getItems().clear();
        species = db.getAll("species");
        while (species.next()){
            speciesName.add(species.getString("name"));
        }
        speciesList.getItems().addAll(speciesName);
    }

    private void getPlanets() throws SQLException {
        planetsName.clear();
        planetsList.getItems().clear();
        planets = db.getAll("planets");
        while (planets.next()){
            planetsName.add(planets.getString("name"));
        }
        planetsList.getItems().addAll(planetsName);
    }


    private void getPeople() throws SQLException {
        peopleName.clear();
        peopleList.getItems().clear();
        people = db.getAll("people");
        while (people.next()){
            peopleName.add(people.getString("name"));
        }
        peopleList.getItems().addAll(peopleName);

    }
    private void getFilms() throws SQLException {
        filmsName.clear();
        filmsList.getItems().clear();
        films = db.getAll("films");
        while (films.next()){
            filmsName.add(films.getString("name"));
        }
        filmsList.getItems().addAll(filmsName);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(()->{
            try {
                getFilms();
                getPeople();
                getPlanets();
                getSpecies();
                getStarships();
                getVehicles();
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    public void filmsClicked(MouseEvent mouseEvent) throws SQLException, IOException {

        // go to page of selected item
        if (mouseEvent.getClickCount()==2 && mouseEvent.getButton()==MouseButton.PRIMARY){
            films.beforeFirst();
            String chosenItem;
            chosenItem = filmsList.getSelectionModel().getSelectedItem();
            while(films.next()){
                if (films.getString("name").toString().replace("\"","").equals(chosenItem)){
                    Utils.passFromFavourites(mouseEvent,chosenItem,"films-view.fxml",films,films.getRow());
                }
            }

        }
        // remove from favourites
        if (mouseEvent.getButton()==MouseButton.SECONDARY & mouseEvent.getClickCount()==2){
            String chosenItem;
            chosenItem = filmsList.getSelectionModel().getSelectedItem();
            filmsList.getItems().remove(chosenItem);
            db.delete("films",chosenItem);
        }
    }

    public void peopleClicked(MouseEvent mouseEvent) throws SQLException, IOException {
        // go to page of selected item
        if (mouseEvent.getClickCount()==2&& mouseEvent.getButton()==MouseButton.PRIMARY){
            people.beforeFirst();
            String chosenItem;
            chosenItem = peopleList.getSelectionModel().getSelectedItem();
            while(people.next()){
                if (people.getString("name").toString().replace("\"","").equals(chosenItem)){
                    Utils.passFromFavourites(mouseEvent,chosenItem,"people-view.fxml",people,people.getRow());
                }
            }

        }
        // remove from favourites
        if (mouseEvent.getButton()==MouseButton.SECONDARY & mouseEvent.getClickCount()==2){
            String chosenItem;
            chosenItem = peopleList.getSelectionModel().getSelectedItem();
            peopleList.getItems().remove(chosenItem);
            db.delete("people",chosenItem);
        }
    }

    public void planetsClicked(MouseEvent mouseEvent) throws SQLException, IOException {
        // go to page of selected item
        if (mouseEvent.getClickCount()==2&& mouseEvent.getButton()==MouseButton.PRIMARY){
            planets.beforeFirst();
            String chosenItem;
            chosenItem = planetsList.getSelectionModel().getSelectedItem();
            while(planets.next()){
                if (planets.getString("name").toString().replace("\"","").equals(chosenItem)){
                    Utils.passFromFavourites(mouseEvent,chosenItem,"planets-view.fxml",planets,planets.getRow());
                }
            }

        }
        // remove from favourites
        if (mouseEvent.getButton()==MouseButton.SECONDARY & mouseEvent.getClickCount()==2){
            String chosenItem;
            chosenItem = planetsList.getSelectionModel().getSelectedItem();
            planetsList.getItems().remove(chosenItem);
            db.delete("planets",chosenItem);
        }
    }

    public void speciesClicked(MouseEvent mouseEvent) throws SQLException, IOException {
        // go to page of selected item
        if (mouseEvent.getClickCount()==2&& mouseEvent.getButton()==MouseButton.PRIMARY){
            species.beforeFirst();
            String chosenItem;
            chosenItem = speciesList.getSelectionModel().getSelectedItem();
            while(species.next()){
                if (species.getString("name").toString().replace("\"","").equals(chosenItem)){
                    Utils.passFromFavourites(mouseEvent,chosenItem,"species-view.fxml",species,species.getRow());
                }
            }

        }
        // remove from favourites
        if (mouseEvent.getButton()==MouseButton.SECONDARY & mouseEvent.getClickCount()==2){
            String chosenItem;
            chosenItem = speciesList.getSelectionModel().getSelectedItem();
            speciesList.getItems().remove(chosenItem);
            db.delete("species",chosenItem);
        }
    }

    public void starshipsClicked(MouseEvent mouseEvent) throws SQLException, IOException {
        // go to page of selected item
        if (mouseEvent.getClickCount()==2&& mouseEvent.getButton()==MouseButton.PRIMARY){
            starships.beforeFirst();
            String chosenItem;
            chosenItem = starshipsList.getSelectionModel().getSelectedItem();
            while(starships.next()){
                if (starships.getString("name").toString().replace("\"","").equals(chosenItem)){
                    Utils.passFromFavourites(mouseEvent,chosenItem,"starships-view.fxml",starships,starships.getRow());
                }
            }

        }
        // remove from favourites
        if (mouseEvent.getButton()==MouseButton.SECONDARY & mouseEvent.getClickCount()==2){
            String chosenItem;
            chosenItem = starshipsList.getSelectionModel().getSelectedItem();
            starshipsList.getItems().remove(chosenItem);
            db.delete("starships",chosenItem);
        }
    }

    public void vehiclesClicked(MouseEvent mouseEvent) throws SQLException, IOException {
        // go to page of selected item
        if (mouseEvent.getClickCount()==2&& mouseEvent.getButton()==MouseButton.PRIMARY){
            vehicles.beforeFirst();
            String chosenItem;
            chosenItem = vehiclesList.getSelectionModel().getSelectedItem();
            while(vehicles.next()){
                if (vehicles.getString("name").toString().replace("\"","").equals(chosenItem)){
                    Utils.passFromFavourites(mouseEvent,chosenItem,"vehicles-view.fxml",vehicles,vehicles.getRow());
                }
            }

        }
        // remove from favourites
        if (mouseEvent.getButton()==MouseButton.SECONDARY & mouseEvent.getClickCount()==2){
            String chosenItem;
            chosenItem = vehiclesList.getSelectionModel().getSelectedItem();
            vehiclesList.getItems().remove(chosenItem);
            db.delete("vehicles",chosenItem);

        }
    }

//    public void filmsClicked(MouseEvent mouseEvent) throws IOException {
//        if (mouseEvent.getClickCount()==2){
//            String chosenItem;
//            chosenItem = filmsList.getSelectionModel().getSelectedItem();
//            for (Document d : films){
//                if (d.get("name").toString().replace("\"", "").equals(chosenItem)){
//                    Utils.passFromFavourites(mouseEvent,chosenItem,"films-view.fxml",d);
//                    break;
//                }
//            }
//
//        }
//    }
//
//
//    public void peopleClicked(MouseEvent mouseEvent) throws IOException {
//        if (mouseEvent.getClickCount()==2){
//            String chosenItem;
//            chosenItem = peopleList.getSelectionModel().getSelectedItem();
//            for (Document d : people){
//                if (d.get("name").toString().replace("\"", "").equals(chosenItem)){
//                    Utils.passFromFavourites(mouseEvent,chosenItem,"people-view.fxml",d);
//                    break;
//                }
//            }
//
//        }
//    }
//
//    public void planetsClicked(MouseEvent mouseEvent) throws IOException {
//        if (mouseEvent.getClickCount()==2){
//            String chosenItem;
//            chosenItem = planetsList.getSelectionModel().getSelectedItem();
//            for (Document d : planets){
//                if (d.get("name").toString().replace("\"", "").equals(chosenItem)){
//                    Utils.passFromFavourites(mouseEvent,chosenItem,"planets-view.fxml",d);
//                    break;
//                }
//            }
//
//        }
//    }
//
//    public void speciesClicked(MouseEvent mouseEvent) throws IOException {
//        if (mouseEvent.getClickCount()==2){
//            String chosenItem;
//            chosenItem = speciesList.getSelectionModel().getSelectedItem();
//            for (Document d : species){
//                if (d.get("name").toString().replace("\"", "").equals(chosenItem)){
//                    Utils.passFromFavourites(mouseEvent,chosenItem,"species-view.fxml",d);
//                    break;
//                }
//            }
//
//        }
//    }
//
//    public void starshipsClicked(MouseEvent mouseEvent) throws IOException {
//        if (mouseEvent.getClickCount()==2){
//            String chosenItem;
//            chosenItem = starshipsList.getSelectionModel().getSelectedItem();
//            for (Document d : starships){
//                if (d.get("name").toString().replace("\"", "").equals(chosenItem)){
//                    Utils.passFromFavourites(mouseEvent,chosenItem,"starships-view.fxml",d);
//                    break;
//                }
//            }
//
//        }
//    }
//
//    public void vehiclesClicked(MouseEvent mouseEvent) throws IOException {
//        if (mouseEvent.getClickCount()==2){
//            String chosenItem;
//            chosenItem = vehiclesList.getSelectionModel().getSelectedItem();
//            for (Document d : vehicles){
//                if (d.get("name").toString().replace("\"", "").equals(chosenItem)){
//                    Utils.passFromFavourites(mouseEvent,chosenItem,"vehicles-view.fxml",d);
//                    break;
//                }
//            }
//
//        }
//    }



    //
//    private void getVehicles(){
//        vehiclesName.clear();
//        vehiclesList.getItems().clear();
//        vehicles = db1.findVehicles();
//        for (Document d : vehicles){
//            vehiclesName.add(d.get("name").toString().toString().replace("\"", ""));
//        }
//        vehiclesList.getItems().addAll(vehiclesName);
//    }
//
//    private void getStarships(){
//        starshipsName.clear();
//        starshipsList.getItems().clear();
//        starships = db1.findStarships();
//        for (Document d : starships){
//            starshipsName.add(d.get("name").toString().toString().replace("\"", ""));
//        }
//        starshipsList.getItems().addAll(starshipsName);
//    }
//
//    private void getSpecies(){
//        speciesName.clear();
//        speciesList.getItems().clear();
//        species = db1.findSpecies();
//        for (Document d : species){
//            speciesName.add(d.get("name").toString().toString().replace("\"", ""));
//        }
//        speciesList.getItems().addAll(speciesName);
//    }
//
//    private void getPlanets(){
//        planetsName.clear();
//        planetsList.getItems().clear();
//        planets = db1.findPlanets();
//        for (Document d : planets){
//            planetsName.add(d.get("name").toString().toString().replace("\"", ""));
//        }
//        planetsList.getItems().addAll(planetsName);
//    }
//
//
//    private void getPeople(){
//        peopleName.clear();
//        peopleList.getItems().clear();
//        people = db1.findPeople();
//        for (Document d : people){
//            peopleName.add(d.get("name").toString().toString().replace("\"", ""));
//        }
//        peopleList.getItems().addAll(peopleName);
//
//    }
//    private void getFilms(){
//        filmsName.clear();
//        filmsList.getItems().clear();
//        films = db1.findFilms();
//        for (Document d : films){
//            filmsName.add(d.get("name").toString().toString().replace("\"", ""));
//        }
//        filmsList.getItems().addAll(filmsName);
//
//    }
}
