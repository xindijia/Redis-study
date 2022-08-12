package com.mango.spike_redis.service.impl;

import com.mango.spike_redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

//    static String secKillScript =
//                    "local userid=KEYS[1];\r\n"+
//                    "local prodid=KEYS[2];\r\n"+
//                    "local kcKey=\"sk:\"..prodid..\":qt\";\r\n"+
//                    "local userKey=\"sk:\"..prodid..\":user\"\r\n"+
//                    "local userExists=redis.call(\"sismember\",userKey,userid);\r\n"+
//                    "if tonumber(userExists)==1 then \r\n"+
//                    "    return 2;\r\n"+
//                    "end\r\n"+
//                    "local num=redis.call(\"get\", kcKey);\r\n"+
//                    "if tonumber(num)<=0 then \r\n"+
//                    "    return 0;"+
//                    "else "+
//                    "    redis.call(\"decr\",kcKey);\r\n"+
//                    "    redis.call(\"sadd\", userKey, userid);\r\n"+
//                    "end\r\n"+
//                    "return 1";
    @Override
    public String spike(String proId,StringBuilder uId) {
        SessionCallback<String> sessionCallback = new SessionCallback<String>(){

            @Override
            public String execute(RedisOperations operations) throws DataAccessException {
                //指定抢购成功人的key
                String user=proId+":userId";
                //指定商品的key
                String pro=proId+":proId";
                //判断抢购是否开始
                Integer commodity = (Integer) operations.opsForValue().get(pro);
                //监视
                operations.watch(pro);

                if (commodity==null){
                    return "抢购还未开始";
                }
                //判断是否是第一次抢购
                if (Boolean.TRUE.equals(operations.opsForSet().isMember(user, uId))){
                    log.info("已经秒杀，不能重复秒杀..");
                    return "已经秒杀，不能重复秒杀..";
                }
                //商品抢购完
                if (commodity <= 0){
                    log.info("抢购已经结束，下次在来！");
                    return "抢购已经结束，下次在来！";
                }
                    //队列
                    operations.multi();
                    //抢购成功
                    operations.opsForSet().add(user,uId);
                    operations.opsForValue().decrement(pro);
                    List exec = operations.exec();
                if(exec.size() == 0){
                    log.info("抢购失败");
                    return "抢购失败";
                }

                log.info("抢购成功！");
                return "抢购成功！";
            }
        };
        return redisTemplate.execute(sessionCallback);



    }
}
