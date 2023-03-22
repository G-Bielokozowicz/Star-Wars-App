package com.projekt.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Objects;
import java.util.Properties;


public class Database {

    private String url;
    private String dbName;
    private String uname;
    private String password;

    private Connection con=null;
    private static Database instance;


    public void connect(){
        try {

            con = DriverManager.getConnection(url+dbName, uname, password);
            System.out.println("connection succeseful");
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    private Database() {
        try {
            Properties prop = new Properties();
            String path = "src/main/resources/com/projekt/config.properties";
            InputStream input = new FileInputStream(path);
            prop.load(input);
            this.url=prop.getProperty("dbUrl");
            this.dbName=prop.getProperty("dbName");
            this.uname=prop.getProperty("dbUName");
            this.password=prop.getProperty("dbPassword");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;

    }

    public void insertIntoVehicles(String name, String model, String manufacturer, String cost, String length, String max_atmosphering_speed,
                                    String crew, String passengers, String cargo_capacity, String vehicle_class, String films, String pilots) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from vehicles where name like '"+name+"'");
        if (!resultSet.next()){
            String query = "INSERT INTO Vehicles(name,model,manufacturer,cost,length,max_atmosphering_speed,crew,passengers,cargo_capacity,vehicle_class,films, pilots)"+
                    "VALUES ('"+name+"'," +
                    "'"+model+"'," +
                    "'"+manufacturer+"'," +
                    "'"+cost+"'," +
                    "'"+length+"'," +
                    "'"+max_atmosphering_speed+"'," +
                    "'"+crew+"'," +
                    "'"+passengers+"'," +
                    "'"+cargo_capacity+"'," +
                    "'"+vehicle_class+"'," +
                    "'"+films+"'," +
                    "'"+pilots+"');";
            statement.executeUpdate(query);
        }
    }

    public void insertIntoStarships(String name, String model, String manufacturer, String cost, String length, String max_atmosphering_speed,
                                  String crew, String passengers, String cargo_capacity, String starship_class, String hyperdrive_rating, String films, String pilots) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from starships where name like '"+name+"'");
        if (!resultSet.next()){
            String query = "INSERT INTO starships(name,model,manufacturer,cost,length,max_atmosphering_speed,crew,passengers,cargo_capacity,starship_class,hyperdrive_rating,films, pilots)"+
                    "VALUES ('"+name+"'," +
                    "'"+model+"'," +
                    "'"+manufacturer+"'," +
                    "'"+cost+"'," +
                    "'"+length+"'," +
                    "'"+max_atmosphering_speed+"'," +
                    "'"+crew+"'," +
                    "'"+passengers+"'," +
                    "'"+cargo_capacity+"'," +
                    "'"+starship_class+"'," +
                    "'"+hyperdrive_rating+"'," +
                    "'"+films+"'," +
                    "'"+pilots+"');";
            statement.executeUpdate(query);
        }
    }

    public void insertIntoSpecies(String name, String classification, String designation, String average_height, String skin_colors, String hair_colors,
                                  String average_lifespan, String language, String homeworld, String films, String people) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from species where name like '"+name+"'");
        if (!resultSet.next()){
            String query = "INSERT INTO species(name,classification,designation,average_height,skin_colors,hair_colors,average_lifespan,language,homeworld,films,people)"+
                    "VALUES ('"+name+"'," +
                    "'"+classification+"'," +
                    "'"+designation+"'," +
                    "'"+average_height+"'," +
                    "'"+skin_colors+"'," +
                    "'"+hair_colors+"'," +
                    "'"+average_lifespan+"'," +
                    "'"+language+"'," +
                    "'"+homeworld+"'," +
                    "'"+films+"'," +
                    "'"+people+"');";
            statement.executeUpdate(query);
        }
    }

