package test.stone.communication.entity.message;

import lombok.Getter;
import lombok.Setter;
import test.stone.communication.message.TestMessage;

public class CustomizeMsgInfo extends TestMessage {
    public CustomizeMsgInfo(byte msgType, short len) {
        super(msgType, len);
    }

    @Getter
    @Setter
    private String customizeInfo;

    public CustomizeMsgInfo(){
        super(MSG_PUSH);
    }
}
