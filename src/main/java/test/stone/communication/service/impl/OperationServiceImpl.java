package test.stone.communication.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import test.stone.communication.config.ExecutorConfig;
import test.stone.communication.dao.CustomizeMsgMapper;
import test.stone.communication.dao.StatisticInfoMapper;
import test.stone.communication.dao.SysConfigMapper;
import test.stone.communication.defines.RedisKeyIds;
import test.stone.communication.defines.ResponseMsg;
import test.stone.communication.entity.CustomizeMsg;
import test.stone.communication.entity.StatisticInfo;
import test.stone.communication.entity.SysConfig;
import test.stone.communication.entity.message.CustomizeMsgInfo;
import test.stone.communication.entity.message.SwitchMsg;
import test.stone.communication.message.serializer.RegSerializer;
import test.stone.communication.netty.ObuServer;
import test.stone.communication.service.OperationService;
import test.stone.communication.util.DataTypeConvertUtils;
import test.stone.communication.util.HexUtils;

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

    @Autowired
    private ExecutorConfig ExecutorConfig;

    @Autowired
    private SysConfigMapper sysConfigMapper;

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
        Object satTotalDelay = redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_SAT_DELAY_TIME_PREFIX);
        BigDecimal satBigDecimalTotalDelay = new BigDecimal(satTotalDelay.toString());
        Object meshTotalDelay = redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_MESH_DELAY_TIME_PREFIX);
        BigDecimal meshBigDecimalTotalDelay = new BigDecimal(meshTotalDelay.toString());
        Object LteRTotalDelay = redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_LTE_R_DELAY_TIME_PREFIX);
        BigDecimal LteRBigDecimalTotalDelay = new BigDecimal(LteRTotalDelay.toString());
        Object LteTotalDelay = redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_4G_DELAY_TIME_PREFIX);
        BigDecimal LteBigDecimalTotalDelay = new BigDecimal(LteTotalDelay.toString());

        Object avg4gDelay = redisTemplate.opsForValue().get(RedisKeyIds.AVG_4G_DELAY_TIME_PREFIX);
        Object avgMeshDelay = redisTemplate.opsForValue().get(RedisKeyIds.AVG_MESH_DELAY_TIME_PREFIX);
        Object avgLteRDelay = redisTemplate.opsForValue().get(RedisKeyIds.AVG_LTE_R_DELAY_TIME_PREFIX);
        Object avgSATDelay = redisTemplate.opsForValue().get(RedisKeyIds.TOTAL_SAT_DELAY_TIME_PREFIX);

        StatisticInfo build = StatisticInfo.builder().id(currentId)
                .satTotalDelay(satBigDecimalTotalDelay.intValue())
                .meshTotalDelay(meshBigDecimalTotalDelay.intValue())
                .lteTotalDelay(LteRBigDecimalTotalDelay.intValue())
                .lteRTotalDelay(LteBigDecimalTotalDelay.intValue())
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
            //Object o = redisTemplate.opsForValue().get(RedisKeyIds.CONTROL_MACHINE);
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
        Integer sendSwitch = (Integer) redisTemplate.opsForValue().get(RedisKeyIds.sendSwitch);
        if(sendSwitch == 0 && StringUtils.isBlank(msg)){
            return ResponseMsg.error(500,"消息不可为空");
        }

        if(sendSwitch == 0 && msg.getBytes().length > 1024){
            return ResponseMsg.error(500,"消息过长");
        }

        if(sendSwitch == 1){
            //如果当前是开启状态，修改为关闭状态并 返回成功
            redisTemplate.opsForValue().set(RedisKeyIds.sendSwitch, 0);
            return ResponseMsg.success("停止发送成功");
        }else{
            //如果当前是关闭状态，修改为可发送状态
            redisTemplate.opsForValue().set(RedisKeyIds.sendSwitch, 1);
        }
        try{
            Long cycleTime = 200L;

            List<SysConfig> list = sysConfigMapper.getList();

            for(int i=0;i<list.size();i++){
                SysConfig sysConfig = list.get(i);
                if("cycle_time".equals(sysConfig.getName())){
                    cycleTime = Long.parseLong(sysConfig.getValue());
                    break;
                }
            }

            CustomizeMsgInfo customizeMsgInfo = new CustomizeMsgInfo();
            customizeMsgInfo.setCustomizeInfo(msg);

            byte[] deviceCode = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
            customizeMsgInfo.getHeader().setDeviceCode(deviceCode);
            byte[] bytes = RegSerializer.serialize(customizeMsgInfo);
            //使用新线程去执行发送任务
            ExecutorConfig.asyncPromiseExecutor().execute((new Send(bytes, obuServer, redisTemplate, cycleTime)));
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


@Slf4j
class Send extends Thread{

    private byte[] bytes;

    private ObuServer obuServer;

    private RedisTemplate redisTemplate;

    private Long cycleTime;

    public Send(byte[] bytes, ObuServer obuServer, RedisTemplate redisTemplate, Long cycleTime){
        this.bytes = bytes;
        this.obuServer = obuServer;
        this.redisTemplate = redisTemplate;
        this.cycleTime = cycleTime;
    }
    @Override
    public void run(){
        for(;;){
            Object o = redisTemplate.opsForValue().get(RedisKeyIds.sendSwitch);
            if(Integer.parseInt(o.toString()) == 1){
                //不停地发送
                try{
                    obuServer.publish(bytes);
                    String s = HexUtils.byte2String(bytes);
                    log.info("发送了内容：{}", s);
                    Thread.sleep(cycleTime);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else{
                break;
            }
        }

    }
}