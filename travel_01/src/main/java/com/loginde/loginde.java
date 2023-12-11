package com.loginde;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class loginde extends HttpServlet {
    private static final long serialVersionUID = 1L;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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

     
        JSONObject result = new JSONObject();
        String temp, jsonInput = "";
        JSONObject requestParams1 = null; // Initialize requestParams1

        BufferedReader reader = request.getReader();
        while ((temp = reader.readLine()) != null) {
            jsonInput += temp;
        }

        // Parse JSON input
        try {
            requestParams1 = new JSONObject(jsonInput);

            String username = requestParams1.getString("username");
            String password = requestParams1.getString("password");

            final String jdbcUrl = "jdbc:mysql://localhost:3306/travel";
            final String jdbcUser = "root";
            final String jdbcPassword = "7966";

            // Initialize result object
            result = new JSONObject();

            try (Connection connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
                 PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM login WHERE username = ? AND password = ?")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        result.put("status", "success");
                        result.put("message", "Login successful.");
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        result.put("status", "error");
                        result.put("message", "Invalid username or password.");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("status", "error");
            result.put("message", "Error connecting to the database.");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        try (PrintWriter out = response.getWriter()) {
            out.println(result.toString());
        }
    }
}
