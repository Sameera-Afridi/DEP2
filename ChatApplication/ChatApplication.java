import java.io.*; 
import java.net.*;
import java.util.*;

// Main class to run the chat application
public class ChatApplication {

    // Inner class for the Chat Server
    static class ChatServer {
        private static Set<ClientHandler> clientHandlers = new HashSet<>();

        public static void main(String[] args) throws IOException {
            ServerSocket serverSocket = new ServerSocket(1234); // Server listening on port 1234
            System.out.println("Chat Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start(); // Start new thread for each client
            }
        }

        public static void broadcastMessage(String message, ClientHandler excludeUser) {
            for (ClientHandler client : clientHandlers) {
                if (client != excludeUser) {
                    client.sendMessage(message);
                }
            }
        }

        public static void removeClient(ClientHandler clientHandler) {
            clientHandlers.remove(clientHandler);
        }
    }

    // Inner class for handling each client
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                String message;

                // Read messages from client and broadcast to all other clients
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);
                    ChatServer.broadcastMessage(message, this);
                }
            } catch (IOException e) {
                System.err.println("Connection error: " + e.getMessage());
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                    ChatServer.removeClient(this);
                } catch (IOException e) {
                    System.err.println("Error closing resources: " + e.getMessage());
                }
            }
        }
    }

    // Inner class for the Chat Client
    static class ChatClient {
        public static void main(String[] args) throws IOException {
            Socket socket = new Socket("localhost", 1234); // Connect to server
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Thread for reading server messages
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String serverMessage;
                    try {
                        while ((serverMessage = in.readLine()) != null) {
                            System.out.println(serverMessage);
                        }
                    } catch (IOException e) {
                        System.err.println("Connection closed.");
                    }
                }
            }).start();

            // Thread for sending messages to server
            String userMessage;
            while ((userMessage = userInput.readLine()) != null) {
                out.println(userMessage);
            }

            socket.close();
        }
    }

    // Entry point to start either the server or client
    public static void main(String[] args) throws IOException {
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            ChatServer.main(args); // Start the server
        } else {
            ChatClient.main(args); // Start the client
        }
    }
}
