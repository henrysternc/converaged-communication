package test.stone.communication.message.serializer;

import lombok.extern.slf4j.Slf4j;
import test.stone.communication.entity.message.CustomizeMsgInfo;
import test.stone.communication.message.SimpleMessage;

import java.nio.ByteBuffer;

@Slf4j
public class CustomizeSerializer extends SimpleSerializer {

    @Override
    public SimpleMessage deserialize(byte[] bytes) {
        if(checkBytes(bytes)){
            CustomizeMsgInfo req = (CustomizeMsgInfo) newInstance();
            int idx = 0;
            try {
                idx = deserializeHeader(req, bytes);
                //包装剩余的字节数据
                byte[] messages = new byte[bytes.length - idx];
                for(int i=0;i<bytes.length-idx;i++){
                    messages[i] = bytes[idx+i];
                }
                req.setCustomizeInfo(new String(messages,"utf-8"));
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
            CustomizeMsgInfo switchMsg = (CustomizeMsgInfo) simpleMessage;

            ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
            byte[] ret = new byte[BUFFER_SIZE];
            int idx = 0;
            try{
                idx = serializeHeader(switchMsg, ret);
                if(idx == -1){
                    return null;
                }
                bb.put(ret, 0, idx);
                //消息内容
                bb.put(((CustomizeMsgInfo) simpleMessage).getCustomizeInfo().getBytes());

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
        return new CustomizeMsgInfo();
    }
}
