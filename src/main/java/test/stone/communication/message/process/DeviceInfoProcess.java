package test.stone.communication.message.process;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.stone.communication.dao.DeviceInfoMapper;
import test.stone.communication.defines.RedisKeyIds;
import test.stone.communication.entity.DeviceInfo;
import test.stone.communication.entity.message.DeviceInfoMsg;
import test.stone.communication.message.SimpleMessage;
import test.stone.communication.util.DataTypeConvertUtils;
import test.stone.communication.util.IdentityUtils;

import java.util.Date;

@Component
@Slf4j
public class DeviceInfoProcess extends AbstractProcess{

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

    @Override
    public void process(SimpleMessage simpleMessage) {
        DeviceInfoMsg deviceInfoMsg = (DeviceInfoMsg) simpleMessage;

        DeviceInfo build = DeviceInfo.builder().deviceCode(deviceInfoMsg.getObuMac())
                .devicePort((int)deviceInfoMsg.getPort())
                .deviceIp(deviceInfoMsg.getObuIp()).gmtCreate(new Date()).build();


        if(!redisTemplate.hasKey(RedisKeyIds.DEVICE_INFO_PREFIX + build.getDeviceCode())){
            deviceInfoMapper.insertSelective(build);
        }
        redisTemplate.opsForValue().set(RedisKeyIds.DEVICE_INFO_PREFIX + build.getDeviceCode(),build);
        //推送到前端頁面
        JSONObject result = new JSONObject();
        result.put("msgType", 26);
        result.put("deviceInfo", build);
        try{
            webSocketServer.sendMessage(result.toJSONString());
        }catch(Exception ex){
            log.info(ex.getLocalizedMessage());
        }

    }
}
