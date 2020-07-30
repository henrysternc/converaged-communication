package test.stone.communication.message.serializer;

import lombok.extern.slf4j.Slf4j;
import test.stone.communication.entity.message.DelayTimeMsg;
import test.stone.communication.message.SimpleMessage;
import test.stone.communication.util.DataTypeConvertUtils;
import test.stone.communication.util.HexUtils;

import java.nio.ByteBuffer;

/**
 *
 * 协议结构 |消息类型 1位|mac地址 6位|占位符 1位|消息体长度 2位|消息体内容（LinkId 1位 | 延时长度 15位 单位 ns）|
 */
@Slf4j
public class DelayTimeSerializer extends SimpleSerializer {

    @Override
    public SimpleMessage deserialize(byte[] bytes) {
        if(checkBytes(bytes)){
            DelayTimeMsg req = (DelayTimeMsg) newInstance();
            int idx = 0;
            try {
                idx = deserializeHeader(req, bytes);
                //包装剩余的字节数据
                ByteBuffer bb = ByteBuffer.wrap(bytes, idx, bytes.length - idx);

                req.setLinkId(bb.get());
                //req.setTransDelay(bb.getLong());
                byte[] transDelay = new byte[SimpleMessage.TRANS_DELAY];
                for(int i=0;i<SimpleMessage.TRANS_DELAY;i++){
                    transDelay[i] = bytes[idx+1+i];
                }
                String s = HexUtils.byte2String(transDelay);
                String s1 = s.replaceAll(" ", "");
                System.out.println(s);
                req.setTransDelay(Long.parseLong(s1, 16));
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
            DelayTimeMsg req = (DelayTimeMsg) simpleMessage;

            ByteBuffer bb = ByteBuffer.allocate(BUFFER_SIZE);
            byte[] ret = new byte[BUFFER_SIZE];
            int idx = 0;
            try{
                idx = serializeHeader(req, ret);
                if(idx == -1){
                    return null;
                }
                bb.put(ret, 0, idx);
                //消息内容
                bb.put(req.getLinkId());


                byte[] bytes = new byte[SimpleMessage.TRANS_DELAY];
                bytes = DataTypeConvertUtils.long2Bytes(req.getTransDelay());
                bb.put(bytes);

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
        return new DelayTimeMsg();
    }
}
