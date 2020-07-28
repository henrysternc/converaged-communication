package test.stone.communication.netty;



import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import test.stone.communication.message.MessageCode;
import test.stone.communication.message.SimpleMessage;

/**
 * 
 * 
 * @author
 *
 */
@Component
@Slf4j
public class ObuHelper{

//	/**
//	 * 请求已经连接了多少设备
//	 *
//	 * @param ch
//	 */
//	public void requestChildren(Channel chanel) throws Exception {
//		if(chanel==null || !chanel.isActive())
//			return;
//		// 通道不能关闭
//	}

	
	/**
	 * 处理连接状态类消息
	 * 
	 * @param simpleMessage
	 * @param chanel
	 * @throws Exception
	 */
	public void handle(SimpleMessage simpleMessage, Channel chanel) throws Exception {
////		if(httpUtil==null || simpleMessage==null ) {
////			log.warn("===null");
////			return;
////		}
//		if(chanel==null || !chanel.isActive())
//			return;
//
//		short fc = simpleMessage.getHeader().getMsgType();
//
//		switch (fc) {
//		case MessageCode.MESH: {
//			RsuNotify rsuNotify = (RsuNotify) simpleMessage;
//			String key = IdentityUtils.makeIdentityString(rsuNotify.getDeviceId());
//			if (rsuNotify.getAction() == 1) {
//				ObuCache.put(key, chanel);
////				httpUtil.insertSysLog(key, LogConsts.LOG_TYPE_OBU, ONLINE);
//				log.info(String.format("%s 上线",key));
//			} else {
//				ObuCache.remove(key);
////				httpUtil.insertSysLog(key, LogConsts.LOG_TYPE_OBU, OFFLINE);
//				log.info(String.format("%s 离线",key));
//			}
//
//			break;
//		}
//		case MessageCode.RSU_CHILDERN_RESPONSE: {
//			RsuChildrenResponse children = (RsuChildrenResponse) simpleMessage;
//
//			String key = null;
//			for (int i = 0; i < children.getCount(); i++) {
//				byte bytes[] = new byte[SimpleMessage.DEVICE_IDENTITY_SIZE];
//				System.arraycopy(children.getDeviceId(), i * SimpleMessage.DEVICE_IDENTITY_SIZE, bytes, 0,
//						SimpleMessage.DEVICE_IDENTITY_SIZE);
//
//				key = IdentityUtils.makeIdentityString(bytes);
//				ObuCache.put(key, chanel);
//
////				httpUtil.insertSysLog(key, LogConsts.LOG_TYPE_OBU, ONLINE);
//				log.info(String.format("%s 上线",key));
//			}
//			break;
//		}
//		default: {
//			break;
//		}
		//}
	}
}
