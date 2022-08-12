package com.mango;

import redis.clients.jedis.Jedis;

import java.util.Random;

public class VerificationCode {
    public static void main(String[] args) {
        String verifyCode = verifyCode();
        redisCode("120",verifyCode);
        check("120",verifyCode);


    }
    //随机6位验证码
    public static String verifyCode(){
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i=0;i<6;i++){
            int num = random.nextInt(10);
            code.append(num);
        }
        return code.toString();
    }
    //验证码放到redis，设置两分钟有效
    public static void redisCode(String phone,String verifyCode){
        //链接redis
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        //验证码的key
        String codeKey =phone+":code";
        //计数的key
        String countKey =phone+":count";
        String count = jedis.get(countKey);
        //每天只能发送三次验证码
        if (count==null){
            //第一次发送设置值
            jedis.setex(countKey,24*60*60,"1");
        }else if (Integer.parseInt(count)<=2){
            jedis.incr(countKey);
        }else {
            System.out.println("今日发送次数已达上线！");
            jedis.close();
            return;
        }
        //保存验证码
        jedis.setex(codeKey,120,verifyCode);
        jedis.close();
    }
    //验证码校验
    public static void check(String phone,String verifyCode){
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        String codeKey =phone+":code";
        String code = jedis.get(codeKey);
        if (verifyCode.equals(code)){
            System.out.println("校验成功");
        }else {
            System.out.println("校验失败");
        }
        jedis.close();
    }
}
