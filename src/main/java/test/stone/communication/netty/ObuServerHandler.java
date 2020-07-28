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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import test.stone.communication.defines.RedisKeyIds;
import test.stone.communication.message.MessageCode;
import test.stone.communication.message.SimpleMessage;
import test.stone.communication.message.process.DelayTimeProcess;
import test.stone.communication.message.process.DeviceInfoProcess;
import test.stone.communication.message.process.LinkStatusProcess;
import test.stone.communication.message.serializer.RegSerializer;
import test.stone.communication.util.HexUtils;

/**
 * Sharable表示此对象在channel间共享 handler类是我们的具体业务类
 */

@Component
@Sharable
@Slf4j
public class ObuServerHandler extends ChannelInboundHandlerAdapter implements MessageCode {
//	@Autowired
//	private KafkaTemplate kafkaTemplate;

	@Autowired
	private ObuHelper obuHelper;
	//
	// @Autowired
	// private HttpUtil httpUtil;

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private DelayTimeProcess delayTimeProcess;

	@Autowired
	private DeviceInfoProcess deviceInfoProcess;

	@Autowired
	private LinkStatusProcess linkStatusProcess;

	public ObuServerHandler() {
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		// ctx.writeAndFlush(byteBuf);
		// 测试，发送直接使用字节数组或者字符串都不能发出去，用ByteBuf可以。
		// ctx.writeAndFlush("wtert");
		// byte mybytes[] = FileTool.fileToByteArray(new File("f:/xu6.txt"));
		// ByteBuf byteBuf =Unpooled.buffer(mybytes.length);
		// byteBuf.writeBytes(mybytes);
		// ctx.writeAndFlush(byteBuf);
		// ctx.close();
		// log.info(new StringBuilder().append("## ").append(getIPString(ctx)).append("
		// ObuServer channelActive ## ")
		// .toString());

		// 最好的是保存RSU设备编码，经过路由的话则IP地址可能不可靠
		// 现协议尚未涉及故还是使用IP地址

		String ip = getIPString(ctx);
		ObuServer.getChannelMap().put(ip, ctx.channel());
		// httpUtil.insertSysLog(ip, LogConsts.LOG_TYPE_RSU, ONLINE);
		log.info(String.format("%s 连接", ip));
		
		//sendRsuStateMessage(ip,1);
		
		// 立即请求已经连接了多少设备
		//obuHelper.requestChildren(ctx.channel());

	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
		// System.out.println("channelReadComplete");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.error("ObuServer异常-" + cause.getMessage());
		String ip = getIPString(ctx);
		ObuServer.getChannelMap().remove(ip);
		
		ctx.close();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		super.channelInactive(ctx);

		// 删除Channel Map中的失效Client
		String ip = getIPString(ctx);
		ObuServer.getChannelMap().remove(ip);
		ctx.close();

		// httpUtil.insertSysLog(ip, LogConsts.LOG_TYPE_RSU, OFFLINE);
		log.info(String.format("%s 断开", ip));
		sendRsuStateMessage(ip,0);

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		log.info("i am coming~~~");
		byte[] bytes = null;
		ByteBuf m = (ByteBuf) msg;

		if (m.readableBytes() > 0) {
			bytes = new byte[m.readableBytes()];
			m.readBytes(bytes);

			 String tmpStr = HexUtils.byte2String(bytes);
			 log.info(String.format("收到[%d] ：%s", bytes.length, tmpStr));

			SimpleMessage simpleMessage = deserialize(bytes);

			//doKfkWrok(simpleMessage);
			processMessage(simpleMessage);
			try {
				//obuHelper.handle(simpleMessage, ctx.channel());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// 释放
		m.release();

	}


	private void processMessage(SimpleMessage simpleMessage){
		byte msgType = simpleMessage.getHeader().getMsgType();
		switch (msgType){
			case LINK_STATUS:
				linkStatusProcess.process(simpleMessage);
				break;
			case DEVICE_INFO:
				deviceInfoProcess.process(simpleMessage);
				break;
			case DELAY_TIME:
				Integer totalSwitch = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.totalSwitch);
				if(1 == totalSwitch){//如果计数开关开启（初始化完成之后），才开始记录计算延时数据
					delayTimeProcess.process(simpleMessage);
				}
				break;
			default:
				log.info("错误。。。。。。");
		}
	}

	/**
	 * 解析1条完整的协议，然后分发
	 * 
	 * @param single
	 */
	private SimpleMessage deserialize(final byte[] single) {
		SimpleMessage simpleMessage = null;
		try {

			simpleMessage = RegSerializer.deserialize(single);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return simpleMessage;
	}



	/*
	 * private void dispatch(List<Object> status) { if(status ==null
	 * ||status.isEmpty()) { System.out.println("无状态数据！"); return; }
	 * 
	 * ObuServerListener list[] = (ObuServerListener[])
	 * ObuServer.listeners.toArray(new ObuServerListener[0]); ObuServerListener
	 * arr[] = list; int len = arr.length; for (int i = 0; i < len; i++) {
	 * ObuServerListener listener = arr[i]; try { if (listener != null) {
	 * listener.onStatusMessage(status); } } catch (Exception e) { log.error(
	 * "dispatch encounter an exception", e); } } }
	 */
	private void dispatch(byte[] bytes) {
		ObuServerListener[] list = (ObuServerListener[]) ObuServer.listeners.toArray(new ObuServerListener[0]);
		ObuServerListener[] arr = list;
		for (int i = 0; i < arr.length; i++) {
			ObuServerListener listener = arr[i];
			try {
				if (listener != null) {
					listener.onChipMessage(bytes);
				}
			} catch (Exception e) {
				log.error("dispatch encounter an exception", e);
			}
		}
	}

	private void dispatch(String msg) {
		ObuServerListener[] list = (ObuServerListener[]) ObuServer.listeners.toArray(new ObuServerListener[0]);
		ObuServerListener[] arr = list;
		for (int i = 0; i < arr.length; i++) {
			ObuServerListener listener = arr[i];
			try {
				if (listener != null) {
					listener.onSimpleMessage(msg);
				}
			} catch (Exception e) {
				log.error("dispatch encounter an exception", e);
			}
		}
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) { 
//				 log.error("读超时，主动断开连接！");
//				直接关掉，否则二次触发inactive()事件
//				channelInactive(ctx);
				ctx.channel().close();
			} else if (event.state() == IdleState.WRITER_IDLE) {
//				 log.error("write超时，主动断开连接！");
				// keepAlive(ctx);
//				channelInactive(ctx);
				ctx.channel().close();
			} else if (event.state() == IdleState.ALL_IDLE) {
				System.out.println("空闲 ALL_IDLE");
			}
		}
		else {
			ctx.fireUserEventTriggered(evt);
		}
	}

 
    
    
	private String getIPString(ChannelHandlerContext ctx) {
		String ipString = "";
		String socketString = ctx.channel().remoteAddress().toString();
		int colonAt = socketString.indexOf(":");
		ipString = socketString.substring(1, colonAt);
		return ipString;
	}
	

	private void sendRsuStateMessage(String ip,int state) throws Exception{
//		RsuConnectState simpleMessage = new RsuConnectState();
//		byte[] bytes = new byte[Constants.IP_LENGTH];
//
////		IP地址肯定正常
//		String[] myip= ip.split("\\.");
//		for(int i=0;i<myip.length;i++) {
//			bytes[i] = (byte)Integer.parseInt(myip[i]);
//		}
//		simpleMessage.setIp(bytes);
//		simpleMessage.setState((byte)state);
//
////		发送
//		doKfkWrok(simpleMessage);
	}
	

}
