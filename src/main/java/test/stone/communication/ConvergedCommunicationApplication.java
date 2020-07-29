package test.stone.communication;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import test.stone.communication.netty.ObuServerHandler;
import test.stone.communication.netty.client.NettyClient;

@SpringBootApplication
@Import({ObuServerHandler.class})
@MapperScan(basePackages = "test.stone.communication.dao")
public class ConvergedCommunicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvergedCommunicationApplication.class, args);
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect();
    }
}
