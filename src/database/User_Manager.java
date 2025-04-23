package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class User_Manager {
    private Map<String, User> cachedUsers;
    private Connection connection;
    
    public User_Manager(Connection connection) {
        this.connection = connection;
        this.cachedUsers = new HashMap<>();
        // Load users from database
        loadUsersFromDatabase();
    }
    
    /**
     * Loads all users from the database into the cache
     */
    private void loadUsersFromDatabase() {
        try {
            String query = "SELECT username, password, email, full_name FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String email = resultSet.getString("email");
                String fullName = resultSet.getString("full_name");
                
                User user = new User(username, password, email, fullName);
                cachedUsers.put(username, user);
            }
            
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public boolean addUser(User user) {
        if (user != null && !userExists(user.getUsername())) {
            // Add to database
            try {
                String query = "INSERT INTO users (username, password, email, full_name) VALUES (?, ?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, user.getUsername());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getEmail());
                statement.setString(4, user.getFullName());
                
                int result = statement.executeUpdate();
                statement.close();
                
                if (result > 0) {
                    // Add to cache
                    cachedUsers.put(user.getUsername(), user);
                    return true;
                }
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Updates user password
     */
    public boolean updateUserPassword(String username, String newPassword) {
        User user = getUser(username);
        if (user != null) {
            try {
                String query = "UPDATE users SET password = ? WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, newPassword);
                statement.setString(2, username);
                
                int result = statement.executeUpdate();
                statement.close();
                
                if (result > 0) {
                    // Update in cache
                    user.setPassword(newPassword);
                    return true;
                }
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * Checks if a username exists
     */
    public boolean userExists(String username) {
        return cachedUsers.containsKey(username);
    }

    /**
     * Gets user by username
     */
    public User getUser(String username) {
        return cachedUsers.get(username);
    }
    
    /**
     * Authenticates a user with given credentials
     */
    public boolean authenticateUser(String username, String password) {
        User user = getUser(username);
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }
    
    /**
     * Updates user information
     */
    public boolean updateUserInfo(String username, String email, String fullName) {
        User user = getUser(username);
        if (user != null) {
            try {
                String query = "UPDATE users SET email = ?, full_name = ? WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, email);
                statement.setString(2, fullName);
                statement.setString(3, username);
                
                int result = statement.executeUpdate();
                statement.close();
                
                if (result > 0) {
                    // Update in cache
                    user.setEmail(email);
                    user.setFullName(fullName);
                    return true;
                }
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    /**
     * Removes a user from the system
     */
    public boolean removeUser(String username) {
        if (userExists(username)) {
            try {
                String query = "DELETE FROM users WHERE username = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username);
                
                int result = statement.executeUpdate();
                statement.close();
                
                if (result > 0) {
                    // Remove from cache
                    cachedUsers.remove(username);
                    return true;
                }
                return false;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    /**
     * Gets all users in the system
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(cachedUsers); // Return a copy to prevent direct modification
    }
    public void reloadUsersFromDatabase() {
        // Clear the existing cache
        cachedUsers.clear();
        // Reload from database
        loadUsersFromDatabase();
    }
}