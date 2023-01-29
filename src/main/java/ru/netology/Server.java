package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server {

    private List<String> validPaths;
    private final int PORT;
    private final int NUMBER_OF_THREADS;


    public Server(List<String> validPaths, int PORT, int NUMBER_OF_THREADS) {
        this.validPaths = validPaths;
        this.PORT = PORT;
        this.NUMBER_OF_THREADS = NUMBER_OF_THREADS;
    }

    public void start() {
        try (final var serverSocket = new ServerSocket(PORT)) {
            System.out.println("---[ Server started ]----------");

            ExecutorService service = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

            while (true) {
                var socket = serverSocket.accept();
                service.execute(() -> handle(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handle (Socket socket){

        try (
                final var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final var out = new BufferedOutputStream(socket.getOutputStream())
        ) {
            // read only request line for simplicity
            // must be in form GET /path HTTP/1.1
            final var requestLine = in.readLine();

            Request request = new Request(requestLine);
            request.getQueryParams()
                    .stream()
                    .forEach(System.out::println);

            System.out.println("Значение \"value\" = "
                    + request.getQueryParam("value"));




            //Вывод requestLine, проверка на наличие информации в requestLine
            System.out.println(Thread.currentThread().getName() + " received a request: " + requestLine);
            if (requestLine == null) {
                System.out.println("Bad request!!!");
                // just close socket
                return;
            }

            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                // just close socket
                return;
            }
            var path = parts[1];
//            List<NameValuePair> params;
//            String param = "v";
//            params = URLEncodedUtils.parse(URI.create(path), "UTF-8");
//            //params.stream().forEach(System.out::println);
//
//            params.stream()
//                    .filter(p -> p.getName().equals(param))
//                    .forEach(System.out::println);
//
//            var dv = params.stream()
//                    .filter(p -> p.getName().equals(param)).toString();



            if (!validPaths.contains(path)) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return;
            }

            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            // special case for classic
            if (path.equals("/classic.html")) {
                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
                return;
            }

            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}