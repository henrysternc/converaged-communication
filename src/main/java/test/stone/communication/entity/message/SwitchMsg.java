package test.stone.communication.entity.message;

import lombok.Getter;
import lombok.Setter;
import test.stone.communication.message.TestMessage;

public class SwitchMsg extends TestMessage {

    public SwitchMsg(byte msgType, short len) {
        super(msgType, len);
    }

    @Getter
    @Setter
    private byte linkId;

    @Getter
    @Setter
    private byte linkSwitchStatus;

    public SwitchMsg(){
        super(SWITCH_CONTROL);
    }
}
