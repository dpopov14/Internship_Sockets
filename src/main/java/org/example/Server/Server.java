package org.example.Server;

import org.example.Client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Server implementation
 * This is the structure of the Application:
 *
 * Initially the client makes the connection to the server:
 *
 *            [Client]----------------------------[Server]
 *      Trying to connect to port x         listening in port x
 *
 * Once connected, the server creates a ClientHandler, which is used to communicate with the client from then on. In this implementation, the Server is only used to establish the connection with the client.
 * Once this happens, the server passes the socket for communication with the client to the respective ClientHandler and the ClientHandler handles communication with that Client
 *
          ______________         (out)___________(in)
         |    Client    | ------|                    |-------[ClientHandler]
         ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯         (in)¯¯¯¯¯¯¯¯¯¯¯(out)
 *
 * The ClientHandler broadcastMessage method goes through the list of handlers and uses them to distribute the message to all clients
 * In a way, the Server is acting as a manager that appoints ClientHandlers to Clients, serving no further role.
 * The ClientHandlers, the server's "minions" are doing the heavy lifting when it comes to message distribution.
 */

public class Server {
    private ServerSocket serverSocket;
    /** List of ClientHandlers, one for each connected client. */
    public ArrayList<ClientHandler> clientHandlers;


    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        clientHandlers = new ArrayList<>();
    }




    /** Methods that handles all logic related to starting the server and establishing a connection with a client if need be. */
    public void startServer(){

        try {
            while (!serverSocket.isClosed()){
                Socket clientSocket = serverSocket.accept();
                System.out.println("A new client has connected!");

                /** Client instances will each be handled on a separate thread */
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /** Methods that handles stopping the server */
    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Initializer for the server when the program is run
     * @TODO: Add a port parameter
     * @TODO: Add a TUI
     * */
    public static void main(String[] args) throws IOException {

        /** Create a new server socket that will be listening for connections from clients on the specified port */
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();

        /** Close the server socket */
    }
}
