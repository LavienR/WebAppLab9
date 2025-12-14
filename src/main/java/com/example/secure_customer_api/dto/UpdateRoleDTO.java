package com.example.secure_customer_api.dto;

import com.example.secure_customer_api.entity.Role;
import jakarta.validation.constraints.NotNull;

public class UpdateRoleDTO {

    @NotNull
    private Role role;

    public UpdateRoleDTO() {}

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
