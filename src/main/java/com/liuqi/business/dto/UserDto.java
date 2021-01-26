package com.liuqi.business.dto;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String realName;
    private String phone;
    private String email;
}
