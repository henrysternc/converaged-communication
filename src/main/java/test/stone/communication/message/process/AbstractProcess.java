package test.stone.communication.message.process;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import test.stone.communication.websocket.WebSocketServer;

public abstract class AbstractProcess implements IProcessMessage {

    @Autowired
    public RedisTemplate redisTemplate;

    @Autowired
    public WebSocketServer webSocketServer;
}
