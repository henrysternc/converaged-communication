package test.stone.communication.message.serializer;

import test.stone.communication.message.MessageCode;
import test.stone.communication.message.SimpleMessage;
import test.stone.communication.message.TestMessage;
import test.stone.communication.util.ClassUtils;
import test.stone.communication.util.HexUtils;

import java.nio.ByteBuffer;


public abstract class SimpleSerializer implements BaseSerializer, MessageCode {

    public final static int BUFFER_SIZE = 1024 * 2;
    public final static String CHARSET_NAME = "UTF-8";

    /**
     * 反序列化
     * 封装对象
     * 取字节流数据，set消息头
     */
    protected int deserializeHeader(TestMessage msg, byte[] bytes) throws Exception {
        TestMessage.Header header = msg.getHeader();

        ByteBuffer bb = ByteBuffer.wrap(bytes, 0, SimpleMessage.HEADER_LENGTH);
        header.setMsgType(bb.get());

        byte[] device_identity = new byte[SimpleMessage.DEVICE_CODE_LENGTH];
        bb.get(device_identity);

        header.setDeviceCode(device_identity);
        header.setPlaceHolder();
        header.setMsgLength(bb.getShort());

        return SimpleMessage.HEADER_LENGTH;//  返回 10,之后是消息内容
    }

    /**
     * 取消息header，填充字节流
     * 注意：length长度要先计算好,
     */
    protected int serializeHeader(TestMessage msg, byte bytes[]) throws Exception {
        int idx = 0;
        TestMessage.Header header = msg.getHeader();
        bytes[idx++] = header.getMsgType();

        byte[] device_identity = header.getDeviceCode();
        if(device_identity.length != SimpleMessage.DEVICE_CODE_LENGTH) {
            return -1;
        }

        for (int i = 0; i < SimpleMessage.DEVICE_CODE_LENGTH; i++) {
            bytes[idx++] = device_identity[i];
        }

        header.setPlaceHolder();
        //先计算协议长度
        header.setMsgLength((short) (ClassUtils.getFieldLength(msg)));

        byte[] tmp = HexUtils.shortToByteArray(header.getMsgLength());
        bytes[idx++] = tmp[0];
        bytes[idx++] = tmp[1];
        bytes[idx++] = 0x00;
        return idx;
    }

    protected boolean checkBytes(byte[] bytes){
        if (bytes == null || bytes.length < SimpleMessage.HEADER_LENGTH) {
            return false;
        }
        return true;
    }

    protected boolean checkSimpleMessage(SimpleMessage simpleMessage){
        if(null == simpleMessage){
            return false;
        }
        return true;
    }

    protected byte[] trim(byte a[], int len) {
        int minLen = Math.min(a.length, len);
        byte ret[] = new byte[minLen];
        for (int i = 0; i < minLen; i++)
            ret[i] = a[i];

        return ret;
    }
}
