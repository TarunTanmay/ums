package org.example.ums.dto;

import lombok.*;

@Setter @Getter
public class UserDetailsDTO {
    private String username;
    private String email;
    private long phone;
    private String code;
}