    public void insertIntoPlanets(String name, String rotation_period, String orbital_period, String diameter, String climate, String gravity,
                                  String terrain, String population, String films, String residents) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from planets where name like '"+name+"'");
        if (!resultSet.next()){
            String query = "INSERT INTO planets(name,rotation_period,orbital_period,diameter,climate,gravity,terrain,population,films,residents)"+
                    "VALUES ('"+name+"'," +
                    "'"+rotation_period+"'," +
                    "'"+orbital_period+"'," +
                    "'"+diameter+"'," +
                    "'"+climate+"'," +
                    "'"+gravity+"'," +
                    "'"+terrain+"'," +
                    "'"+population+"'," +
                    "'"+films+"'," +
                    "'"+residents+"');";
            statement.executeUpdate(query);
        }
    }


    public void insertIntoFilms(String name, String director, String producer, String release_date, String people, String starships, String planets) throws SQLException {
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from films where name like '"+name+"'");
        if (!resultSet.next()){
            String query = "INSERT INTO films(name,director,producer,release_date,people,starships,planets)"+
                    "VALUES ('"+name+"'," +
                    "'"+director+"'," +
                    "'"+producer+"'," +
                    "'"+release_date+"'," +
                    "'"+people+"'," +
                    "'"+starships+"'," +
                    "'"+planets+"');";
            statement.executeUpdate(query);
            }
    }


    public void insertIntoPeople(String name, String height, String hair_color, String eye_color, String birth_year, String gender, String homeworld,
                                 String films, String starships, String vehicles) throws SQLException {


        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery("select * from people where name like '"+name+"'");
        if (!resultSet.next()){
            // add only if not in database
            String query = "INSERT INTO people(name,height,hair_color,eye_color,birth_year,gender,homeworld,films,starships,vehicles)"+
                    "VALUES ('"+name+"'," +
                    "'"+height+"'," +
                    "'"+hair_color+"'," +
                    "'"+eye_color+"'," +
                    "'"+birth_year+"'," +
                    "'"+gender+"'," +
                    "'"+homeworld+"'," +
                    "'"+films+"'," +
                    "'"+starships+"'," +
                    "'"+vehicles+"');";
            statement.executeUpdate(query);

        }

    }

    public void delete(String table, String name) throws SQLException {

        Statement statement = con.createStatement();
        String query=("DELETE FROM "+ table +" where name like '"+name+"'");
        statement.executeUpdate(query);
    }

    public ResultSet getAll(String tableName) throws SQLException {
        String query = "select * from "+tableName;
        Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
        ResultSet resultSet = statement.executeQuery(query);
        return resultSet;
    }

    public void init() throws SQLException, IOException {
        try {
            boolean exists=false;
            Connection con = DriverManager.getConnection(url,uname,password);
            Statement statement = con.createStatement();

            ResultSet db = con.getMetaData().getCatalogs();
            while(db.next()){
                if(Objects.equals(db.getString(1), dbName)){
                    exists=true;
                }
            }
            if(!exists){
                System.out.println("Creating database");
                statement.executeUpdate("CREATE DATABASE "+dbName);
                con.close();
                con =DriverManager.getConnection(url+dbName,uname,password);
                statement = con.createStatement();
            } else {
                con.close();
                con =DriverManager.getConnection(url+dbName,uname,password);
                statement = con.createStatement();
            }
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(dbName,null,"people",new String[] {"TABLE"});
            if (!tables.next()){
                // no database
                System.out.println("Creating tables");
                String createFilms = "CREATE TABLE `films`("+
	                "`id` INT NOT NULL AUTO_INCREMENT,"+
                    "`name` VARCHAR(100) NOT NULL,"+
                    "`director` VARCHAR(100) NOT NULL,"+
                    "`producer` VARCHAR(100) NOT NULL,"+
                    "`release_date` VARCHAR(50) NOT NULL,"+
                    "`people` TEXT NOT NULL,"+
                    "`starships` TEXT NOT NULL,"+
                    "`planets` TEXT NOT NULL,"+
                        "PRIMARY KEY (`id`)"+
                ");";

                String createPeople = "CREATE TABLE `people`("+
	                "`id` INT NOT NULL AUTO_INCREMENT,"+
                    "`name` VARCHAR(100) NOT NULL,"+
                    "`height` VARCHAR(50) NOT NULL,"+
                    "`hair_color` VARCHAR(50) NOT NULL,"+
                    "`eye_color` VARCHAR(50) NOT NULL,"+
                    "`birth_year` VARCHAR(50) NOT NULL,"+
                    "`gender` VARCHAR(50) NOT NULL,"+
                    "`homeworld` VARCHAR(50) NOT NULL,"+
                    "`films` TEXT NOT NULL,"+
                    "`starships` TEXT NOT NULL,"+
                    "`vehicles` TEXT NOT NULL,"+
                        "PRIMARY KEY (`id`)"+
                    ");";

                String createPlanets = "CREATE TABLE `planets`("+
                        "`id` INT NOT NULL AUTO_INCREMENT,"+
                        "`name` VARCHAR(100) NOT NULL,"+
                        "`rotation_period` VARCHAR(100) NOT NULL,"+
                        "`orbital_period` VARCHAR(100) NOT NULL,"+
                        "`diameter` VARCHAR(100) NOT NULL,"+
                        "`climate` VARCHAR(100) NOT NULL,"+
                        "`gravity` VARCHAR(100) NOT NULL,"+
                        "`terrain` VARCHAR(100) NOT NULL,"+
                        "`population` VARCHAR(100) NOT NULL,"+
                        "`films` TEXT NOT NULL,"+
                        "`residents` TEXT NOT NULL,"+
                        "PRIMARY KEY (`id`)"+
                    ");";

                String createSpecies = "CREATE TABLE `species`("+
                        "`id` INT NOT NULL AUTO_INCREMENT,"+
                        "`name` VARCHAR(100) NOT NULL,"+
                        "`classification` VARCHAR(100) NOT NULL,"+
                        "`designation` VARCHAR(100) NOT NULL,"+
                        "`average_height` VARCHAR(100) NOT NULL,"+
                        "`skin_colors` VARCHAR(100) NOT NULL,"+
                        "`hair_colors` VARCHAR(100) NOT NULL,"+
                        "`average_lifespan` VARCHAR(100) NOT NULL,"+
                        "`language` VARCHAR(100) NOT NULL,"+
                        "`homeworld` VARCHAR(100) NOT NULL,"+
                        "`films` TEXT NOT NULL,"+
                        "`people` TEXT NOT NULL,"+
                        "PRIMARY KEY (`id`)"+
                    ");";

                String createStarships = "CREATE TABLE `starships`("+
                        "`id` INT NOT NULL AUTO_INCREMENT,"+
                        "`name` VARCHAR(100) NOT NULL,"+
                        "`model` VARCHAR(100) NOT NULL,"+
                        "`manufacturer` VARCHAR(200) NOT NULL,"+
                        "`cost` VARCHAR(100) NOT NULL,"+
                        "`length` VARCHAR(100) NOT NULL,"+
                        "`max_atmosphering_speed` VARCHAR(100) NOT NULL,"+
                        "`crew` VARCHAR(100) NOT NULL,"+
                        "`passengers` VARCHAR(100) NOT NULL,"+
                        "`cargo_capacity` VARCHAR(100) NOT NULL,"+
                        "`starship_class` VARCHAR(100) NOT NULL,"+
                        "`hyperdrive_rating` VARCHAR(100) NOT NULL,"+
                        "`films` TEXT NOT NULL,"+
                        "`pilots` TEXT NOT NULL,"+
                        "PRIMARY KEY (`id`)"+
                    ");";

                String createVehicles = "CREATE TABLE `vehicles`("+
                        "`id` INT NOT NULL AUTO_INCREMENT,"+
                        "`name` VARCHAR(100) NOT NULL,"+
                        "`model` VARCHAR(100) NOT NULL,"+
                        "`manufacturer` VARCHAR(200) NOT NULL,"+
                        "`cost` VARCHAR(100) NOT NULL,"+
                        "`length` VARCHAR(100) NOT NULL,"+
                        "`max_atmosphering_speed` VARCHAR(100) NOT NULL,"+
                        "`crew` VARCHAR(100) NOT NULL,"+
                        "`passengers` VARCHAR(100) NOT NULL,"+
                        "`cargo_capacity` VARCHAR(100) NOT NULL,"+
                        "`vehicle_class` VARCHAR(100) NOT NULL,"+
                        "`films` TEXT NOT NULL,"+
                        "`pilots` TEXT NOT NULL,"+
                        "PRIMARY KEY (`id`)"+
                    ");";
                statement.executeUpdate(createFilms);
                statement.executeUpdate(createPeople);
                statement.executeUpdate(createPlanets);
                statement.executeUpdate(createSpecies);
                statement.executeUpdate(createStarships);
                statement.executeUpdate(createVehicles);
                con.close();
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("Couldn't get connection with database! App closed!");
            System.exit(-1);
        }
    }

}

