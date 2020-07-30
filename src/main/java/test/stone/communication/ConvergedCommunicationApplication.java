package test.stone.communication;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import test.stone.communication.netty.client.NettyClient;

@SpringBootApplication
@MapperScan(basePackages = "test.stone.communication.dao")
public class ConvergedCommunicationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConvergedCommunicationApplication.class, args);
        NettyClient nettyClient = new NettyClient();
        nettyClient.connect();
    }
}
