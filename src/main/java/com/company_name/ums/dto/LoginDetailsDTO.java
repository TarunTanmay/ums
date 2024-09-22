package com.company_name.ums.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class LoginDetailsDTO {
    private String name;
    private String code;
    private String email;
    private UUID token;
    private long phone;
    private String type;
}
