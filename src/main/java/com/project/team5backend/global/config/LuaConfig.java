package com.project.team5backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

@Configuration
public class LuaConfig {

    @Bean(name = "lockAcquireScript")
    public DefaultRedisScript<List> lockAcquireScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/lockAcquire.lua"));
        script.setResultType(List.class);
        return script;
    }

    @Bean(name = "lockReleaseScript")
    public DefaultRedisScript<List> lockReleaseScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/lockRelease.lua"));
        script.setResultType(List.class);
        return script;
    }

    @Bean(name = "lockRenewScript")
    public DefaultRedisScript<List> lockRenewScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("scripts/lockRenew.lua"));
        script.setResultType(List.class);
        return script;
    }

}
