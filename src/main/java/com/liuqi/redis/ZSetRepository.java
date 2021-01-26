package com.liuqi.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ZSetRepository<K, M> {

    private ZSetOperations<K, M> zSetRepository;

    @Autowired
    public ZSetRepository(ZSetOperations<K, M> zSetRepository) {
        this.zSetRepository = zSetRepository;
    }


    /**
     * 新增总量
     *
     * @param userId
     * @param num
     */
    public void incrementScore(K k, M m, int num) {
        zSetRepository.incrementScore(k, m, num);
    }

    public void incrementScore(K k, M m, double num) {
        zSetRepository.incrementScore(k, m, num);
    }
    public Double score(K k, M m) {
        return zSetRepository.score(k, m);
    }

    public Long reverseRank(K k, M m) {
        return zSetRepository.reverseRank(k, m);
    }


    /**
     * 获取排行
     *
     * @param start
     * @param end
     */
    public Set<ZSetOperations.TypedTuple<M>> rangeWithScores(K k, long start, long end) {
        return zSetRepository.rangeWithScores(k, start, end);
    }

    public Set<ZSetOperations.TypedTuple<M>> reverseRangeByScoreWithScores(K k, long start, long end) {
        return zSetRepository.reverseRangeWithScores(k, start, end);
    }



}
