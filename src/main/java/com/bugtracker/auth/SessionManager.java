package com.bugtracker.auth;

public class SessionManager {
    private static int loggedInUserId = 0; // Store logged-in user ID
    private static String loggedInUsername = ""; // Store logged-in username

    public static void setLoggedInUser(int userId, String username) {
        loggedInUserId = userId;
        loggedInUsername = username;
    }

    public static int getLoggedInUserId() {
        return loggedInUserId;
    }

    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    public static void logout() {
        loggedInUserId = 0;
        loggedInUsername = ""; // ✅ Reset the username properly
    }
}
