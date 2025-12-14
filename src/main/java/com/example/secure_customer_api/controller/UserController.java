package com.example.secure_customer_api.controller;

import com.example.secure_customer_api.dto.UpdateProfileDTO;
import com.example.secure_customer_api.dto.UserResponseDTO;
import com.example.secure_customer_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        UserResponseDTO user = userService.getCurrentUser(username);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
public ResponseEntity<UserResponseDTO> updateProfile(
        @Valid @RequestBody UpdateProfileDTO dto) {

    Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

    String username = authentication.getName();

    UserResponseDTO updatedUser = userService.updateProfile(username, dto);

    return ResponseEntity.ok(updatedUser);
}

    @DeleteMapping("/account")
public ResponseEntity<?> deleteAccount(@RequestParam String password) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String username = authentication.getName();

    userService.deleteAccount(username, password);

    return ResponseEntity.ok(Map.of("message", "Account deleted successfully"));
}

}