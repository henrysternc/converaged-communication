package test.stone.communication.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.stone.communication.dao.ControlInfoMapper;
import test.stone.communication.dao.DelayDetailMapper;
import test.stone.communication.entity.ControlInfo;
import test.stone.communication.entity.DelayDetail;
import test.stone.communication.service.RecordService;

@Service
public class RecordServiceImpl implements RecordService {

    @Autowired
    DelayDetailMapper delayDetailMapper;

    @Autowired
    ControlInfoMapper controlInfoMapper;

    @Override
    public void recordSwitchLinkStatus(ControlInfo controlInfo) {
        controlInfoMapper.insertSelective(controlInfo);
    }

    @Override
    public void recordLinkDelay(DelayDetail delayDetail) {
        delayDetailMapper.insertSelective(delayDetail);
    }
}
