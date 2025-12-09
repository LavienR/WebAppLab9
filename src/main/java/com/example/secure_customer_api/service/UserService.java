package com.example.secure_customer_api.service;

import com.example.secure_customer_api.dto.LoginRequestDTO;
import com.example.secure_customer_api.dto.LoginResponseDTO;
import com.example.secure_customer_api.dto.RegisterRequestDTO;
import com.example.secure_customer_api.dto.UserResponseDTO;

public interface UserService {
    
    LoginResponseDTO login(LoginRequestDTO loginRequest);
    
    UserResponseDTO register(RegisterRequestDTO registerRequest);
    
    UserResponseDTO getCurrentUser(String username);
}
