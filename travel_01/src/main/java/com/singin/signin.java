package com.singin;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class signin extends HttpServlet {

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

        try {
            requestParams = new JSONObject(jsonInput);

            if (requestParams.has("username") && requestParams.has("password")) {
                String username = requestParams.getString("username");
                String password = requestParams.getString("password");

                try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                    String insertQuery = "INSERT INTO login (username, password) VALUES (?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setString(1, username);
                        preparedStatement.setString(2, password);

                        preparedStatement.executeUpdate();

                        result.put("status", "success");
                        result.put("message", "User added successfully!");
                        response.setStatus(HttpServletResponse.SC_CREATED);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    result.put("status", "error");
                    result.put("message", "Error adding user");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                result.put("status", "error");
                result.put("message", "Missing 'username' or 'password' in the request");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "Invalid JSON format in the request");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        response.getWriter().println(result.toString());
    } catch (Exception e) {
        e.printStackTrace();
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error adding user");
    }
}

}