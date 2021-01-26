package com.liuqi.business.model;

import lombok.Data;

@Data
public class TransferModelDto extends TransferModel{

						
     private String name;

     private String receiveName;

     private String currencyName;

     private String address;

     private String receiveAddress;

}
