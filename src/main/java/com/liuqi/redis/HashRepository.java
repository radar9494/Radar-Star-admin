package com.liuqi.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HashRepository<K, M, V> {


    private HashOperations<K, M, V> hashOperations;

    @Autowired
    public HashRepository(HashOperations<K, M, V> hashOperations) {
        this.hashOperations = hashOperations;
    }

    /**
     * 获取redis 中 hash 的值
     */
    public V get(K k, M m) {
        return hashOperations.get(k, m);
    }

    /**
     * 设置值
     *
     * @param k
     * @param m
     * @param v
     */
    public void set(K k, M m, V v) {
        hashOperations.put(k, m, v);
    }

    public void set(K k, Map<M,V> map) {
        hashOperations.putAll(k, map);
    }

    /**
     * 自增1
     *
     * @param k
     * @param m
     */
    public void increment(K k, M m) {
        hashOperations.increment(k, m, 1L);
    }

    public Double increment(K k, M m, double num) {
        return hashOperations.increment(k, m, num);
    }


}
