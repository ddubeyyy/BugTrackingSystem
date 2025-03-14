package com.bugtracker.gui;

import javax.swing.*;
import javax.websocket.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URI;
import java.util.List;

@ClientEndpoint
public class ChatGUI extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JComboBox<String> testerDropdown; // ✅ Dropdown for selecting a tester
    private Session session;
    private String senderUsername;

    public ChatGUI(String senderUsername, List<String> testersList) {
        this.senderUsername = senderUsername;

        setTitle("Chat - " + senderUsername);
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 🌟 Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(50, 115, 220));

        JLabel lblTitle = new JLabel("Chat System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);

        JButton closeButton = new JButton("X");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        headerPanel.add(closeButton, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 🌟 Chat Display Area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);

        JScrollPane chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(chatScrollPane, BorderLayout.CENTER);

        // 🌟 User Selection + Message Input
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        testerDropdown = new JComboBox<>(testersList.toArray(new String[0])); // ✅ Select recipient
        testerDropdown.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(testerDropdown, BorderLayout.WEST);

        messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setPreferredSize(new Dimension(250, 35));
        inputPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(50, 115, 220));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        // 🌟 WebSocket Connection
        connectWebSocket();

        // 🌟 KeyListener: Send on Enter Key
        messageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });

        setVisible(true);
    }

    private void connectWebSocket() {
        try {
            URI uri = new URI("ws://localhost:8080/ws/chat/" + senderUsername);
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, uri);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "⚠ Error connecting to chat server!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");

            // ✅ Auto-scroll to the latest message
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        String recipient = testerDropdown.getSelectedItem().toString(); // ✅ Get selected recipient

        if (!message.isEmpty() && session != null) {
            try {
                String fullMessage = senderUsername + " -> " + recipient + ": " + message;
                session.getBasicRemote().sendText(fullMessage);
                chatArea.append(fullMessage + "\n"); // Show sent message
                messageField.setText("");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }
}
