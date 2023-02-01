package ru.netology;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {

        final int PORT = 9999;
        final int NUMBER_OF_THREADS = 64;

        Server server = new Server(PORT, NUMBER_OF_THREADS);

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
                System.out.println("Hello GET");
            }
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                // TODO: handlers code
                System.out.println("Hello POST");
            }
        });

        server.addHandler("GET", "/favicon.ico", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
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


            }
        });

        server.start();
    }
}


