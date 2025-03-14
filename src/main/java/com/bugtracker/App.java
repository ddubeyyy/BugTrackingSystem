package com.bugtracker;

import com.bugtracker.gui.LoginGUI;
import com.bugtracker.chat.ChatServer;

public class App {
    public static void main(String[] args) {
        LoginGUI.main(args);
        ChatServer.startServer();// ✅ This ensures the GUI starts
    }
}