//public class Database {
//    String uri = "mongodb+srv://cinema:cinema@cinema.uau7r.mongodb.net/?retryWrites=true&w=majority";
//    private static Database instance;
//    private MongoClient mongoClient;
//    private MongoDatabase database;
//    private MongoCollection<Document> collection;
//    private Database() {
//        try  {
//            mongoClient = MongoClients.create(uri);
//            database = mongoClient.getDatabase("edp");
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
////    public Document test(){
////        collection=database.getCollection("films");
////        return collection.find().first();
////    }
//
//    public FindIterable<Document> findVehicles(){
//        collection=database.getCollection("vehicles");
//        FindIterable<Document> ret =null;
//        try {
//            ret = collection.find();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return ret;
//    }
//    public FindIterable<Document> findStarships(){
//        collection=database.getCollection("starships");
//        FindIterable<Document> ret =null;
//        try {
//            ret = collection.find();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return ret;
//    }
//
//    public FindIterable<Document> findSpecies(){
//        collection=database.getCollection("species");
//        FindIterable<Document> ret =null;
//        try {
//            ret = collection.find();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return ret;
//    }
//
//    public FindIterable<Document> findPlanets(){
//        collection=database.getCollection("planets");
//        FindIterable<Document> ret =null;
//        try {
//            ret = collection.find();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return ret;
//    }
//    public FindIterable<Document> findPeople(){
//        collection=database.getCollection("people");
//        FindIterable<Document> ret =null;
//        try {
//            ret = collection.find();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return ret;
//    }
//
//    public FindIterable<Document> findFilms(){
//        collection=database.getCollection("films");
//        FindIterable<Document> ret =null;
//        try {
//             ret = collection.find();
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        return ret;
//    }
//
//    public void insertFilm(Document film){
//        collection=database.getCollection("films");
//        Document check = null;
//        try {
//            check= collection.find(eq("name",film.get("name"))).first();
//            if (check==null){
//                collection.insertOne(film);
//            }
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void insertCharacter(Document character){
//        collection=database.getCollection("people");
//        Document check = null;
//        try {
//            check= collection.find(eq("name",character.get("name"))).first();
//            if (check==null){
//                collection.insertOne(character);
//            }
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void insertPlanet(Document planet){
//        collection=database.getCollection("planets");
//        Document check = null;
//        try {
//            check= collection.find(eq("name",planet.get("name"))).first();
//            if (check==null){
//                collection.insertOne(planet);
//            }
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void insertSpecies(Document species){
//        collection=database.getCollection("species");
//        Document check = null;
//        try {
//            check= collection.find(eq("name",species.get("name"))).first();
//            if (check==null){
//                collection.insertOne(species);
//            }
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void insertStarship(Document starship){
//        collection=database.getCollection("starships");
//        Document check = null;
//        try {
//            check= collection.find(eq("name",starship.get("name"))).first();
//            if (check==null){
//                collection.insertOne(starship);
//            }
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public void insertVehicle(Document vehicle){
//        collection=database.getCollection("vehicles");
//        Document check = null;
//        try {
//            check= collection.find(eq("name",vehicle.get("name"))).first();
//            if (check==null){
//                collection.insertOne(vehicle);
//            }
//
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    public Document find(String name, String coll){
//        collection= database.getCollection(coll);
//        return collection.find(eq("name",name)).first();
//    }
//
//    public static Database getInstance() {
//        if (instance == null) {
//            instance = new Database();
//        }
//        return instance;
//    }
//}

