package com.bugtracker.chat;

import com.bugtracker.db.DatabaseConnection;
import org.glassfish.tyrus.server.Server;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{username}")
public class ChatServer {

    private static final ConcurrentHashMap<String, Session> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        startServer(); // ✅ Start WebSocket Server
    }

    public static void startServer() {
        new Thread(() -> {
            Server server = new Server("localhost", 8080, "/ws", null, ChatServer.class);
            try {
                System.out.println("✅ Chat Server started at ws://localhost:8080/ws/chat/{username}");
                server.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static List<String> getOnlineUsers() {
        List<String> onlineUsers = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM users WHERE is_online = 1");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                onlineUsers.add(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return onlineUsers;
    }


    @OnOpen
    public void onOpen(@PathParam("username") String username, Session session) {
        clients.put(username, session);
        System.out.println("📢 " + username + " joined the chat.");
    }

    @OnMessage
    public void onMessage(@PathParam("username") String sender, String message) {
        String[] parts = message.split("->");
        if (parts.length < 2) return;

        String recipient = parts[1].split(":")[0].trim(); // Get the recipient name
        String actualMessage = parts[1].split(":")[1].trim(); // Extract actual message

        Session recipientSession = clients.get(recipient);
        if (recipientSession != null) {
            try {
                recipientSession.getBasicRemote().sendText(sender + ": " + actualMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @OnClose
    public void onClose(@PathParam("username") String username, Session session) {
        clients.remove(username);
        System.out.println("❌ " + username + " left the chat.");
    }
}
