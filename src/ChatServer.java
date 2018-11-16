import java.util.Scanner;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

final class ChatServer {
    private static int uniqueId = 0;
    private final List<ClientThread> clients = new ArrayList<>();
    private final int port;
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();

    private ChatServer() {
        port = 1503;
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

    /**sends a message to all the chat cliets
     * This goes through a loop of all the clients and tries to
     * send a message to each one.
     *
     */
    private synchronized void broadcast(String message) {
        for(ClientThread temp : clients){
            temp.writeMessage(message);
        }
    }


    private synchronized void directMessage(String message, String username) {
        for (ClientThread temp: clients) {
            if (temp.username.equals(username)) {
                temp.writeMessage(message);
            }
        }
    }

    /*
     *  > java ChatServer
     *  > java ChatServer portNumber
     *  If the port number is not specified 1500 is used
     */
    public static void main(String[] args) {
        ChatServer server;

        if(args.length == 1) {
            server = new ChatServer(Integer.parseInt(args[0]));

        } else {
            server = new ChatServer();
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

        /** This sends the message to the chat client connected,
         * and does it weather or not the person sent it
         *
         *
         * NEEEDs to check if socket is connected?
         *
         */

        private boolean writeMessage(String msg) {

            try {
                sOutput.writeObject(formatter.format(date) + " " + username + ": "
                        + msg + "\n");
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            return true;

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
                    try {
                        //Remove the object from the clients ArrayList.
                        temp.socket.close();
                        temp.sInput.close();
                        temp.sOutput.close();
                        clients.remove(temp);
                        break;
                    } catch (IOException e){
                        return;
                    }

                }
            }
        }

             /*
          Iterator<ClientThread> iter = clients.iterator();
            while(iter.hasNext()){
                ClientThread thread = iter.next();
                if(theID == thread.id){
                    iter.remove();
                }
            }
         */

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

                if (cm.getNum() == 1) {
                    remove(id);
                    broadcast(cm.getStr());
                    break;

                } else if (cm.getRecipient() != null) {
                    directMessage(cm.getStr(), cm.getRecipient());
                } else {
                    broadcast(cm.getStr());
                }
                System.out.println(formatter.format(date) + " " + username + ": " + cm.getStr());


                /*try {
                    sOutput.writeObject(username + ": " + cm.getStr() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
            }
        }
    }
}
