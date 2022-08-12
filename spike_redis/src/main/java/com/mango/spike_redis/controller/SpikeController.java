package com.mango.spike_redis.controller;



import com.mango.spike_redis.service.impl.RedisServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;


@RestController
@RequestMapping
@Slf4j
public class SpikeController {

@Autowired
private RedisServiceImpl redisService;

    @GetMapping("/spike")
    public String spike(@RequestParam String proId){
        StringBuilder uId = new StringBuilder();
        Random random = new Random();
        for (int i=0;i<6;i++){
            int anInt = random.nextInt(10);
            uId.append(anInt);
        }
        String spike = redisService.spike(proId, uId);
        return spike;





    }
}
