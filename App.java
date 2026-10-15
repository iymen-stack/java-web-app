import com.sun.net.httpserver.HttpServer;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class App {

    public static void main(String[] args) throws Exception {

        HttpServer server = HttpServer.create(
            new InetSocketAddress("0.0.0.0", 8082), 0);

        // Health check endpoint for GitHub Actions
        server.createContext("/health", exchange -> {
            byte[] response = "UP".getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=UTF-8");

            exchange.sendResponseHeaders(200, response.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        // Main website
        server.createContext("/", exchange -> {

            String path = exchange.getRequestURI().getPath();

            if (!path.equals("/")) {
                byte[] response = "Not Found".getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(404, response.length);

                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response);
                }
                return;
            }

            String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <title>Java Docker Website</title>
                </head>
                <body>
                    <h1>Hello from Java and Docker!</h1>

                    <p>
                        A simple HTML page served by a Java
                        HTTP server running inside a Docker container.
                    </p>

                    <button onclick="showMessage()">
                        Click Me
                    </button>

                    <p id="message"></p>

                    <script>
                        function showMessage() {
                            document.getElementById("message").innerHTML =
                                "Hello from JavaScript!";
                        }
                    </script>

                </body>
                </html>
                """;

            byte[] response = html.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set(
                "Content-Type", "text/html; charset=UTF-8");

            exchange.sendResponseHeaders(200, response.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        });

        server.start();
        System.out.println("Website running on port 8082");
    }
}