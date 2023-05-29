import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
//import sun.misc.IOUtils;
//import sun.nio.ch.IOUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class httpServer {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        int port = 8015;

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
            String jsonResponse = retrieveData();

            sendResponse(exchange, 200, jsonResponse);
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {

            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            //String requestBody = new String(IOUtils.toByteArray(exchange.getRequestBody()), StandardCharsets.UTF_8);

            try {
                EcommerceData dataObject = objectMapper.readValue(requestBody, EcommerceData.class);

                saveData(dataObject);

                sendResponse(exchange, 200, "Data saved successfully");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "Invalid JSON payload");
            }
        }

        private void handlePutRequest(HttpExchange exchange) throws IOException {

            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                EcommerceData dataObject = objectMapper.readValue(requestBody, EcommerceData.class);

                updateData(dataObject);

                sendResponse(exchange, 200, "Data updated successfully");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "Invalid JSON payload");
            }
        }

        private void handleDeleteRequest(HttpExchange exchange) throws IOException {
            String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            try {
                EcommerceData dataObject = objectMapper.readValue(requestBody, EcommerceData.class);

                deleteData(dataObject);

                sendResponse(exchange, 200, "Data deleted successfully");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                sendResponse(exchange, 400, "Invalid JSON payload");
            }
        }

        private String retrieveData() {
            String jdbcUrl = "jdbc:sqlite:/path/to/ecommerce-pbo.db";
            try (Connection connection = DriverManager.getConnection(jdbcUrl)) {
                String queryUsers = "SELECT * FROM users";
                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(queryUsers)) {

                    List<User> userList = new ArrayList<>();

                    while (resultSet.next()) {
                        User user = new User();
                        user.setUserId(resultSet.getString("user_id"));
                        user.setName(resultSet.getString("name"));
                        user.setAddress(resultSet.getString("address"));
                        user.setEmail(resultSet.getString("email"));


                        userList.add(user);
                    }

                    EcommerceData ecommerceData = new EcommerceData();
                    ecommerceData.setUsers(userList);

                    return objectMapper.writeValueAsString(ecommerceData);
                }
            } catch (SQLException | JsonProcessingException e) {
                e.printStackTrace();
                return "Error retrieving data from the database";
            }
        }

        private void saveData(EcommerceData dataObject) {

        }

        private void updateData(EcommerceData dataObject) {

        }

        private void deleteData(EcommerceData dataObject) {

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

    static class EcommerceData {
        private List<User> users;
        private List<Order> orders;
        private List<Product> products;

        // Getters and Setters
        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public List<Order> getOrders() {
            return orders;
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        // Constructors
        public EcommerceData() {
            this.users = new ArrayList<>();
            this.orders = new ArrayList<>();
            this.products = new ArrayList<>();
        }
    }

    static class User {
        private String userId;
        private String name;
        private String address;
        private String email;
        private List<Product> products;
        private List<Order> orders;
        private List<Review> reviews;

        // Getters and Setters
        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public List<Order> getOrders() {
            return orders;
        }

        public void setOrders(List<Order> orders) {
            this.orders = orders;
        }

        public List<Review> getReviews() {
            return reviews;
        }

        public void setReviews(List<Review> reviews) {
            this.reviews = reviews;
        }

        // Constructors
        public User() {
            this.products = new ArrayList<>();
            this.orders = new ArrayList<>();
            this.reviews = new ArrayList<>();
        }

    }

    static class Product {
        private String productId;
        private String namaProduk;
        private int harga;

        // Getters and Setters
        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getNamaProduk() {
            return namaProduk;
        }

        public void setNamaProduk(String namaProduk) {
            this.namaProduk = namaProduk;
        }

        public int getHarga() {
            return harga;
        }

        public void setHarga(int harga) {
            this.harga = harga;
        }

        // Constructors
        public Product(String productId, String namaProduk, int harga) {
            this.productId = productId;
            this.namaProduk = namaProduk;
            this.harga = harga;
        }
    }

    static class Order {
        private int orderId;
        private String productId;
        private String namaProduk;
        private int harga;
        private String orderStatus;

        // Getters and Setters
        public int getOrderId() {
            return orderId;
        }

        public void setOrderId(int orderId) {
            this.orderId = orderId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getNamaProduk() {
            return namaProduk;
        }

        public void setNamaProduk(String namaProduk) {
            this.namaProduk = namaProduk;
        }

        public int getHarga() {
            return harga;
        }

        public void setHarga(int harga) {
            this.harga = harga;
        }

        public String getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }

        // Constructors
        public Order(int orderId, String productId, String namaProduk, int harga, String orderStatus) {
            this.orderId = orderId;
            this.productId = productId;
            this.namaProduk = namaProduk;
            this.harga = harga;
            this.orderStatus = orderStatus;
        }
    }

    static class Review {
        private int reviewId;
        private String productId;
        private String namaProduk;
        private String reviewContent;

        // Getters and Setters
        public int getReviewId() {
            return reviewId;
        }

        public void setReviewId(int reviewId) {
            this.reviewId = reviewId;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getNamaProduk() {
            return namaProduk;
        }

        public void setNamaProduk(String namaProduk) {
            this.namaProduk = namaProduk;
        }

        public String getReviewContent() {
            return reviewContent;
        }

        public void setReviewContent(String reviewContent) {
            this.reviewContent = reviewContent;
        }

        // Constructors
        public Review(int reviewId, String productId, String namaProduk, String reviewContent) {
            this.reviewId = reviewId;
            this.productId = productId;
            this.namaProduk = namaProduk;
            this.reviewContent = reviewContent;
        }
    }
}
