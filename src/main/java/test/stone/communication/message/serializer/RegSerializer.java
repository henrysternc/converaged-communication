package test.stone.communication.message.serializer;

import lombok.extern.slf4j.Slf4j;
import test.stone.communication.message.MessageCode;
import test.stone.communication.message.SimpleMessage;

import java.util.HashMap;

@Slf4j
public class RegSerializer {

    private static final String FC = "FC";

    private static HashMap<String, SimpleSerializer> serializerMaps = new HashMap<>();

    static{
        serializerMaps.put(FC + String.valueOf(MessageCode.SWITCH_CONTROL), new SwitchSerializer());
        serializerMaps.put(FC + String.valueOf(MessageCode.MSG_PUSH), new CustomizeSerializer());
        serializerMaps.put(FC + String.valueOf(MessageCode.LINK_STATUS), new LinkStatusSerializer());
        serializerMaps.put(FC + String.valueOf(MessageCode.DEVICE_INFO), new DeviceInfoSerializer());
        serializerMaps.put(FC + String.valueOf(MessageCode.DELAY_TIME), new DelayTimeSerializer());
    }

    public RegSerializer(){
    }

    /**
     * 反序列化
     */
    public static SimpleMessage deserialize(byte[] bytes) throws Exception {
        if (bytes.length < SimpleMessage.HEADER_LENGTH) {
            return null;
        }

        byte fc_code = bytes[SimpleMessage.MSG_TYPE_IDX];
        SimpleSerializer serializer = serializerMaps.get(FC + String.valueOf(fc_code));
        if (serializer == null) {
            log.info("非法类型消息：{}", fc_code);
            return null;
        } else {
            return serializer.deserialize(bytes);
        }
    }

    /**
     * 序列化
     */
    public static byte[] serialize(SimpleMessage msg) throws Exception {
        if (msg == null)
            return null;
        byte fc_code = msg.getHeader().getMsgType();
        SimpleSerializer serializer = serializerMaps.get(FC + String.valueOf(fc_code));
        if (serializer == null) {
            log.info("非法类型消息：{}", fc_code);
            return null;
        } else {
            return serializer.serialize(msg);
        }
    }
}
