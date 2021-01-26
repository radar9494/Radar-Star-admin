package com.liuqi.third.coindog.dto;

import lombok.Data;

import java.util.List;

@Data
public class CoinListDto {

    private String date;

    private List<CoinLivesDto> lives;
}
