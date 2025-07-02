import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ChatClient {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;

    public ChatClient() {
        frame = new JFrame("Client Chat");
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

        connectToServer();
    }

    private void connectToServer() {
        try {
            Socket socket = new Socket("localhost", 1234); // Replace with IP if needed
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            new Thread(() -> {
                String msg;
                try {
                    while ((msg = in.readLine()) != null) {
                        chatArea.append("Server: " + msg + "\n");
                    }
                } catch (IOException e) {
                    chatArea.append("Connection closed.\n");
                }
            }).start();

        } catch (IOException ex) {
            chatArea.append("Connection failed: " + ex.getMessage() + "\n");
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
        SwingUtilities.invokeLater(ChatClient::new);
    }
}
