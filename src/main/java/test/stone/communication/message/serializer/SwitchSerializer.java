package test.stone.communication.message.serializer;

import lombok.extern.slf4j.Slf4j;
import test.stone.communication.entity.message.SwitchMsg;
import test.stone.communication.message.SimpleMessage;
import test.stone.communication.util.DataTypeConvertUtils;

import java.nio.ByteBuffer;

@Slf4j
public class SwitchSerializer extends SimpleSerializer {
    @Override
    public SimpleMessage deserialize(byte[] bytes) {
        if(checkBytes(bytes)){
            SwitchMsg req = (SwitchMsg) newInstance();
            int idx = 0;
            try {
                idx = deserializeHeader(req, bytes);
                //包装剩余的字节数据
                ByteBuffer bb = ByteBuffer.wrap(bytes, idx, bytes.length - idx);

                req.setLinkId(bb.get());
                req.setLinkSwitchStatus(bb.get());
                return req;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public byte[] serialize(SimpleMessage simpleMessage) {
        if(checkSimpleMessage(simpleMessage)){
            SwitchMsg switchMsg = (SwitchMsg) simpleMessage;

            ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
            byte[] ret = new byte[BUFFER_SIZE];
            int idx = 0;
            try{
                idx = serializeHeader(switchMsg, ret);
                if(idx == -1){
                    return null;
                }
                bb.put(ret, 0, idx);
                bb.put(switchMsg.getLinkId());
                bb.put(switchMsg.getLinkSwitchStatus());
                int size = bb.position();
                bb.rewind();
                ret = bb.array();
                ret = trim(ret, size);
                return ret;
            }catch (Exception ex){
                log.info(ex.getLocalizedMessage());
            }
        }
        return null;
    }

    @Override
    public SimpleMessage newInstance() {
        return new SwitchMsg();
    }
}
