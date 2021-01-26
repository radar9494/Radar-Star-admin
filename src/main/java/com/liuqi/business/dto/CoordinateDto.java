package com.liuqi.business.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * description: CoordinateDto <br>
 * date: 2020/6/11 9:21 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
@Data
@Builder
public class CoordinateDto implements Serializable {

    private LocalDate x;
    private double y;
}
