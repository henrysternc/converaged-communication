/**
 * 
 */
package test.stone.communication.util;

import java.nio.ByteBuffer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import test.stone.communication.message.MessageCode;
import test.stone.communication.message.SimpleMessage;

/**
 * 反序列化mac地址
 * 
 * @author
 *
 */
@Slf4j
public class IdentityUtils {

	/**
	 * 根据字节生成MAC
	 * 
	 * @param bytes
	 * @return
	 */
	public static String makeIdentityString(byte[] bytes) {
		if (bytes == null || bytes.length < SimpleMessage.DEVICE_CODE_LENGTH){
			return null;
		}
		ByteBuffer bb = ByteBuffer.wrap(bytes, 0, bytes.length - 0);

		return String.format("%02X:%02X:%02X:%02X:%02X:%02X", bb.get(), bb.get(), bb.get(), bb.get(), bb.get(), bb.get());
	}

	/**
	 * 设备字符串编码转字节数组
	 * 
	 * @param deviceCode
	 * @return
	 */
	public static byte[] makeIdentityByte(String deviceCode) {
		if(deviceCode==null){
			return null;
		}
		String[] id = deviceCode.split(":");
		if (id.length < SimpleMessage.DEVICE_CODE_LENGTH) {
			return null;
		}

		byte[] device_identity = new byte[SimpleMessage.DEVICE_CODE_LENGTH];
		for (int i = 0; i < 6; i++) {
			device_identity[i] = Integer.decode("0x" + id[i]).byteValue();
		}

		return device_identity;
	}

	public static String bytes2IpV4(byte[] bytes){
		if(bytes.length == SimpleMessage.IP_LENGTH){
			ByteBuffer bb = ByteBuffer.wrap(bytes, 0, bytes.length - 0);
			return String.format("%d.%d.%d.%d", bb.getShort(), bb.getShort(), bb.getShort(), bb.getShort());
		}
		return null;
	}

	public static byte[] ipV42Bytes(String obuIp){
		if(!StringUtils.isEmpty(obuIp)){
			byte[] ret = new byte[SimpleMessage.IP_LENGTH];
			String[] split = obuIp.split(".");
			int retIdx = 0;
			for(int i=0;i<split.length;i++){
				int i1 = Integer.parseInt(split[i]);
				byte[] bytes = DataTypeConvertUtils.int2Bytes(i1);
				if(bytes.length == 2){
					ret[retIdx] = bytes[0];
					ret[++retIdx] = bytes[1];
				}else{
					ret[retIdx] = bytes[0];
				}
				retIdx++;
			}

			return ret;
		}

		return null;
	}
}
