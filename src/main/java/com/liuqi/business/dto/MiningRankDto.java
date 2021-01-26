package com.liuqi.business.dto;

import lombok.Builder;
import lombok.Data;

/**
 * description: MiningRankDto <br>
 * date: 2020/6/9 14:11 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */
@Builder
@Data
public class MiningRankDto  {

    private long rank;
    private long total;

}
