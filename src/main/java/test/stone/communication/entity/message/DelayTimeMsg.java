package test.stone.communication.entity.message;

import lombok.Getter;
import lombok.Setter;
import test.stone.communication.message.TestMessage;

public class DelayTimeMsg extends TestMessage {
    public DelayTimeMsg(byte msgType, short len) {
        super(msgType, len);
    }

    @Getter
    @Setter
    private byte linkId;

    @Getter
    @Setter
    private long transDelay;

    public DelayTimeMsg(){
        super(DELAY_TIME);
    }
}
