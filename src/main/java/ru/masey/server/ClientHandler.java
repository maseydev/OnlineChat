package main.java.ru.masey.server;


import main.java.ru.masey.Division;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private CommonProxy server;
    private PrintWriter output;
    private Scanner input;
    private static final String HOST = "localhost";
    private static final int PORT = 3443;
    private Socket clientSocket = null;
    private static int clients_count = 0;

    public ClientHandler(Socket socket, CommonProxy server) {
        if (Division.SERVER) {
            try {
                clients_count++;
                this.server = server;
                this.clientSocket = socket;
                this.output = new PrintWriter(socket.getOutputStream());
                this.input = new Scanner(socket.getInputStream());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        if (Division.SERVER) {
            try {
                while (true) {
                    server.sendMessageToAllClients("New user entry in chat!");
                    server.sendMessageToAllClients("Clients in chat = " + clients_count);
                    break;
                }

                while (true) {
                    if (input.hasNext()) {
                        String clientMessage = input.nextLine();
                        if (clientMessage.equalsIgnoreCase("##session##end##")) {
                            break;
                        }
                        System.out.println(clientMessage);
                        server.sendMessageToAllClients(clientMessage);
                    }
                    Thread.sleep(100);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                this.close();
            }
        }
    }

    public void sendMsg(String msg) {
        if (Division.SERVER) {
            try {
                output.println(msg);
                output.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void close() {
        if (Division.SERVER) {
            server.removeClient(this);
            clients_count--;
            server.sendMessageToAllClients("Clients in chat = " + clients_count);
        }
    }
}