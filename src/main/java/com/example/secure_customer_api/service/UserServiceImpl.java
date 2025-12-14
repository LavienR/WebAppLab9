package com.example.secure_customer_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secure_customer_api.dto.ChangePasswordDTO;
import com.example.secure_customer_api.dto.ForgotPasswordDTO;
import com.example.secure_customer_api.dto.LoginRequestDTO;
import com.example.secure_customer_api.dto.LoginResponseDTO;
import com.example.secure_customer_api.dto.RefreshTokenDTO;
import com.example.secure_customer_api.dto.RegisterRequestDTO;
import com.example.secure_customer_api.dto.ResetPasswordDTO;
import com.example.secure_customer_api.dto.UpdateProfileDTO;
import com.example.secure_customer_api.dto.UpdateRoleDTO;
import com.example.secure_customer_api.dto.UserResponseDTO;
import com.example.secure_customer_api.entity.RefreshToken;
import com.example.secure_customer_api.entity.Role;
import com.example.secure_customer_api.entity.User;
import com.example.secure_customer_api.exception.DuplicateResourceException;
import com.example.secure_customer_api.exception.ResourceNotFoundException;
import com.example.secure_customer_api.repository.RefreshTokenRepository;
import com.example.secure_customer_api.repository.UserRepository;
import com.example.secure_customer_api.security.JwtTokenProvider;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    
    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Generate JWT token
        String accessToken = tokenProvider.generateToken(authentication);
        
        // Get user details
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    // 2 Refresh token (7 days)
    String refreshTokenValue = UUID.randomUUID().toString();

    RefreshToken refreshToken = new RefreshToken(user,refreshTokenValue,LocalDateTime.now().plusDays(7)
    );

    refreshTokenRepository.save(refreshToken);

    return new LoginResponseDTO(
            accessToken,
            refreshTokenValue,
            user.getUsername(),
            user.getEmail(),
            user.getRole().name()
        );
    }
    
    @Override
    public UserResponseDTO register(RegisterRequestDTO registerRequest) {
        // Check if username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        
        // Check if email exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getFullName());
        user.setRole(Role.USER);  // Default role
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        return convertToDTO(savedUser);
    }
    
    @Override
    public UserResponseDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return convertToDTO(user);
    }
    
    private UserResponseDTO convertToDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getRole().name(),
            user.getIsActive(),
            user.getCreatedAt()
        );
    }
    @Override
public void changePassword(String username, ChangePasswordDTO dto) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // 1. Verify current password
    if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
        throw new IllegalArgumentException("Current password is incorrect");
    }

    // 2. Check new password matches confirm
    if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
        throw new IllegalArgumentException("New password and confirm password do not match");
    }

    // 3. Hash and update password
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

    userRepository.save(user);
}
    
    @Override
public void forgotPassword(ForgotPasswordDTO dto) {

    User user = userRepository.findByEmail(dto.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Generate reset token
    String token = UUID.randomUUID().toString();

    user.setResetToken(token);
    user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

    userRepository.save(user);

    // Return token (real app → send via email)
    System.out.println("Password reset token: " + token);
}

    @Override
public void resetPassword(ResetPasswordDTO dto) {

    User user = userRepository.findByResetToken(dto.getToken())
            .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token"));

    // Check token expiry
    if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
        throw new IllegalArgumentException("Reset token has expired");
    }

    // Update password
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

    // Clear token
    user.setResetToken(null);
    user.setResetTokenExpiry(null);

    userRepository.save(user);
}

    @Override
public UserResponseDTO updateProfile(String username, UpdateProfileDTO dto) {

    User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    user.setFullName(dto.getFullName());

    if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
        user.setEmail(dto.getEmail());
    }

    User updatedUser = userRepository.save(user);

    return convertToDTO(updatedUser);
}

    @Override
public void deleteAccount(String username, String password) {

    User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Verify password
    if (!passwordEncoder.matches(password, user.getPassword())) {
        throw new IllegalArgumentException("Password is incorrect");
    }

    // Soft delete
    user.setIsActive(false);

    userRepository.save(user);
}

    @Override
public List<UserResponseDTO> getAllUsers() {
    return userRepository.findAll()
            .stream()
            .map(this::convertToDTO)
            .toList();
}
    @Override
public UserResponseDTO updateUserRole(Long id, UpdateRoleDTO dto) {

    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    user.setRole(dto.getRole());

    return convertToDTO(userRepository.save(user));
}

    @Override
public UserResponseDTO toggleUserStatus(Long id) {

    User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    user.setIsActive(!user.getIsActive());

    return convertToDTO(userRepository.save(user));
}

    @Override
public LoginResponseDTO refreshToken(RefreshTokenDTO dto) {
    RefreshToken refreshToken = refreshTokenRepository
            .findByToken(dto.getRefreshToken())
            .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

    // Check expiry
    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
        throw new IllegalArgumentException("Refresh token expired");
    }

    User user = refreshToken.getUser();

    // ✅ Generate new access token WITHOUT Authentication
    String newAccessToken =
            tokenProvider.generateTokenFromUsername(user.getUsername());

    return new LoginResponseDTO(
            newAccessToken,
            refreshToken.getToken(), // reuse same refresh token
            user.getUsername(),
            user.getEmail(),
            user.getRole().name()
    );
}

}
