package test.stone.communication.message.serializer;

import lombok.extern.slf4j.Slf4j;
import test.stone.communication.entity.message.DeviceInfoMsg;
import test.stone.communication.message.SimpleMessage;
import test.stone.communication.util.IdentityUtils;

import java.nio.ByteBuffer;

@Slf4j
public class DeviceInfoSerializer extends SimpleSerializer {

    @Override
    public SimpleMessage deserialize(byte[] bytes) {
        if(checkBytes(bytes)){
            DeviceInfoMsg req = (DeviceInfoMsg) newInstance();
            int idx = 0;
            try {
                idx = deserializeHeader(req, bytes);
                //包装剩余的字节数据
                ByteBuffer bb = ByteBuffer.wrap(bytes, idx, bytes.length - idx);

                byte[] obuMac = new byte[SimpleMessage.DEVICE_CODE_LENGTH];
                for(int i = 0;i<SimpleMessage.DEVICE_CODE_LENGTH;i++){
                    obuMac[i] = bytes[idx+i];
                }

                req.setObuMac(IdentityUtils.makeIdentityString(obuMac));

                byte[] obuIp = new byte[SimpleMessage.IP_LENGTH];
                for(int i=0;i<SimpleMessage.IP_LENGTH;i++){
                    obuIp[i] = bytes[i+idx+SimpleMessage.DEVICE_CODE_LENGTH];
                }
                req.setObuIp(IdentityUtils.bytes2IpV4(obuIp));
                req.setPort(bb.getShort());
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
            DeviceInfoMsg req = (DeviceInfoMsg) simpleMessage;

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
                bb.put(IdentityUtils.makeIdentityByte(req.getObuMac()));
                //bb.put() //ip 18位
                bb.putShort(req.getPort());
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
        return new DeviceInfoMsg();
    }
}
