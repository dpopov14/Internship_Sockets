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
