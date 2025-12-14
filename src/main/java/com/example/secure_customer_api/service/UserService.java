package com.example.secure_customer_api.service;

import java.util.List;
import com.example.secure_customer_api.dto.*;

public interface UserService {
    
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    UserResponseDTO register(RegisterRequestDTO registerRequest);
    
    UserResponseDTO getCurrentUser(String username);

    void changePassword(String username, ChangePasswordDTO dto);

    void forgotPassword(ForgotPasswordDTO dto);

    void resetPassword(ResetPasswordDTO dto);

    UserResponseDTO updateProfile(String username, UpdateProfileDTO dto);

    void deleteAccount(String username, String password);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO updateUserRole(Long id, UpdateRoleDTO dto);

    UserResponseDTO toggleUserStatus(Long id);
    
    LoginResponseDTO refreshToken(RefreshTokenDTO dto);

}
