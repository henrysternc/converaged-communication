package test.stone.communication.message;

import java.io.Serializable;
import java.util.Arrays;

public abstract class SimpleMessage implements Serializable {

    /**
     * 消息的构成是 消息类型 1字节+设备MAC地址 6字节+消息长度 2字节+消息内容
     */
    public final static int MSG_TYPE_IDX = 0;

    public final static int MSG_TYPE_LENGTH = 1;

    public final static short DEVICE_CODE_LENGTH = 6;

    /**
     * msg_header length is msg_type + device_code + msg_length
     */
    public final static short HEADER_LENGTH = 10;

    public final static int IP_LENGTH = 18;

    public final static int TRANS_DELAY = 15;



    public class Header implements Serializable{
        private byte msgType;
        private byte[] deviceCode;

        private byte placeHolder;
        private short msgLength;

        public Header(){}

        public Header(byte msgType){
            //setHeader(new Header(msgType));
            init(msgType);
        }

        public Header(byte msgType, short msgLength){
            init(msgType);
            this.msgLength = msgLength;
        }

        private void init(byte msgType){
            this.msgType = msgType;
            this.deviceCode = new byte[DEVICE_CODE_LENGTH];
            this.placeHolder = 0x00;
        }

        public byte getMsgType() {
            return msgType;
        }

        public void setMsgType(byte msgType) {
            this.msgType = msgType;
        }

        public byte[] getDeviceCode() {
            return deviceCode;
        }

        public void setDeviceCode(byte[] deviceCode) {
            this.deviceCode = deviceCode;
        }

        public short getMsgLength() {
            return msgLength;
        }

        public void setMsgLength(short msgLength) {
            this.msgLength = msgLength;
        }
        public byte getPlaceHolder(){
            return placeHolder;
        }

        public void setPlaceHolder(){
            placeHolder = 0x00;
        }
        @Override
        public String toString() {
            return "Header{" +
                    "deviceCode=" + Arrays.toString(deviceCode) +
                    ", msgType=" + msgType +
                    ", msgLength=" + msgLength +
                    '}';
        }
    }

    private Header header;

    public SimpleMessage(){}

    public static int getMsgType() {
        return MSG_TYPE_IDX;
    }

    public static short getDeviceCode() {
        return DEVICE_CODE_LENGTH;
    }

    public static short getHeaderLength() {
        return HEADER_LENGTH;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }
}
