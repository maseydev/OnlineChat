package main.java.ru.masey.server;

import main.java.ru.masey.Division;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class CommonProxy {
    static final int PORT = 3443;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public CommonProxy() throws IOException {
        if (Division.SERVER) {
            Socket clientSocket = null;
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(PORT);
                System.out.println("Сервер запущен!");
                while (true) {
                    clientSocket = serverSocket.accept();
                    ClientHandler client = new ClientHandler(clientSocket, this);
                    clients.add(client);
                    new Thread(client).start();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                clientSocket.close();
                System.out.println("Сервер остановлен");
                serverSocket.close();
            }
        }
    }

    public void sendMessageToAllClients(String msg) {
        if (Division.SERVER) {
            for (ClientHandler o : clients) {
                o.sendMsg(msg);
            }
        }
    }

    public void removeClient(ClientHandler client) {
        if (Division.SERVER) {
            clients.remove(client);
        }
    }
}