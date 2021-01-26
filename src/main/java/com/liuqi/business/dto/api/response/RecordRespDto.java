package com.liuqi.business.dto.api.response;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.liuqi.business.model.UserWalletLogModelDto;
import com.liuqi.utils.MathUtil;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
@Builder
@Data
public class RecordRespDto {
    private Long id;
    private String  currency;
    private String quantity;
    private String balance;
    private Integer type;
    private String typeStr;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date time;



    public static List<RecordRespDto> transfer(String currency,List<UserWalletLogModelDto> logList){
        List<RecordRespDto> list=new ArrayList<RecordRespDto>();
        logList.stream().forEach(t->{
            list.add(RecordRespDto.builder()
                    .id(t.getId())
                    .currency(currency)
                    .quantity(MathUtil.format(t.getMoney()))
                    .balance(MathUtil.format(t.getBalance()))
                    .type(t.getType())
                    .typeStr(t.getTypeStr())
                    .time(t.getCreateTime())
                    .build());
        });
        return list;
    }
}
