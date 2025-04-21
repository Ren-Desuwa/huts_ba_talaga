package database;

public class User_Manager {

	public User_Manager() {
		
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

	
	public boolean authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null) {
            return user.getPassword().equals(password);
        }
        return false;
    }
}

