package com.automation.service;

import com.automation.core.ResponseHandler;
import com.automation.model.User;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserService extends BaseService {

    private static final String BASE_PATH = "/api/v1/users";

    public UserService() {
        super(BASE_PATH);
    }

    public ResponseHandler getAllUsers() {
        log.info("Getting all users");
        return get("");
    }

    public ResponseHandler getAllUsers(int page, int size) {
        log.info("Getting all users with pagination: page={}, size={}", page, size);
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("page", page);
        queryParams.put("size", size);
        
        return get("");
    }

    public ResponseHandler getUserById(Long userId) {
        log.info("Getting user by ID: {}", userId);
        return get("/{id}", userId);
    }

    public ResponseHandler getUserByUsername(String username) {
        log.info("Getting user by username: {}", username);
        return get("/username/{username}", username);
    }

    public User createUser(User user) {
        log.info("Creating user: {}", user.getUsername());
        ResponseHandler response = post("", user);
        response.validateStatusCode(201);
        return response.getBodyAs(User.class);
    }

    public ResponseHandler updateUser(Long userId, User user) {
        log.info("Updating user: {}", userId);
        return put("/{id}", user, userId);
    }

    public ResponseHandler deleteUser(Long userId) {
        log.info("Deleting user: {}", userId);
        return delete("/{id}", userId);
    }

    public ResponseHandler searchUsers(String keyword) {
        log.info("Searching users with keyword: {}", keyword);
        
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("keyword", keyword);
        
        return get("/search");
    }

    public ResponseHandler updateUserStatus(Long userId, User.UserStatus status) {
        log.info("Updating user status: userId={}, status={}", userId, status);
        
        var request = new Object() {
            User.UserStatus status = status;
        };
        
        return patch("/{id}/status", request, userId);
    }

    public ResponseHandler uploadUserAvatar(Long userId, Object file) {
        log.info("Uploading avatar for user: {}", userId);
        return post("/{id}/avatar", file, userId);
    }
}
