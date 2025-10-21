package com.controledu.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String usuario;
    private String password;
}