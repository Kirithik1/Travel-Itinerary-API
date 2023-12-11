package com.traveldetails;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class traveldetails extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/travel";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "7966";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	

        // Response Header
        response.setContentType("application/json");

        // Allow requests from any origin
        response.setHeader("Access-Control-Allow-Origin", "*");

        // Allow specific HTTP methods
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");

        // Allow specific headers
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        // Allow credentials (if needed)
        response.setHeader("Access-Control-Allow-Credentials", "true");
    	
        // Fetch itinerary from the database based on the destination parameter
        String destinationParam = request.getParameter("destination");
        List<Destination> destinations;

        if (destinationParam != null && !destinationParam.isEmpty()) {
            destinations = getItineraryByDestinationFromDatabase(destinationParam);
        } else {
            destinations = getItineraryFromDatabase();
        }

        // Process the request
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Convert destinations to JSON and send the response
        String json = convertToJson(destinations);
        response.getWriter().write(json);
    }

    private List<Destination> getItineraryFromDatabase() {
        List<Destination> destinations = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT destinationid, destination, date, eat, leisure, transportation FROM itinerary";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String destinationId = resultSet.getString("destinationid");
                    String destination = resultSet.getString("destination");
                    String date = resultSet.getString("date");
                    String eat = resultSet.getString("eat");
                    String leisure = resultSet.getString("leisure");
                    String transportation = resultSet.getString("transportation");

                    destinations.add(new Destination(destinationId, destination, date, eat, leisure, transportation));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return destinations;
    }

    private List<Destination> getItineraryByDestinationFromDatabase(String destination) {
        List<Destination> destinations = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            String query = "SELECT destinationid, destination, date, eat, leisure, transportation FROM itinerary WHERE destination = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, destination);
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    String destinationId = resultSet.getString("destinationid");
                    String date = resultSet.getString("date");
                    String eat = resultSet.getString("eat");
                    String leisure = resultSet.getString("leisure");
                    String transportation = resultSet.getString("transportation");

                    destinations.add(new Destination(destinationId, destination, date, eat, leisure, transportation));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return destinations;
    }

    private String convertToJson(List<Destination> destinations) {
        JSONObject jsonResponse = new JSONObject();
        JSONArray destinationsArray = new JSONArray();

        for (Destination destination : destinations) {
            JSONObject destinationJson = new JSONObject();
            destinationJson.put("destinationId", destination.getDestinationId());
            destinationJson.put("destination", destination.getDestination());
            destinationJson.put("date", destination.getDate());
            destinationJson.put("eat", destination.getEat());
            destinationJson.put("leisure", destination.getLeisure());
            destinationJson.put("transportation", destination.getTransportation());
            destinationsArray.put(destinationJson);
        }

        jsonResponse.put("destinations", destinationsArray);

        return jsonResponse.toString();
    }

    private static class Destination {
        private String destinationId;
        private String destination;
        private String date;
        private String eat;
        private String leisure;
        private String transportation;

        public Destination(String destinationId, String destination, String date, String eat, String leisure, String transportation) {
            this.destinationId = destinationId;
            this.destination = destination;
            this.date = date;
            this.eat = eat;
            this.leisure = leisure;
            this.transportation = transportation;
        }

        public String getDestinationId() {
            return destinationId;
        }

        public String getDestination() {
            return destination;
        }

        public String getDate() {
            return date;
        }

        public String getEat() {
            return eat;
        }

        public String getLeisure() {
            return leisure;
        }

        public String getTransportation() {
            return transportation;
        }
    }
}
