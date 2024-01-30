import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import java.io.*;
import java.net.Socket;


public class PasswordGUI extends JFrame{
    private JTextField messageField1 , messageField;
    private JButton button;
    private JLabel label1 , label2;
    private JTextArea chatArea;

    public PasswordGUI(){
        setTitle("Login!");
        setSize(500,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);

        label1 = new JLabel("Enter your Name : ");
        label1.setBounds(10,50,200,80);
        add(label1);

        messageField1 = new JTextField();
        messageField1.setBounds(10,120,380,30);
        add(messageField1);

        label2 = new JLabel("Enter your password : ");
        label2.setBounds(10,150,200,80);
        add(label2);

        messageField = new JTextField();
        messageField.setBounds(10,220,380,30);
        add(messageField);

        button = new JButton("Submit");
        button.setBounds(180,270,80,30);
        add(button);

        chatArea = new JTextArea();
        chatArea.setBounds(10, 400, 470, 50);
        chatArea.setEditable(false);
        add(chatArea);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String message = messageField.getText();
                chatArea.setText("");
                if(check(message)){
                    openChatClient();
                    dispose();
                }
                else
                {
                    chatArea.append("Wrong Password!");
                }
                messageField.setText("");
            }
        });
    }
    private boolean check(String message){
        try{
            String line;
            File file = new File("passwords.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while((line = reader.readLine())!=null){
                if(line.trim().equals(message)){
                    reader.close();
                    return true;
                }
            }
            reader.close();
        } catch (IOException e){
            e.printStackTrace();;
        }
        return false;
    }

    private void openChatClient() {
        String msg = messageField1.getText();
        ChatClientGUI chatClient = new ChatClientGUI(msg);
        chatClient.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PasswordGUI().setVisible(true);
            }
        });
    }
}
class ChatClientGUI extends JFrame {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private JLabel label;
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter writer;

    public ChatClientGUI(String msg1) {
        setTitle("Chatting !");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        label =new JLabel("Welcome "+msg1);
        label.setBounds(10,15,470,10);
        add(label);

        chatArea = new JTextArea();
        chatArea.setBounds(10, 50, 470, 350);
        chatArea.setEditable(false);
        add(chatArea);

        messageField = new JTextField();
        messageField.setBounds(10, 420, 380, 30);
        add(messageField);

        JButton sendButton = new JButton("Send");
        sendButton.setBounds(400, 420, 80, 30);
        add(sendButton);

        sendButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                String message = messageField.getText();
                sendMessage(message);
                messageField.setText("");
            }
        });

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(new Runnable() {
                public void run() {
                    try {
                        while (true) {
                            String message = reader.readLine();
                            if (message != null) {
                                chatArea.append("Message : " + message + "\n");
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String message) {
        writer.println(message);
    }
}