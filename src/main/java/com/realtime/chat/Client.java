package com.realtime.chat;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        String host = "localhost";
        int port = 5000;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(host, port)) {
            System.out.println("Connected to chat server " + host + ":" + port);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            Scanner scanner = new Scanner(System.in);

            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            });
            reader.setDaemon(true);
            reader.start();

            while (true) {
                String input = scanner.nextLine();
                out.println(input);
                if ("/quit".equalsIgnoreCase(input.trim())) break;
            }
        }
    }
}
