package com.library.service;

import com.library.dao.UserDAO;
import com.library.model.User;
import java.util.List;

public class UserService {
    private final UserDAO userDAO;
    private static User currentUser;  // Store the currently logged-in user
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    public User authenticate(String username, String password) {
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            // Store the current user
            currentUser = user;
        }
        return user;
    }
    
    public boolean register(User user) {
        // Check if username already exists
        if (isUsernameExists(user.getUsername())) {
            return false;
        }
        
        return userDAO.addUser(user);
    }
    
    public boolean isUsernameExists(String username) {
        return userDAO.isUsernameExists(username);
    }
    
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }
    
    public User getUserById(int id) {
        return userDAO.getUserById(id);
    }
    
    public boolean updateUser(User user) {
        return userDAO.updateUser(user);
    }
    
    public boolean deleteUser(int id) {
        return userDAO.deleteUser(id);
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : -1;
    }
    
    public static boolean isAuthenticated() {
        return currentUser != null;
    }
    
    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    public static void logout() {
        currentUser = null;
    }
}
