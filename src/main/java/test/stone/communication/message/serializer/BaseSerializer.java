package test.stone.communication.message.serializer;

import test.stone.communication.message.SimpleMessage;

public interface BaseSerializer {
    /**反序列化成对象*/
    SimpleMessage deserialize(byte[] bytes);

    /**序列化*/
    byte[] serialize(SimpleMessage simpleMessage);

    /**
     * 创建协议消息对象
     */
    SimpleMessage newInstance();
}
