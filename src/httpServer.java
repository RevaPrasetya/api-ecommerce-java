import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class httpServer {
    public static void main(String[] args) throws IOException {
        int port = 1015; // Port number 015 is equivalent to port 15

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // Use the default executor

        server.start();

        System.out.println("Server started on port " + port);
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Hello, World!"; // Modify the response as needed
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
