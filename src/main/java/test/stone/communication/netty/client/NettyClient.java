package test.stone.communication.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import test.stone.communication.dao.SysConfigMapper;
import test.stone.communication.entity.SysConfig;
import test.stone.communication.util.SpringUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {

    /**
     * 主机
     */
    private static String host;

    /**
     * 端口号
     */
    private static int port;

    private static ObuClientHandler handler;

    private static SysConfigMapper sysConfigMapper;

    static{
        handler = SpringUtil.getBean(ObuClientHandler.class);
        sysConfigMapper = SpringUtil.getBean(SysConfigMapper.class);
        List<SysConfig> list = sysConfigMapper.getList();
        for(int i=0;i<list.size();i++){
            SysConfig sysConfig = list.get(i);

            if("server_ip".equals(sysConfig.getName())){
                host = sysConfig.getValue();
            }else if("server_port".equals(sysConfig.getName())){
                port = Integer.parseInt(sysConfig.getValue());
            }
        }
    }

    private final static int readerIdleTime = 60;// 读操作空闲秒
    private final static int writerIdleTime = 0;// 写操作空闲
    private final static int allIdleTime = 0; // 读写全部空闲

    /**
     * 构造函数
     *
     */
    public NettyClient() {
    }

    /**
     * 连接方法
     */
    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO)).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime,TimeUnit.SECONDS));
                    p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8, 2, 0, 0));
                    p.addLast(handler);
                }
            });

            Channel channel = bootstrap.connect(host, port).sync().channel();

            System.out.println("连接建立了吗？结果：" + channel.isActive());
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
            try {
                TimeUnit.SECONDS.sleep(5);
                connect(); // 断线重连
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
