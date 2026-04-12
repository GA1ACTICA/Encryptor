package Networking.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    // Initialize socket and input/output streams
    private Socket socket = null;

    private BufferedReader reader = null;
    private PrintWriter writer = null;
    private BufferedReader console = null;

    public Client(String addr, int port) {
        // Establish a connection
        try {
            socket = new Socket(addr, port);
            System.out.println("Connected");

            console = new BufferedReader(new InputStreamReader(System.in));

            // Setup for reader
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Setup for writer
            writer = new PrintWriter(socket.getOutputStream(), true);

        } catch (UnknownHostException u) {
            System.out.println(u);
            return;
        } catch (IOException i) {
            System.out.println(i);
            return;
        }

        // Reader
        new Thread(() -> {
            try {
                String msg;
                while ((msg = reader.readLine()) != null) {
                    System.out.println("Server: " + msg);
                }
            } catch (Exception e) {
                System.out.println("Disconnected");
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

    }

    public static void main(String[] args) {
        new Client("127.0.0.1", 5000);
    }
}
