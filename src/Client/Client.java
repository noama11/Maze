package Client;

import java.io.*;
import java.net.*;

public class Client {
    private InetAddress serverIP;
    private int serverPort;
    private IClientStrategy clientStrategy;

    public Client(InetAddress serverIP, int serverPort, IClientStrategy clientStrategy) {
        if (serverIP == null || clientStrategy == null || serverPort <= 0) {
            throw new IllegalArgumentException("Invalid client parameters");
        }
        this.serverIP = serverIP;
        this.serverPort = serverPort;
        this.clientStrategy = clientStrategy;
    }

    public void communicateWithServer() {
        try (
            // Create new socket connection to server
            Socket server = new Socket(serverIP, serverPort);

            // Get input and output streams for communication
            // These will also be closed automatically by try-with-resources
            InputStream inFromServer = server.getInputStream();
            OutputStream outToServer = server.getOutputStream()
        ) {
            // Log successful connection
            System.out.println("Connected to server: " + serverIP + ":" + serverPort);

            // Execute the client's strategy for communication
            clientStrategy.clientStrategy(inFromServer, outToServer);

        }
        catch (ConnectException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
        }
    }  // Socket and streams are automatically closed here by try-with-resources
}
