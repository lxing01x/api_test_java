package com.automation.service;

import com.automation.core.ResponseHandler;
import com.automation.model.AuthRequest;
import com.automation.model.AuthResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthService extends BaseService {

    private static final String BASE_PATH = "/api/v1/auth";

    public AuthService() {
        super(BASE_PATH);
    }

    public ResponseHandler login(AuthRequest request) {
        log.info("Performing login for user: {}", request.getUsername());
        return post("/login", request);
    }

    public AuthResponse loginAndGetToken(AuthRequest request) {
        ResponseHandler response = login(request);
        response.validateStatusCodeIsSuccess();
        
        AuthResponse authResponse = response.getBodyAs(AuthResponse.class);
        setAuthToken(authResponse.getToken());
        storeToContext("authToken", authResponse.getToken());
        storeToContext("refreshToken", authResponse.getRefreshToken());
        
        log.info("Login successful, token obtained");
        return authResponse;
    }

    public ResponseHandler logout() {
        log.info("Performing logout");
        ResponseHandler response = post("/logout", null);
        clearAuthToken();
        return response;
    }

    public ResponseHandler refreshToken() {
        log.info("Refreshing token");
        String refreshToken = getFromContext("refreshToken");
        
        var request = new Object() {
            String refreshToken = refreshToken;
        };
        
        return post("/refresh", request);
    }

    public ResponseHandler register(Object registrationRequest) {
        log.info("Performing user registration");
        return post("/register", registrationRequest);
    }

    public ResponseHandler getCurrentUser() {
        log.info("Getting current user info");
        return get("/me");
    }

    public ResponseHandler changePassword(String oldPassword, String newPassword) {
        log.info("Changing password");
        
        var request = new Object() {
            String oldPassword = oldPassword;
            String newPassword = newPassword;
        };
        
        return post("/change-password", request);
    }
}
