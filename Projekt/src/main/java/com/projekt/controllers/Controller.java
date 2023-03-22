package com.projekt.controllers;


import java.sql.ResultSet;
import java.sql.SQLException;

public interface Controller {
    void setInput(String input);
    void setFromFavourites(ResultSet result, int row) throws SQLException;
}
