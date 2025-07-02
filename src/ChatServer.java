import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatServer {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;

    public ChatServer() {
        frame = new JFrame("Server Chat");
        chatArea = new JTextArea(20, 50);
        inputField = new JTextField(40);
        JButton sendButton = new JButton("Send");

        chatArea.setEditable(false);
        frame.setLayout(new FlowLayout());
        frame.add(new JScrollPane(chatArea));
        frame.add(inputField);
        frame.add(sendButton);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        startServer();
    }

    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            chatArea.append("Waiting for client...\n");
            Socket socket = serverSocket.accept();
            chatArea.append("Client connected!\n");

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        chatArea.append("Client: " + msg + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("Connection closed.\n");
                }
            }).start();

        } catch (IOException ex) {
            chatArea.append("Server error: " + ex.getMessage() + "\n");
        }
    }

    private void sendMessage() {
        String msg = inputField.getText();
        if (!msg.isEmpty()) {
            chatArea.append("You: " + msg + "\n");
            out.println(msg);
            inputField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatServer::new);
    }
}
