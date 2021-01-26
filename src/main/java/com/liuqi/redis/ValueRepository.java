package com.liuqi.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class ValueRepository<K, V> {

    private ValueOperations<K, V> valueOperations;

    @Autowired
    public ValueRepository(ValueOperations<K, V> valueOperations) {
        this.valueOperations = valueOperations;
    }

    public void setKey(K key, V content, Long time, TimeUnit unit) {
        valueOperations.set(key, content, time, unit);
    }

    public void setKey(K key, V content) {
        valueOperations.set(key, content);
    }

    public V get(K key) {
        return valueOperations.get(key);
    }


}
