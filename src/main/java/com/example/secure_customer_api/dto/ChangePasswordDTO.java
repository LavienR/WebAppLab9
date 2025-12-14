package com.example.secure_customer_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChangePasswordDTO {

    @NotBlank(message = "Current password must not be blank")
    private String currentPassword;

    @NotBlank(message = "New password must not be blank")
    @Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;

    @NotBlank(message = "Confirm password must not be blank")
    private String confirmPassword;

    // Constructors
    public ChangePasswordDTO() {
    }
    public ChangePasswordDTO(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
    
    // Getters and Setters
    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}