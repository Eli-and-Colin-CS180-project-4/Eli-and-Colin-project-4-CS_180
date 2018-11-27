import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

final class ChatClient {
    private ObjectInputStream sInput;
    private ObjectOutputStream sOutput;
    private Socket socket;

    private final String server;
    private final String username;
    private final int port;

    private ChatClient(String username, int port, String server) {
        this.username = username;
        this.port = port;
        this.server = server;
    }

    private ChatClient(String username, int port) {
        this(username, port, "localhost");
    }

    private ChatClient(String username) {
        this(username, 1503, "localhost");
    }

    private ChatClient() {
        this("Anonymous", 1503, "localhost");
    }

    /*
     * This starts the Chat Client
     */
    private boolean start() {
        // Create a socket
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            System.out.println("The server is currently not online.");
            System.exit(0);
        }

        // Create your input and output streams
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // This thread will listen from the server for incoming messages
        Runnable r = new ListenFromServer();
        Thread t = new Thread(r);
        t.start();

        // After starting, send the clients username to the server.
        try {
            sOutput.writeObject(username);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }


    /*
     * This method is used to send a ChatMessage Objects to the server
     */
    private void sendMessage(ChatMessage msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
     * To start the Client use one of the following command
     * > java ChatClient
     * > java ChatClient username
     * > java ChatClient username portNumber
     * > java ChatClient username portNumber serverAddress
     *
     * If the portNumber is not specified 1500 should be used
     * If the serverAddress is not specified "localHost" should be used
     * If the username is not specified "Anonymous" should be used
     */
    public static void main(String[] args) {
        Scanner scn = new Scanner(System.in);

        // Get proper arguments and override defaults
        ChatClient client;
        if (args.length == 1) {
            client = new ChatClient(args[0]);
        } else if (args.length == 2) {
            client = new ChatClient(args[0], Integer.parseInt(args[1]));
        } else if (args.length == 3) {
            client = new ChatClient(args[0], Integer.parseInt(args[1]), args[2]);
        } else {
            client = new ChatClient();
        }
        // Create your client and start it
        client.start();
        while (true) {
            String msg = scn.nextLine();
            if (msg.equals("/logout")) {
                client.sendMessage(new ChatMessage(1, client.username + " disconnected with a LOGOUT message."));
                System.out.println("Server has closed the connection.");
                break;
            } else if (msg.contains("/msg")) {
                client.sendMessage(new ChatMessage(0, msg, client.username,
                        msg.substring(5, msg.indexOf(" ", 5))));
                //Send an empty message to the server
            } else {
                client.sendMessage(new ChatMessage(0, msg));
            }
        }
        System.exit(0);
    }


    /*
     * This is a private class inside of the ChatClient
     * It will be responsible for listening for messages from the ChatServer.
     * ie: When other clients send messages, the server will relay it to the client.
     */
    private final class ListenFromServer implements Runnable {
        public void run() {
            while (true) {
                try {
                    String msg = (String) sInput.readObject();
                    System.out.print(msg);
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Server has gone offline");
                    System.exit(0);
                }

            }

        }
    }
}
