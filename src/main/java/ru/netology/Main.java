package ru.netology;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        final int PORT = 9998;
        final int NUMBER_OF_THREADS = 64;

        Server server = new Server(PORT, NUMBER_OF_THREADS);

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages", (request, responseStream) -> {
            // TODO: handlers code
            System.out.println("Hello GET");
            System.out.println(request.getQueryParams());

            System.out.println("Значение \"value\" = "
                    + request.getQueryParam("value"));

        });
        server.addHandler("POST", "/messages", (request, responseStream) -> {
            // TODO: handlers code
            System.out.println("Hello POST");
        });

        server.addHandler("GET", "/favicon.ico", (request, responseStream) -> {
            try {
                final var path = "/favicon.ico";
                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);
                final var length = Files.size(filePath);
                responseStream.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, responseStream);
                System.out.println("favicon.ico sent");
                responseStream.flush();

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        });

        server.start();
    }
}


