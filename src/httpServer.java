import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class httpServer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        int port = 1015;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // Use the default executor

        server.start();

        System.out.println("Server started on port " + port);
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();

            switch (requestMethod) {
                case "GET":
                    handleGetRequest(exchange);
                    break;
                case "POST":
                    handlePostRequest(exchange);
                    break;
                case "PUT":
                    handlePutRequest(exchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange);
                    break;
                default:
                    sendResponse(exchange, 405, "Method Not Allowed");
            }
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            String jsonResponse = retrieveDataFromDatabase();

            sendResponse(exchange, 200, jsonResponse);
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {

            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                ecommerceData dataObject = objectMapper.readValue(requestBody, ecommerceData.class);

                saveDataToDatabase(dataObject);

                sendResponse(exchange, 200, "Data saved successfully");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "Invalid JSON payload");
            }
        }

        private void handlePutRequest(HttpExchange exchange) throws IOException {

            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                ecommerceData dataObject = objectMapper.readValue(requestBody, ecommerceData.class);

                updateDataInDatabase(dataObject);

                sendResponse(exchange, 200, "Data updated successfully");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "Invalid JSON payload");
            }
        }

        private void handleDeleteRequest(HttpExchange exchange) throws IOException {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                ecommerceData dataObject = objectMapper.readValue(requestBody, ecommerceData.class);

                deleteDataFromDatabase(dataObject);

                sendResponse(exchange, 200, "Data deleted successfully");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "Invalid JSON payload");
            }
        }

        private String retrieveDataFromDatabase() {
            String jdbcUrl = "jdbc:sqlite:/path/to/database.db";
            try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
                String queryUsers = "SELECT * FROM users";
                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(queryUsers)) {

                    // JSON array of objects
                    object jsonDatalist = ;

                    return objectMapper.writeValueAsString(jsonDatalist);
                }
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
                return "Error retrieving data from the database";
            }
        }

        private void saveDataToDatabase(ecommerceData dataObject) {

        }

        private void updateDataInDatabase(ecommerceData dataObject) {

        }

        private void deleteDataFromDatabase(ecommerceData dataObject) {

        }

        private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(statusCode, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class ecommerceData {

    }
}
