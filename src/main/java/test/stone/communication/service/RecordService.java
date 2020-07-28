package test.stone.communication.service;

import test.stone.communication.entity.ControlInfo;
import test.stone.communication.entity.DelayDetail;
import test.stone.communication.entity.LinkStatus;
import test.stone.communication.entity.message.SwitchMsg;

public interface RecordService {

    void recordSwitchLinkStatus(ControlInfo controlInfo);

    void recordLinkDelay(DelayDetail delayDetail);
}
