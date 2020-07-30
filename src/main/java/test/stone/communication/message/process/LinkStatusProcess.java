package test.stone.communication.message.process;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.stone.communication.dao.LinkStatusMapper;
import test.stone.communication.defines.RedisKeyIds;
import test.stone.communication.entity.LinkStatus;
import test.stone.communication.entity.message.LinkStatusMsg;
import test.stone.communication.message.SimpleMessage;

import java.util.Date;

@Service
@Slf4j
public class LinkStatusProcess extends AbstractProcess {

    @Autowired
    private LinkStatusMapper linkStatusMapper;

    @Override
    public void process(SimpleMessage simpleMessage) {

        LinkStatusMsg linkStatusMsg = (LinkStatusMsg) simpleMessage;

        LinkStatus build = LinkStatus.builder().link4gStatus((int) linkStatusMsg.getLink4gStatus())
                .linkLteRStatus((int) linkStatusMsg.getLinkLteRStatus())
                .linkMeshStatus((int) linkStatusMsg.getLinkMeshStatus())
                .linkSatStatus((int) linkStatusMsg.getLinkSatStatus())
                .gmtCreate(new Date()).build();

        linkStatusMapper.insertSelective(build);

        //更新redis中的值
        updateRedis(build);
        //推送到前端

        JSONObject result = new JSONObject();
        result.put("msgType", 25);
        result.put("linkStatus", build);
        try{
            webSocketServer.sendMessage(result.toJSONString());
        }catch (Exception ex){
            log.info(ex.getLocalizedMessage());
        }
    }


    private void updateRedis(LinkStatus linkStatus){
        redisTemplate.opsForValue().set(RedisKeyIds.LINK_STATUS_MESH, linkStatus.getLinkMeshStatus());
        redisTemplate.opsForValue().set(RedisKeyIds.LINK_STATUS_LTE_R, linkStatus.getLinkLteRStatus());
        redisTemplate.opsForValue().set(RedisKeyIds.LINK_STATUS_4G, linkStatus.getLink4gStatus());
        redisTemplate.opsForValue().set(RedisKeyIds.LINK_STATUS_SAT, linkStatus.getLinkSatStatus());
    }
}
