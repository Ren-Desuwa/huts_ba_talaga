package database;

import java.util.HashMap;
import java.util.Map;

public class User_Manager {
    private Map<String, User> users;
    
    public User_Manager() {
        users = new HashMap<>();
        // Add some default users for testing
        addUser(new User("admin", "admin123", "admin@example.com", "Administrator"));
        addUser(new User("user", "user123", "user@example.com", "Regular User"));
    }
    
    public boolean addUser(User user) {
        if (user != null && !users.containsKey(user.getUsername())) {
            users.put(user.getUsername(), user);
            return true;
        }
        return false;
    }

    /**
     * Updates user password
     */
    public boolean updateUserPassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }

    /**
     * Checks if a username exists
     */
    public boolean userExists(String username) {
        return users.containsKey(username);
    }

    /**
     * Gets user by username
     */
    public User getUser(String username) {
        return users.get(username);
    }
    
    /**
     * Authenticates a user with given credentials
     */
    public boolean authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }
    
    /**
     * Updates user information
     */
    public boolean updateUserInfo(String username, String email, String fullName) {
        User user = users.get(username);
        if (user != null) {
            user.setEmail(email);
            user.setFullName(fullName);
            return true;
        }
        return false;
    }
    
    /**
     * Removes a user from the system
     */
    public boolean removeUser(String username) {
        if (users.containsKey(username)) {
            users.remove(username);
            return true;
        }
        return false;
    }
    
    /**
     * Gets all users in the system
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(users); // Return a copy to prevent direct modification
    }
}