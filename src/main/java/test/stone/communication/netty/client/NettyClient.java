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
import test.stone.communication.util.SpringUtil;

import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyClient {

    /**
     * 主机
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    private static ObuClientHandler handler;

    static{
        handler = SpringUtil.getBean(ObuClientHandler.class);
    }

    private final static int readerIdleTime = 60;// 读操作空闲秒
    private final static int writerIdleTime = 0;// 写操作空闲
    private final static int allIdleTime = 0; // 读写全部空闲

    /**
     * 构造函数
     *
     * @param host host
     * @param port port
     */
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 连接方法
     */
    public void connect() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).channel(NioSocketChannel.class);
           // bootstrap.option(ChannelOption.TCP_NODELAY, true);
            // bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(new LoggingHandler(LogLevel.INFO)).handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();
                    p.addLast(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime,TimeUnit.SECONDS));
                    //
                    p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8, 2, 0, 0));
                    //p.addLast(new StringDecoder());
                    p.addLast(handler);
                }
            });

            Channel channel = bootstrap.connect(host, port).sync().channel();
            //channel.writeAndFlush(msg);


            System.out.println("连接建立了吗？结果：" + channel.isActive());
            channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();

            try {
                TimeUnit.SECONDS.sleep(5000);

                connect(); // 断线重连

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
