package com.example.secure_customer_api.dto;

public class LoginResponseDTO {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private String role;
    
    public LoginResponseDTO() {
    }
    
    public LoginResponseDTO(String accessToken, String refreshToken,
                            String username, String email, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.email = email;
        this.role = role;
    }
    
    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}
