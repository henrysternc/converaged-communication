package test.stone.communication.message.process;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import test.stone.communication.dao.DelayDetailMapper;
import test.stone.communication.defines.RedisKeyIds;
import test.stone.communication.defines.WebSocketDefines;
import test.stone.communication.entity.DelayDetail;
import test.stone.communication.entity.message.DelayTimeMsg;
import test.stone.communication.message.SimpleMessage;
import test.stone.communication.websocket.WebSocketServer;

import java.math.BigDecimal;
import java.util.Date;

@Component
@Slf4j
public class DelayTimeProcess extends AbstractProcess implements WebSocketDefines {

    @Autowired
    private DelayDetailMapper delayDetailMapper;

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    public WebSocketServer webSocketServer;

    @Override
    public void process(SimpleMessage simpleMessage) {
        DelayTimeMsg delayTimeMsg = (DelayTimeMsg) simpleMessage;

        DelayDetail build = DelayDetail.builder().linkType((int) delayTimeMsg.getLinkId())
                .delayTime((int) delayTimeMsg.getTransDelay())
                .gmtCreate(new Date()).build();

        delayDetailMapper.insertSelective(build);

        //更新值
        calculateDelay(build);
    }

    private void calculateDelay(DelayDetail delayDetail){

        updateRedisByTypeAndValue(delayDetail.getLinkType(), delayDetail.getDelayTime());
    }

    private void updateRedisByTypeAndValue(Integer type, Integer value){
        String totalKey = null;
        String avgKey = null;
        String countKey = null;
        if(1 == type){//Mesh
            totalKey = RedisKeyIds.TOTAL_MESH_DELAY_TIME_PREFIX;
            avgKey = RedisKeyIds.AVG_MESH_DELAY_TIME_PREFIX;
            countKey = RedisKeyIds.COUNT_MESH;
        }else if(2 == type){//LTE-R
            totalKey = RedisKeyIds.TOTAL_LTE_R_DELAY_TIME_PREFIX;
            avgKey = RedisKeyIds.AVG_LTE_R_DELAY_TIME_PREFIX;
            countKey = RedisKeyIds.COUNT_LTE_R;
        }else if(3 == type){//4g
            totalKey = RedisKeyIds.TOTAL_4G_DELAY_TIME_PREFIX;
            avgKey = RedisKeyIds.AVG_4G_DELAY_TIME_PREFIX;
            countKey = RedisKeyIds.COUNT_4G;
        }else{
            totalKey = RedisKeyIds.TOTAL_SAT_DELAY_TIME_PREFIX;
            avgKey = RedisKeyIds.AVG_SAT_DELAY_TIME_PREFIX;
            countKey = RedisKeyIds.COUNT_SAT;
        }

        updateRedisValue(totalKey, avgKey, countKey, value, type);
    }

    private void updateRedisValue(String totalKey, String avgKey, String countKey, Integer value, Integer type){

        JSONObject result = new JSONObject();
        result.put("msgType", 27);
        result.put("type", type);
        //更新总时延
        Object o = redisTemplate.opsForValue().get(totalKey);
        BigDecimal bigDecimal = new BigDecimal(o.toString());
        //total+=value;
        BigDecimal add = bigDecimal.add(new BigDecimal(value.toString()));
        redisTemplate.opsForValue().set(RedisKeyIds.TOTAL_MESH_DELAY_TIME_PREFIX, add);
        result.put("totalDelay", add);
        //更新数量
        Long increment = redisTemplate.opsForValue().increment(countKey);
        result.put("count", increment);
        //更新平均时延
        BigDecimal divide = add
                .divide(new BigDecimal(String.valueOf(increment)),
                        2, BigDecimal.ROUND_HALF_UP);
        result.put("avgDelay", divide);
        result.put("currentDelay", value);
        redisTemplate.opsForValue().set(avgKey, divide);
        try{
            webSocketServer.sendMessage(result.toJSONString());
        }catch (Exception ex){
            log.info(ex.getLocalizedMessage());
        }
    }
}
