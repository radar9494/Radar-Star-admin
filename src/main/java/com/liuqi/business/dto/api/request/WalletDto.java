package com.liuqi.business.dto.api.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
@Data
public class WalletDto implements Serializable {
    private String currency;

    private Long userId;

}
