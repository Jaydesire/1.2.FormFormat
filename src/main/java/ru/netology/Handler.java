package ru.netology;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;

public interface Handler {
    public void handle(Request request, BufferedOutputStream responseStream) throws FileNotFoundException;
}
