package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserTreeDto implements Serializable{

    private Long id;
    private String name;
    private Long pId;
    private List<UserTreeDto> children;
    private String icon;

}
