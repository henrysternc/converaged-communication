/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package test.stone.communication.netty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.netty.handler.codec.string.StringDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import test.stone.communication.config.ObuCache;
import test.stone.communication.util.HexUtils;

/**
 * Modification of {@link} which utilizes Java object serialization.
 * 
 * 
 */
@Slf4j
@Component("obuServer")
@Configuration
public class ObuServer implements Runnable {
	@Value("${rsu.server.port}")
	private int port = 49160;

	@Autowired
	private ObuServerHandler handler = null;

	/** 用于分配处理业务线程的线程组个数 */
	protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2; // 默认
	/** 业务出现线程大小 */
	protected static final int BIZTHREADSIZE = 4;
	/*
	 * NioEventLoopGroup实际上就是个线程池,
	 * NioEventLoopGroup在后台启动了n个NioEventLoop来处理Channel事件,
	 * 每一个NioEventLoop负责处理m个Channel,
	 * NioEventLoopGroup从NioEventLoop数组里挨个取出NioEventLoop来处理Channel
	 */
	private static final EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
	private static final EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);
	// static final boolean SSL = System.getProperty("ssl") != null;
	// static final int PORT = Integer.parseInt(System.getProperty("port",
	// "8007"));
	private final static int readerIdleTime = 60;// 读操作空闲秒
	private final static int writerIdleTime = 0;// 写操作空闲
	private final static int allIdleTime = 0; // 读写全部空闲

	public static List<ObuServerListener> listeners;

	private static Map<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();
	

	public ObuServer() {
		// this.port = port;
		listeners = new ArrayList<ObuServerListener>();
		//handler = new ObuServerHandler();

		Thread thread = new Thread(this);
		thread.start();

	}

	// 功能码 设备标识 占位符 消息长度 消息内容（PDU）
	// 2字节 6字节 1字节 2字节 N字节
	@Override
	public void run() {
		try {

			ServerBootstrap b = new ServerBootstrap();

			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							p.addLast(new IdleStateHandler(readerIdleTime, writerIdleTime, allIdleTime,TimeUnit.SECONDS));
							//
							p.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 8, 2, 0, 0));
							//p.addLast(new StringDecoder());
							p.addLast(handler);
						}
					});

			// b.option(ChannelOption.TCP_NODELAY, true);
			// b.option(ChannelOption.SO_KEEPALIVE, true);
			b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

			// 例句
			// f = b. bind(port).sync().channel().closeFuture().sync();
			ChannelFuture channelFuture = b.bind(port).sync();
			log.info(String.format("## server bind ...port= %d  ##", port));

			// Wait until the connection is closed.
			// channelFuture.channel().closeFuture().sync();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// bossGroup.shutdownGracefully();
			// workerGroup.shutdownGracefully();
		}
	}

	/**
	 * 发送给全部已连接设备
	 * 
	 * @param bytes
	 */
	public void publish(byte[] bytes) {
		try {
			if (channelMap.isEmpty()) {
				System.out.println("server 无连接channel！");
				return;
			}

			ByteBuf byteBuf = Unpooled.buffer(bytes.length);
			byteBuf.writeBytes(bytes);

			Iterator<String> it = channelMap.keySet().iterator();
			while (it.hasNext()) {
				Channel ch = (Channel) channelMap.get(it.next());
				if (ch != null && ch.isActive()) {
					ch.writeAndFlush(byteBuf);
					// 通道不能关闭
					// ch.close();
				} else {
					System.out.println("channel =null");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("server 发送数据flash完成！");
	}

	/**
	 * 
	 * 发送给指定obu设备,由RSU转发
	 * <p>
	 * 可发送之前检查是否已连接，重复检查？
	 * 
	 * <p>
	 * 
	 * @param deviceCode 
	 * @param bytes
	 */
	public boolean sendToObu(String deviceCode, byte[] bytes) {
		boolean sendFinished = false;
		if(StringUtil.isNullOrEmpty(deviceCode)) {
//			log.error("deviceCode = null");
			return false;
		}
			
		try {
			String tmpStr = HexUtils.byte2String(bytes);

			Channel ch = ObuCache.get(deviceCode);
			if (ch != null && ch.isActive()) {
				ByteBuf byteBuf = Unpooled.buffer(bytes.length);
				byteBuf.writeBytes(bytes);

				ch.writeAndFlush(byteBuf);
				sendFinished = true;
				
				// 通道不能关闭				
				 log.info(String.format("发送[%d] ：%s", bytes.length, tmpStr));
			} else {
//				log.error(String.format("中止发送%s，%s 离线", tmpStr,deviceCode));
//				log.error(String.format("中止发送，%s 离线",deviceCode));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("server 发送数据flash完成！");
		return sendFinished;
	}

	

	/**
	 * 
	 * 发送给指定RSU
	 * 
	 * @param ip rsu的IP地址
	 * @param bytes
	 */
	public void sendToRsu(String ip, byte[] bytes) {
		try {
			if (channelMap.isEmpty()) {
				System.out.println("没有连接的设备！");
				return;
			}
			Channel ch = channelMap.get(ip); 
			if (ch != null && ch.isActive()) {
				ByteBuf byteBuf = Unpooled.buffer(bytes.length);
				byteBuf.writeBytes(bytes);

				ch.writeAndFlush(byteBuf);
				// 通道不能关闭
				 String tmpStr = HexUtils.byte2String(bytes);
				 log.info(String.format("发送[%d] ：%s", bytes.length, tmpStr));
			} else {
				System.out.println(String.format("发送失败：%s 未连接",ip));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// System.out.println("server 发送数据flash完成！");
	}

	
	/**
	 * 检查车辆（obu）是否为连接状态
	 * @param deviceCode
	 * @return
	 */
	public boolean isObuActive(String deviceCode) {
		Channel ch = ObuCache.get(deviceCode);
		if (ch == null) {
			return false;
		}
		else {
			if (ch.isActive())
				return true;
			else
				return false;
		}
	}
	
	
	
	
	public void close() {
		try {
			unregisterMessageListeners();
			bossGroup.shutdownGracefully();

			workerGroup.shutdownGracefully();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		log.info("##server主动断开 ##");
	}

	public void registerMessageListener(ObuServerListener listener) {
		if (listener == null)
			log.error("invalid listener registered");
		else
			listeners.add(listener);
		log.info("server注册监听者");
	}

	public void unregisterMessageListener(ObuServerListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
			log.info("server注销监听者");
		}
	}

	public void unregisterMessageListeners() {
		if (listeners.isEmpty())
			return;
		listeners.clear();
		// listeners = new ArrayList();
		log.info("server注销全部监听者");
	}

	public static Map<String, Channel> getChannelMap() {
		return channelMap;
	}

	public static void setChannelMap(Map<String, Channel> channelMap) {
		ObuServer.channelMap = channelMap;
	}

	public ObuServerHandler getHandler() {
		return handler;
	}

	public void setHandler(ObuServerHandler handler) {
		this.handler = handler;
	}
}
