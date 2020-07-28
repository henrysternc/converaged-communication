package test.stone.communication.util;

/**
 * Created by xudb on 2017/10/20.
 */


public class HexUtils {
    static String TAG = "HexUtils";
    private final static String DIGITAL = "0123456789ABCDEF";

    public synchronized static String getHexStr(short id) {
        byte idb[] = new byte[2];
        idb[0] = (byte) (id >> 8 & 0xff);
        idb[1] = (byte) (id & 0xff);
        String hexStr = (new String(HexUtils.toHex(idb))).toUpperCase();
        return hexStr;
    }


    /**
     * Returns a hex string of <code>byte[]</code>, just for log
     */
    public synchronized static String byte2string(byte[] buff, int len) {
        StringBuffer sb = new StringBuffer();
        String x;
        for (int i = 0; i < len; i++) {
            x = Integer.toHexString(buff[i] & 0xff).toUpperCase();
            sb.append(x.length() == 1 ? "0" + x : x);
            sb.append(" ");
        }

        return sb.toString();
    }

    public static byte[] toHex(byte binary[], int offset, int length) {
        byte ret[] = new byte[length * 2];
        int i = offset;
        for (int j = 0; i < offset + length; j += 2) {
            int b = binary[i];
            ret[j + 0] = LETTERS[b >> 4 & 0xf];
            ret[j + 1] = LETTERS[b & 0xf];
            i++;
        }

        return ret;
    }

    public static byte[] toHex(byte binary[]) {
        return toHex(binary, 0, binary.length);
    }

    public synchronized static short byte2short(byte[] buf, int index, boolean littleEndian) {
        if (littleEndian)
            return getShortLittleEndian(buf, index);
        else
            return getShortBigEndian(buf, index);
    }

    /**
     * 大端模式
     *
     * @param buf   <code>byte[]</code> contain at lest 2 bytes to transfer a
     *              short value
     * @param index <code>int</code> from which the short value start
     * @return <code>short</code> value of Big Endian ordering, 0 if failed
     */
    private static short getShortBigEndian(byte[] buf, int index) {
        if ((buf == null) || (index < 0) || (index > (buf.length - 1)))
            return 0;

        return (short) ((buf[index] << 8) | (buf[index + 1] & 0xff));
    }

    /**
     * 小端模式
     *
     * @param buf   <code>byte[]</code> contain at lest 2 bytes to transfer a
     *              short value
     * @param index <code>int</code> from which the short value start
     * @return <code>short</code> value of Little Endian ordering, 0 if failed
     */
    private static short getShortLittleEndian(byte[] buf, int index) {
        if ((buf == null) || (index < 0) || (index > (buf.length - 1)))
            return 0;

        return (short) ((buf[index + 1] << 8) | (buf[index] & 0xff));
    }

    /**
     * Returns a hex string of <code>byte[]</code>, just for log
     */
    public synchronized static String byte2String(byte[] buff) {
        StringBuffer sb = new StringBuffer();
        String x;
        for (int i = 0; i < buff.length; i++) {
            x = Integer.toHexString(buff[i] & 0xff);
            sb.append(x.length() == 1 ? "0" + x : x);
            sb.append(" ");
        }

        return sb.toString();
    }


    /**
     * Returns a hex string of <code>byte[]</code>, just for log
     */
    public synchronized static String byte2String(byte[] buff, int length) {
        StringBuffer sb = new StringBuffer();
        String x;
        for (int i = 0; i < length; i++) {
            x = Integer.toHexString(buff[i] & 0xff);
            sb.append(x.length() == 1 ? "0" + x : x);
            sb.append(" ");
        }

        return sb.toString();
    }


    private static byte uniteBytes(String src0, String src1) {
        byte b0 = Byte.decode("0x" + src0).byteValue();
        b0 = (byte) (b0 << 4);
        byte b1 = Byte.decode("0x" + src1).byteValue();
        byte ret = (byte) (b0 | b1);
        return ret;
    }

    /**
     * @deprecated
     */
    public synchronized static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
 
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = uniteBytes(src.substring(i * 2, m), src.substring(m, n));
        }
        return ret;
    }

    /**
     * 从16进制编码转byte数组类型
     *
     * @author xudongbo
     */
    public synchronized static byte[] hex2byte(String hex) {
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = DIGITAL.indexOf(hex2char[2 * i]) * 16;
            temp += DIGITAL.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return bytes;
    }


    /**
     * 从16进制编码转short类型
     *
     * @author xudongbo
     */
    public synchronized static int hex2int(String hex) {
        byte[] packa = hex2byte(hex);
        return (getShortLittleEndian(packa, 0) & 0xffff);
    }


    /**
     * 将16位的short转换成byte数组
     *
     * @param s short
     * @return byte[] 长度为2
     */
    public synchronized static byte[] shortToByteArray(short s) {
        byte[] targets = new byte[2];
        for (int i = 0; i < 2; i++) {
            int offset = (targets.length - 1 - i) * 8;
            targets[i] = (byte) ((s >>> offset) & 0xff);
        }
        return targets;
    }


//    public synchronized static int byteToInt(byte[] b) {
//		int s = 0;
//		int s0 = b[0] & 0xff;//
//		int s1 = b[1] & 0xff;
//		int s2 = b[2] & 0xff;
//		int s3 = b[3] & 0xff;
//		s3 <<= 24;
//		s2 <<= 16;
//		s1 <<= 8;
//		s = s0 | s1 | s2 |s3 ;
//		return s;
//	}

//    /**
//     * 注意使用的为小端模式
//     * @deprecated
//     *
//     * */
//    public synchronized static short byteToShort(byte[] b) {
//    	short s = 0;
//    	short s0 =(short)( b[0] & 0xff);//
//    	short s1 = (short)(b[1] & 0xff);
//
//		s1 <<= 8;
//		s =(short) (s0 | s1)  ;
//		return s;
//	}

    private static byte LETTERS[] = {
            48, 49, 50, 51, 52, 53, 54, 55, 56, 57,
            97, 98, 99, 100, 101, 102
    };

}