package com.mango.spike_redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
class SpikeRedisApplicationTests {

    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("name","小鸡吃米");
        System.out.println(redisTemplate.opsForValue().get("name"));
    }


}
