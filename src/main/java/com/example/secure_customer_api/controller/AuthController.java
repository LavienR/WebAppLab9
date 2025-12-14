package com.example.secure_customer_api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.secure_customer_api.dto.ChangePasswordDTO;
import com.example.secure_customer_api.dto.ForgotPasswordDTO;
import com.example.secure_customer_api.dto.LoginRequestDTO;
import com.example.secure_customer_api.dto.LoginResponseDTO;
import com.example.secure_customer_api.dto.RegisterRequestDTO;
import com.example.secure_customer_api.dto.ResetPasswordDTO;
import com.example.secure_customer_api.dto.UserResponseDTO;
import com.example.secure_customer_api.service.UserService;
import com.example.secure_customer_api.dto.RefreshTokenDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        UserResponseDTO response = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        UserResponseDTO user = userService.getCurrentUser(username);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // In JWT, logout is handled client-side by removing token
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully. Please remove token from client.");
        return ResponseEntity.ok(response);
    }
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordDTO dto) {

        // 1. Get current user from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // 2. Verify current password
        // 3. Check new password matches confirm
        // 4. Hash and update password
        // (Delegated to service layer)
        userService.changePassword(username, dto);

        // 5. Return success message
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordDTO dto) {

        userService.forgotPassword(dto);

        return ResponseEntity.ok(
                Map.of("message", "Password reset token generated (check email)"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordDTO dto) {

        userService.resetPassword(dto);

        return ResponseEntity.ok(
                Map.of("message", "Password reset successfully"));
    }
    @PostMapping("/refresh")
public ResponseEntity<LoginResponseDTO> refreshToken(
        @Valid @RequestBody RefreshTokenDTO dto) {

    return ResponseEntity.ok(userService.refreshToken(dto));
}

}
