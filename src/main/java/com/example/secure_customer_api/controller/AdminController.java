package com.example.secure_customer_api.controller;

import com.example.secure_customer_api.dto.UpdateRoleDTO;
import com.example.secure_customer_api.dto.UserResponseDTO;
import com.example.secure_customer_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    //Task 8.1 List all users (ADMIN only)
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    //Task 8.2 Update user role
    @PutMapping("/users/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleDTO dto) {

        return ResponseEntity.ok(userService.updateUserRole(id, dto));
    }

    //Task 8.3 Activate and deactivate user
    @PatchMapping("/users/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> toggleUserStatus(@PathVariable Long id) {

        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }
}
