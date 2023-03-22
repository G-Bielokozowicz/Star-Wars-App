module com.example.projekt {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpclient;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires java.logging;
    requires java.sql;
    requires mysql.connector.java;

    opens com.projekt to javafx.fxml;
    exports com.projekt;
    exports com.projekt.controllers;
    opens com.projekt.controllers to javafx.fxml;
}