package com.realtime.chat;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private final int port;
    private final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public Server(int port) { this.port = port; }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        }
    }

    private void broadcast(String message, ClientHandler exclude) {
        for (ClientHandler c : clients) {
            if (c != exclude) c.send(message);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String name = "Anonymous";

        ClientHandler(Socket socket) { this.socket = socket; }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                out.println("Enter your name:");
                name = Optional.ofNullable(in.readLine()).orElse("Anonymous");
                out.println("Welcome, " + name + "! Type /quit to exit.");
                broadcast("[join] " + name + " joined the chat", this);
                String line;
                while ((line = in.readLine()) != null) {
                    if ("/quit".equalsIgnoreCase(line.trim())) break;
                    String msg = String.format("[%tT] %s: %s", new Date(), name, line);
                    System.out.println(msg);
                    broadcast(msg, this);
                }
            } catch (IOException e) {
                System.err.println("Client error: " + e.getMessage());
            } finally {
                close();
            }
        }

        void send(String msg) { if (out != null) out.println(msg); }

        void close() {
            try { if (in != null) in.close(); } catch (IOException ignored) {}
            if (out != null) out.close();
            try { socket.close(); } catch (IOException ignored) {}
            clients.remove(this);
            broadcast("[left] " + name + " left the chat", this);
        }
    }

    public static void main(String[] args) throws IOException {
        int port = 5000;
        if (args.length > 0) port = Integer.parseInt(args[0]);
        new Server(port).start();
    }
}
