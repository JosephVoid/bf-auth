package com.buyersfirst.auth.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import redis.clients.jedis.*;

@Component
public class RedisCacheService {
    @Value("${redis.host}")
    private String redisHost;
    @Value("${redis.port}")
    private Integer redisPort;
    @Value("${redis.pass}")
    private String redisPass;
    @Value("${redis.user}")
    private String redisUser;
    private HostAndPort address;
    private JedisClientConfig config;
    public JedisPooled jedis;

    RedisCacheService() {
    }

    @PostConstruct
    void init() {
        System.out.println(redisHost + "|" + redisPort + "|" + redisPass + "|" + redisUser);
        this.address = new HostAndPort(this.redisHost, this.redisPort);
        this.config = DefaultJedisClientConfig.builder()
                .user(this.redisUser)
                .password(this.redisPass)
                .build();
        this.jedis = new JedisPooled(this.address, this.config);
    }
}
