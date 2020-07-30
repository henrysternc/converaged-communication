package test.stone.communication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import test.stone.communication.defines.RedisKeyIds;

import java.util.Set;

@Component
public class InitData implements ApplicationRunner {

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Set<String> keys = redisTemplate.keys("*");
        if(keys != null && !keys.isEmpty()){
            redisTemplate.delete(keys);
        }

        //重新初始化数据

        redisTemplate.opsForValue().set(RedisKeyIds.TOTAL_MESH_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.TOTAL_LTE_R_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.TOTAL_SAT_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.TOTAL_4G_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.AVG_MESH_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.AVG_LTE_R_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.AVG_4G_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.AVG_SAT_DELAY_TIME_PREFIX, 0);

        redisTemplate.opsForValue().set(RedisKeyIds.totalSwitch, 0);

        redisTemplate.opsForValue().set(RedisKeyIds.sendSwitch, 0);
    }
}
