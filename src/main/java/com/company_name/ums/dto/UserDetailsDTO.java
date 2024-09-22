package com.company_name.ums.dto;

import lombok.*;

@Setter @Getter
public class UserDetailsDTO {
    private String email;
    private long phone;
    private String code;
    private String type;
}
