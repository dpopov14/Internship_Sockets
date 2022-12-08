package org.example.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/** Implements Runnable so that instances will be executed by a separate thread */
public class ClientHandler implements Runnable {


    /** Keeps track of all our clients - this is why it is inside the class definition -
     * since it is a static property, it will be accessible through all instances
     */
    private static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    //The socket that will be passed from the server
    private Socket clientSocket;
    //Read messages that will be sent by the client
    private BufferedReader in;
    //Send messages that will be sent to other clients
    private PrintWriter out;

    private String clientUsername;
    private Client client;

    public ClientHandler(Socket clientSocket){

        try {
            this.clientSocket = clientSocket;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
//            System.out.println("Please enter a username: \n");
            this.clientUsername = in.readLine();
            //Add this instance to the list of clientHandlers:
            clientHandlers.add(this);
            broadcastMessage("SERVER" + clientUsername + "has entered the chat!");
        } catch (IOException e) {
            //Close the connection to the server:
            closeEverything(clientSocket, in, out);
        }

    }


    /** Listening functionality for when the handler is running*/
    @Override
    public void run() {
        //What we will on a separate thread: listen for messages
        String messageFromClient;

        //As long as the Handler is runnign, try to read incoming messages. Sending messages is implemented in @broadcastMessage()
        while(!clientSocket.isClosed()){
            try {
                messageFromClient = in.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(clientSocket, in, out);
                break;
            }
        }
    }


    /** Broadcast a message to all connected clients */
    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler : clientHandlers){
            try {
                //Check so that we don't message ourselves
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.out.println(messageToSend);
//                    out.newLine();
                    //Manually flush so that the message is sent:
                    clientHandler.out.flush();
                }
            } catch (Exception e) {
                closeEverything(clientSocket, in, out);
            }
        }
    }

    /** Removes the current handler from the list of handlers.
     * DOES NOT CLOSE THE COMMUNICATION CHANNELS! Complementary method to closeEverything() */
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + "has left the chat!");
    }


    /** Closes all connections to the corresponding client */
    public void closeEverything(Socket clientSocket, BufferedReader in, PrintWriter out){
        removeClientHandler();
        try {
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(clientSocket!= null){
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
