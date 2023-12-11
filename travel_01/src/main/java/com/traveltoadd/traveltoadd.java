package com.traveltoadd;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class traveltoadd extends HttpServlet {

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

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

		try {
			JSONObject result = new JSONObject();

			String temp, jsonInput = "";
			JSONObject requestParams;
			BufferedReader reader = request.getReader();
			while ((temp = reader.readLine()) != null) {
				jsonInput += temp;
			}
			requestParams = new JSONObject(jsonInput);

			String destinationid = requestParams.getString("destinationid");
			String destination = requestParams.getString("destination");
			String date = requestParams.getString("date");
			String eat = requestParams.getString("Eat");
			String leisure = requestParams.getString("Leisure");
			String transportation = requestParams.getString("Transportation");

			try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
				String insertQuery = "INSERT INTO itinerary (destinationid, destination, date, Eat, Leisure, Transportation) VALUES (?, ?, ?, ?, ?, ?)";
				try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
					preparedStatement.setString(1, destinationid);
					preparedStatement.setString(2, destination);
					preparedStatement.setString(3, date);
					preparedStatement.setString(4, eat);
					preparedStatement.setString(5, leisure);
					preparedStatement.setString(6, transportation);

					preparedStatement.executeUpdate();

					result.put("status", "success");
					result.put("message", "Destination added successfully!");
					response.setStatus(HttpServletResponse.SC_CREATED);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				result.put("status", "error");
				result.put("message", "Error adding destination");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}

			response.getWriter().println(result.toString());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding destination");
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

        // Initialize the result object
        JSONObject result = new JSONObject();

        try {
            String destinationID = null;
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                destinationID = pathInfo.substring(1);
            }

            JSONObject result1 = new JSONObject();
            String temp, jsonInput = "";
            JSONObject requestParams1;
            BufferedReader reader = request.getReader();
            while ((temp = reader.readLine()) != null) {
                jsonInput += temp;
            }
            requestParams1 = new JSONObject(jsonInput);

            String destination = requestParams1.getString("destination");
            String date = requestParams1.getString("date");
            String eat = requestParams1.getString("Eat");
            String leisure = requestParams1.getString("Leisure");
            String transportation = requestParams1.getString("Transportation");

            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                String updateQuery = "UPDATE itinerary SET destination = ?, date = ?, eat = ?, leisure = ?, transportation = ? WHERE destinationID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    preparedStatement.setString(1, destination);
                    preparedStatement.setString(2, date);
                    preparedStatement.setString(3, eat);
                    preparedStatement.setString(4, leisure);
                    preparedStatement.setString(5, transportation);

                    if (destinationID != null) {
                        preparedStatement.setString(6, destinationID);

                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            result1.put("status", "success");
                            result1.put("message", "Itinerary details updated successfully.");
                            response.setStatus(HttpServletResponse.SC_OK);
                        } else {
                            result1.put("status", "error");
                            result1.put("message", "Destination ID not found.");
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        }
                    } else {
                        result1.put("status", "error");
                        result1.put("message", "Destination ID is missing in the request path.");
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "An unexpected error occurred.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        response.getWriter().println(result.toString());
    }
}