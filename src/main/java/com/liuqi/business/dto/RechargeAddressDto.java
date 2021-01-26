package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RechargeAddressDto implements Serializable{

    //路径
    private String path;
    //地址
    private String address;

}
