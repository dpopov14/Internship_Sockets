package org.example.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    public String username;

    /** @param clientSocket - for connecting with the server
     * @param username - for distinguishing the clients */
    public Client(Socket clientSocket, String username) {
        this.clientSocket = clientSocket;
        this.username = username;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            closeEverything(clientSocket, in, out);
        }
    }

    /** Send messages to the client handler */
    public void sendMessage(){
        out.write(username);
        out.println();
        out.flush();

        Scanner scanner = new Scanner(System.in);
        while(clientSocket.isConnected()){
            String messageToSend = scanner.nextLine();
            out.println(username + ": " + messageToSend);
            out.flush();
        }
    }

    /** Used for listening for incoming messages on the Client's side
     *  Also is concurrently running, but here it is done directly in the method instead of
     *  implementing Runnable
     * */
    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {

                String messageFromGroupChat;
                while(clientSocket.isConnected()){
                    try{
                        messageFromGroupChat = in.readLine();
    //                    if(messageFromGroupChat.equals("/exit")){
    //                        closeEverything(clientSocket, in, out);
    //                        return;
    //                    }else{
    //
    //                    }
                        System.out.println(messageFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(clientSocket, in, out);
                    }
                }
            }
        }).start(); //We are running the thread immediately
    }


    /** Closes all relevant information channels */
    private void closeEverything(Socket clientSocket, BufferedReader in, PrintWriter out) {
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

    /** Initializer for the Client. At this stage it only requires a username
     * @TODO: add port argument
     * @TODO: add a TUI
     * */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter you username for the groupchat: ");
        String username = scanner.nextLine();
        Socket clientSocket = new Socket("localhost", 1234);
        Client client = new Client(clientSocket, username);
        client.listenForMessage();
        client.sendMessage();

    }

}
