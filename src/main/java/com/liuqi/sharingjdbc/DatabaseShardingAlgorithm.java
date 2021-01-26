package com.liuqi.sharingjdbc;


import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;

import java.util.Collection;

public class DatabaseShardingAlgorithm implements PreciseShardingAlgorithm<Long> {
    @Override
    public String doSharding(Collection<String> databases, PreciseShardingValue<Long> shardingValue) {
        for (String each : databases) {
            if (each.endsWith(shardingValue.getValue() % 4 + "")) {
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }
}
