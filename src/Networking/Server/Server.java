package Networking.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    // Initialize socket and input stream
    private Socket socket = null;
    private ServerSocket serverSocket = null;

    // Constructor with port
    public Server(int port) {

        // Starts server and waits for a connection
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = serverSocket.accept();
            System.out.println("Client accepted");
            System.out.println("Client IP: " + socket.getInetAddress().getHostAddress());
            System.out.println("Client Host Name: " + socket.getInetAddress().getCanonicalHostName());

            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            // Setup for reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Setup for writer
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            // Reader
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = reader.readLine()) != null) {
                        System.out.println("Client: " + msg);
                    }
                } catch (Exception e) {
                    System.out.println("Client disconnected");
                }
            }).start();

            // Writer
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = console.readLine()) != null) {
                        writer.println(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new Server(5000);
    }

}
