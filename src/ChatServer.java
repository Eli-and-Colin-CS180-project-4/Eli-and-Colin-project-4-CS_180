
import java.util.Scanner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;

    private ChatServer() {
        port = 1500;
    }

    private ChatServer(int port) {
        this.port = port;
    }

    /*
     * This is what starts the ChatServer.
     * Right now it just creates the socketServer and adds a new ClientThread to a list to be handled
     */
    private void start() {

            try {
                ServerSocket serverSocket = new ServerSocket(port);

                while (true) {
                    Socket socket = serverSocket.accept();
                    Runnable r = new ClientThread(socket, uniqueId++);
                    Thread t = new Thread(r);
                    clients.add((ClientThread) r);
                    t.start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer();

        if(args.length == 1) {
            server = new ChatServer(Integer.parseInt(args[0]));
        }

        server.start();
    }


    /*
     * This is a private class inside of the ChatServer
     * A new thread will be created to run this every time a new client connects.
     */
    private final class ClientThread implements Runnable {
        Socket socket;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        int id;
        String username;
        ChatMessage cm;

        private ClientThread(Socket socket, int id) {
            this.id = id;
            this.socket = socket;
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                username = (String) sInput.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * The remove method.
         * This method takes a ClientThread ID as a parameter and
         * removes the ClientThread from the clients ArrayList that shares
         * the ID.
         *
         * The method is synchronized to ensure that there is no concurrency issues
         * when removing the client from the clients ArrayList.
         *
         * Use this method when a user disconnects to remove their ClientThread from the list of active threads.
         * 
         * @param theID the ID of the ClientThread object to be removed.
         */
        public synchronized void remove(int theID) {
            //Iterate across the clients ArrayList.
            for (ClientThread temp: clients) {
                //If the ID of the current ClientThread object matches the given ID.
                if (temp.id == theID) {
                    //Remove the object from the clients ArrayList.
                    clients.remove(temp);
                }
            }
        }

        /*
         * This is what the client thread actually runs.
         */
        @Override
        public void run() {
            // Read the username sent to you by client
            while (true) {
                try {
                    cm = (ChatMessage) sInput.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                System.out.println(username + ":ping");


                // Send message back to the client
                try {
                    sOutput.writeObject("Pong");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
