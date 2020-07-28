package test.stone.communication.entity.message;

import lombok.Getter;
import lombok.Setter;
import test.stone.communication.message.TestMessage;

public class LinkStatusMsg extends TestMessage {
    public LinkStatusMsg(byte msgType, short len) {
        super(msgType, len);
    }

    @Getter
    @Setter
    private byte linkMeshStatus;

    @Getter
    @Setter
    private byte link4gStatus;

    @Getter
    @Setter
    private byte linkLteRStatus;

    @Getter
    @Setter
    private byte linkSatStatus;

    public LinkStatusMsg(){
        super(LINK_STATUS);
    }
}
