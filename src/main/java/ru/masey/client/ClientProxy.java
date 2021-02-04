package main.java.ru.masey.client;

import main.java.ru.masey.Division;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientProxy extends JFrame {

    private static final String HOST = "localhost";
    private static final int PORT = 3443;
    private Socket socket;
    private PrintWriter output;
    private Scanner input;

    private JTextField jTextFieldMessage;
    private JTextField jTextFieldName;
    private JTextArea jTextAreaMessage;

    private String clientName = "";

    public ClientProxy() throws IOException {
        if (Division.CLIENT) {
            socket = new Socket(HOST, PORT);
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream());

            setBounds(600, 300, 600, 500);
            setTitle("Online chat");
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jTextAreaMessage = new JTextArea();
            jTextAreaMessage.setEditable(false);
            jTextAreaMessage.setLineWrap(true);
            JScrollPane jScrollPane = new JScrollPane(jTextAreaMessage);
            add(jScrollPane, BorderLayout.CENTER);

            JLabel jLabelOfClients = new JLabel("Clients online: ");
            add(jLabelOfClients, BorderLayout.NORTH);
            JPanel jPanel = new JPanel(new BorderLayout());
            add(jPanel, BorderLayout.SOUTH);

            JButton jButtonSendMessage = new JButton("Send");
            jPanel.add(jButtonSendMessage, BorderLayout.EAST);

            jTextFieldMessage = new JTextField("Input message: ");
            jPanel.add(jTextFieldMessage, BorderLayout.CENTER);

            jTextFieldName = new JTextField("Input your name: ");
            jPanel.add(jTextFieldName, BorderLayout.WEST);

            jButtonSendMessage.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    if (!jTextFieldMessage.getText().isEmpty() && !jTextFieldName.getText().isEmpty()) {
                        clientName = jTextFieldName.getText();
                        sendMessage();
                        jTextFieldMessage.grabFocus();
                    }
                }
            });
            jTextFieldMessage.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    jTextFieldMessage.setText("");
                }
            });

            jTextFieldName.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    jTextFieldName.setText("");
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        if (input.hasNext()) {
                            String inMsg = input.nextLine();
                            String clientsInChat = "Clients in chat: ";
                            if (inMsg.indexOf(clientsInChat) == 0) {
                                jLabelOfClients.setText(inMsg);
                            } else {
                                jTextAreaMessage.append(inMsg);
                                jTextAreaMessage.append("\n");
                            }
                        }
                    }
                }
            }).start();

            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent windowEvent) {
                    super.windowClosing(windowEvent);
                    if (!clientName.isEmpty() && clientName != "Input your name: ") {
                        output.println(clientName + "leave chat");
                    } else {
                        output.println("Member leave chat, without introducing myself");
                    }
                    output.println("##session##end##");
                    output.flush();
                    output.close();
                    input.close();

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            setVisible(true);
        }
    }

    public void sendMessage() {
        if (Division.CLIENT) {
            String messageStr = jTextFieldName.getText() + ": " + jTextFieldMessage.getText();
            output.println(messageStr);
            output.flush();
            jTextFieldMessage.setText("");
        }
    }
}