package com.xutao.itrip.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.xutao.itrip.utils.RedisAPI;

public class RedisTester {
	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisAPI redisAPI = (RedisAPI) context.getBean("redisAPI");
		redisAPI.set("test", 300, "redis");
	}

}
