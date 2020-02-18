package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ChatServer implements Runnable {
    private final static int PORT = 8000;
    private final static int MAX_CLIENTS = 5;
    private final static Executor executor = Executors.newFixedThreadPool(MAX_CLIENTS);
    private CopyOnWriteArrayList messages;
    private int index;

    private final Socket clientSocket;
    private String clientName = "";

    private ChatServer(Socket clientSocket, CopyOnWriteArrayList messages) {
        this.clientSocket = clientSocket;
        this.messages = messages;



    }

    public void run() {
        SocketAddress remoteSocketAddress = clientSocket.getRemoteSocketAddress();
        SocketAddress localSocketAddress = clientSocket.getLocalSocketAddress();
        System.out.println("Accepted client " + remoteSocketAddress
                + " (" + localSocketAddress + ").");

        PrintWriter socketWriter = null;
        BufferedReader socketReader = null;
        try {
            socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            socketReader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );


            String threadInfo = " (" + Thread.currentThread().getName() + ").";
            String inputLine = socketReader.readLine();
            System.out.println("Received: \"" + inputLine + "\" from "
                    + remoteSocketAddress + threadInfo);

            // First message is client name.
            PrintWriter finalSocketWriter = socketWriter;
            new Thread(() -> {
                int currentIndex = messages.size();
                while (true) {
                    if (currentIndex < messages.size()) {
                        finalSocketWriter.println(messages.get(currentIndex));
                        currentIndex++;
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();


            clientName = inputLine;

            while (inputLine != null) {
                messages.add("Client name " +  clientName + " sent message: " + inputLine);
                inputLine = socketReader.readLine();


            }


            System.out.println("Closing connection " + remoteSocketAddress
                    + " (" + localSocketAddress + ").");
        } catch (Exception exception) {
            System.out.println(exception);
        } finally {
            try {
                if (socketWriter != null)
                    socketWriter.close();
                if (socketReader != null)
                    socketReader.close();
                if (clientSocket != null)
                    clientSocket.close();
            } catch (Exception exception) {
                System.out.println(exception);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Server2 started.");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
            SocketAddress serverSocketAddress = serverSocket.getLocalSocketAddress();
            System.out.println("Listening (" + serverSocketAddress + ").");
            CopyOnWriteArrayList<String> messages = new CopyOnWriteArrayList<String>();

            while (true) {
                clientSocket = serverSocket.accept();
                executor.execute(new ChatServer(clientSocket, messages));
            }
        } catch (Exception exception) {
            System.out.println(exception);
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (Exception exception) {
                System.out.println(exception);
            }
        }
    }
}