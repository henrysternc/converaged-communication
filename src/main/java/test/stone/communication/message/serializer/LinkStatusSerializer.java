package test.stone.communication.message.serializer;

import lombok.extern.slf4j.Slf4j;
import test.stone.communication.entity.message.LinkStatusMsg;
import test.stone.communication.message.SimpleMessage;

import java.nio.ByteBuffer;
/**
 * 处理链路状态消息 19 3c 4a 56 77 88 99 00 00 08 01 01 00 01 00 01 01 01
 * 协议结构 |消息类型 1位|mac地址 6位|占位符 1位|消息体长度 2位|消息体内容（各链路状态，每种占两位）|
 */
@Slf4j
public class LinkStatusSerializer extends SimpleSerializer {

    @Override
    public SimpleMessage deserialize(byte[] bytes) {
        if(checkBytes(bytes)){
            LinkStatusMsg req = (LinkStatusMsg) newInstance();
            int idx = 0;
            try {
                idx = deserializeHeader(req, bytes);
                //包装剩余的字节数据
                ByteBuffer bb = ByteBuffer.wrap(bytes, idx, bytes.length - idx);

                req.setLinkMeshStatus(bb.get());
                req.setLinkLteRStatus(bb.get());
                req.setLink4gStatus(bb.get());
                req.setLinkSatStatus(bb.get());
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
            LinkStatusMsg req = (LinkStatusMsg) simpleMessage;

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
                bb.put(req.getLinkMeshStatus());
                bb.put(req.getLinkLteRStatus());
                bb.put(req.getLink4gStatus());
                bb.put(req.getLinkSatStatus());

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
        return new LinkStatusMsg();
    }
}
