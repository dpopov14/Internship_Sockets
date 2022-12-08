package org.example.Server;

import org.example.Client.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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


    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {

        /** Create a new server socket that will be listening for connections from clients on the specified port */
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();

        /** Close the server socket */
    }
}
