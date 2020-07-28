/**
 * 
 */
package test.stone.communication.util;

import org.springframework.util.StringUtils;

/**
 * @author stone
 *
 * 2019年3月18日
 */
public class DataTypeConvertUtils {

    public static byte[] int2Bytes(int num) {
        byte[] byteNum = new byte[4];
        for (int ix = 0; ix < 4; ++ix) {
            int offset = 32 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static int bytes2Int(byte[] byteNum) {
        int num = 0;
        for (int ix = 0; ix < 4; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    public static byte int2OneByte(int num) {
        return (byte) (num & 0x000000ff);
    }

    public static int oneByte2Int(byte byteNum) {
        return byteNum > 0 ? byteNum : (128 + (128 + byteNum));
    }

    public static byte[] long2Bytes(long num) {
        byte[] byteNum = new byte[8];
        for (int ix = 0; ix < 8; ++ix) {
            int offset = 64 - (ix + 1) * 8;
            byteNum[ix] = (byte) ((num >> offset) & 0xff);
        }
        return byteNum;
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }
    
    /**
     * 单字符串转ascii 对照的byte值
     * 例:'a'-> 97
     * @param str
     * @return
     * @throws Exception
     */
    public static byte singleStr2Ascii(String str) throws Exception {
        if(StringUtils.isEmpty(str) || str.length() > 1) {
            throw new Exception("参数不合法");
        }
        char charAt = str.charAt(0);
        return (byte) charAt;
    }
    
    /**
     * ascii码转字符串
     * 例:97->'a'
     * @param b
     * @return
     */
    public static String ascii2String(byte b) throws Exception{
        if(b<0 || b>127) {
            throw new Exception("字节数不合法");
        }
        byte[] a = {b};
        return new String(a);
    }
    
    public static void main(String[] args) {
        short a = 90;
        byte b = (byte) a;
        try {
            String c = ascii2String(b);
            System.out.println(c);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        byte[] bytes = unsignedShortToByte2(1);
        System.out.println(bytes);

        int i = byte2ToUnsignedShort(bytes, 0);

        System.out.println(i);
    }


    public static byte[] unsignedShortToByte2(int s) {
        byte[] targets = new byte[2];
        targets[0] = (byte) (s >> 8 & 0xFF);
        targets[1] = (byte) (s & 0xFF);
        return targets;
    }

    public static int byte2ToUnsignedShort(byte[] bytes, int off) {
        int high = bytes[off];
        int low = bytes[off + 1];
        return (high << 8 & 0xFF00) | (low & 0xFF);
    }
}
