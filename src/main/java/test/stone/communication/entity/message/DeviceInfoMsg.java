package test.stone.communication.entity.message;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import test.stone.communication.message.TestMessage;

public class DeviceInfoMsg extends TestMessage {
    public DeviceInfoMsg(byte msgType, short len) {
        super(msgType, len);
    }

    @Getter
    @Setter
    private String obuMac;

    @Getter
    @Setter
    private String obuIp;

    @Getter
    @Setter
    private short port;

    public DeviceInfoMsg(){
        super(DEVICE_INFO);
    }
}
