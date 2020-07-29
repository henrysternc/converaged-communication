package test.stone.communication.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import test.stone.communication.dao.CustomizeMsgMapper;
import test.stone.communication.dao.StatisticInfoMapper;
import test.stone.communication.defines.RedisKeyIds;
import test.stone.communication.defines.ResponseMsg;
import test.stone.communication.entity.CustomizeMsg;
import test.stone.communication.entity.StatisticInfo;
import test.stone.communication.entity.message.CustomizeMsgInfo;
import test.stone.communication.entity.message.SwitchMsg;
import test.stone.communication.entity.vo.ControlDeviceVO;
import test.stone.communication.message.serializer.RegSerializer;
import test.stone.communication.netty.ObuServer;
import test.stone.communication.service.OperationService;
import test.stone.communication.util.DataTypeConvertUtils;
import test.stone.communication.util.HexUtils;
import test.stone.communication.util.IdentityUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class OperationServiceImpl implements OperationService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StatisticInfoMapper statisticInfoMapper;

    @Autowired
    private CustomizeMsgMapper customizeMsgMapper;

    @Autowired
    private ObuServer obuServer;

    @Override
    public ResponseMsg init() {

        //重置redis中的key
        clearRedisKeys();

        //在statistics_info表中插入一条记录【初始化】
        StatisticInfo build = StatisticInfo.builder().startTime(new Date())
                .lteAvgDelay(BigDecimal.ZERO)
                .lteRAvgDelay(BigDecimal.ZERO)
                .meshAvgDelay(BigDecimal.ZERO)
                .satAvgDelay(BigDecimal.ZERO)
                .lteRTotalDelay(0)
                .lteTotalDelay(0)
                .meshTotalDelay(0)
                .satTotalDelay(0).build();

        statisticInfoMapper.insertSelective(build);
        redisTemplate.opsForValue().set(RedisKeyIds.currentTaskId, build.getId());
        //开启总开关
        redisTemplate.opsForValue().set(RedisKeyIds.totalSwitch, 1);
        return ResponseMsg.success();
    }

    @Override
    public ResponseMsg complete() {
        //关闭计数总开关
        redisTemplate.opsForValue().set(RedisKeyIds.totalSwitch, 0);
        Integer currentId = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.currentTaskId);
        Integer satTotalDelay = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_SAT_DELAY_TIME_PREFIX);
        Integer meshTotalDelay = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_MESH_DELAY_TIME_PREFIX);
        Integer LteRTotalDelay = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_LTE_R_DELAY_TIME_PREFIX);
        Integer LteTotalDelay = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_4G_DELAY_TIME_PREFIX);

        Integer avg4gDelay = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.AVG_4G_DELAY_TIME_PREFIX);
        Integer avgMeshDelay = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.AVG_MESH_DELAY_TIME_PREFIX);
        Integer avgLteRDelay = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.AVG_LTE_R_DELAY_TIME_PREFIX);
        Integer avgSATDelay = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_SAT_DELAY_TIME_PREFIX);

        StatisticInfo build = StatisticInfo.builder().id(currentId)
                .satTotalDelay(satTotalDelay)
                .meshTotalDelay(meshTotalDelay)
                .lteTotalDelay(LteTotalDelay)
                .lteRTotalDelay(LteRTotalDelay)
                .satAvgDelay(new BigDecimal(avg4gDelay.toString()))
                .meshAvgDelay(new BigDecimal(avgLteRDelay.toString()))
                .lteRAvgDelay(new BigDecimal(avgMeshDelay.toString()))
                .lteAvgDelay(new BigDecimal(avgSATDelay.toString()))
                .endTime(new Date())
                .build();
        statisticInfoMapper.updateByPrimaryKeySelective(build);
        return ResponseMsg.success();
    }

    @Override
    public ResponseMsg getDetails(Integer id) {
        return null;
    }

    @Override
    public ResponseMsg getList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<StatisticInfo> list = statisticInfoMapper.getList();
        return ResponseMsg.success(new PageInfo<>(list));
    }

    @Override
    public ResponseMsg changeSwitch(Integer linkType, Integer status) {
        try{
            SwitchMsg switchMsg = new SwitchMsg();
            switchMsg.setLinkId(DataTypeConvertUtils.int2OneByte(linkType));
            switchMsg.setLinkSwitchStatus(DataTypeConvertUtils.int2OneByte(status));
            Object o = redisTemplate.opsForValue().get(RedisKeyIds.CONTROL_MACHINE);
            byte[] deviceCode = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
            switchMsg.getHeader().setDeviceCode(deviceCode);
            byte[] bytes = RegSerializer.serialize(switchMsg);
            obuServer.publish(bytes);
            String s = HexUtils.byte2String(bytes);
            log.info("发送了内容：{}", s);
        }catch (Exception ex){

        }

        return null;
    }

    @Override
    public ResponseMsg sendMsg(String msg) {
        if(msg.getBytes().length > 1024){
            return ResponseMsg.error(500,"消息过长");
        }
        try{
            CustomizeMsgInfo customizeMsgInfo = new CustomizeMsgInfo();
            customizeMsgInfo.setCustomizeInfo(msg);
//            Object o = redisTemplate.opsForValue().get(RedisKeyIds.CONTROL_MACHINE);
//            String deviceCode = null;
//            if(o != null){
//                ControlDeviceVO controlDeviceVO = JSON.toJavaObject((JSON) JSON.toJSON(o), ControlDeviceVO.class);
//                deviceCode = controlDeviceVO.getDeviceCode();
//            }else{
//                deviceCode = "00:00:00:00:00:00";
//            }
            byte[] deviceCode = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
            customizeMsgInfo.getHeader().setDeviceCode(deviceCode);
            byte[] bytes = RegSerializer.serialize(customizeMsgInfo);
            obuServer.publish(bytes);
            String s = HexUtils.byte2String(bytes);
            log.info("发送了内容：{}", s);
            CustomizeMsg customizeMsg = new CustomizeMsg();
            customizeMsg.setGmtCreate(new Date());
            customizeMsg.setMsgContent(msg);
            customizeMsgMapper.insertSelective(customizeMsg);
            return ResponseMsg.success();
        }catch (Exception ex){
            return ResponseMsg.error(500, ex.getMessage());
        }
    }

    /**
     * 重新初始化关键计数记录
     */
    private void clearRedisKeys(){

        redisTemplate.opsForValue().set(RedisKeyIds.AVG_4G_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.AVG_MESH_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.AVG_SAT_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.AVG_LTE_R_DELAY_TIME_PREFIX, 0);

        redisTemplate.opsForValue().set(RedisKeyIds.TOTAL_4G_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.TOTAL_MESH_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.TOTAL_LTE_R_DELAY_TIME_PREFIX, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.TOTAL_SAT_DELAY_TIME_PREFIX, 0);

        redisTemplate.opsForValue().set(RedisKeyIds.COUNT_4G, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.COUNT_MESH, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.COUNT_LTE_R, 0);
        redisTemplate.opsForValue().set(RedisKeyIds.COUNT_SAT, 0);
    }
}