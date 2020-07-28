package test.stone.communication.config;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import test.stone.communication.netty.ObuServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObuCache implements ApplicationRunner {

    @Getter
    @Setter
    private Map<String, Object> obuMap = new ConcurrentHashMap<>();


    @Override
    public void run(ApplicationArguments args) throws Exception {

    }

    public static Channel get(String deviceCode){

        return ObuServer.getChannelMap().get(deviceCode);
    }
}
