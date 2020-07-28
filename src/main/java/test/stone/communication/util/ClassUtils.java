package test.stone.communication.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


import lombok.extern.slf4j.Slf4j;

/**
 * Created by xudb on 2017/11/11.
 */
@Slf4j
public class ClassUtils {

    static String TAG = "ClassUtils";

    /**
     * 计算类中所有字段长度
     * 可以计算 int,short,byte,String,byte[]
     *
     * @param object 实例化对象
     */
    public static int getFieldLength(Object object) throws Exception {
        int totalLength = 0;

        // 拿到该类
        Class<?> clz = object.getClass();
        // 获取实体类的所有属性，返回Field数组
        Field[] fields = clz.getDeclaredFields();

        for (Field field : fields) {
            if (field.getName().toLowerCase().equals("serialversionuid") || field.getName().toLowerCase().equals("$change"))
                continue;

            if (field.getGenericType().toString().equals(
                    "byte")) {
                totalLength += 1;
            } else if (field.getGenericType().toString().equals(
                    "int")) {
                totalLength += 4;
            } else if (field.getGenericType().toString().equals(
                    "short")) {
                totalLength += 2;
            }
            // 如果类型是String
            else if (field.getGenericType().toString().equals(
                    "class java.lang.String")) {
                // 如果type是类类型，则前面包含"class "，后面跟类名
                // 拿到该属性的gettet方法
                //他是根据拼凑的字符来找你写的getter方法的
                //在Boolean值的时候是isXXX（默认使用ide生成getter的都是isXXX）
                //如果出现NoSuchMethod异常 就说明它找不到那个gettet方法 需要做个规范

                Method m = (Method) object.getClass().getMethod(
                        "get" + getMethodName(field.getName()));

                String val = (String) m.invoke(object);// 调用getter方法获取属性值
                if (val != null) {
                    totalLength += val.getBytes().length;
                }
            }
            // byte 一维数组
            else if (field.getGenericType().toString().equals(
                    "class [B")) {
                Method m = (Method) object.getClass().getMethod(
                        "get" + getMethodName(field.getName()));

                byte[] val = (byte[]) m.invoke(object);// 调用getter方法获取属性值
                if (val != null) {
                    totalLength += val.length;
                }
            }
            // 仅 byte 二维数组 2018-03-27
            else if (field.getGenericType().toString().equals(
                    "class [[B")) {
                Method m = (Method) object.getClass().getMethod(
                        "get" + getMethodName(field.getName()));

                byte val[][] = (byte[][]) m.invoke(object);// 调用getter方法获取属性值
                if (val != null) {
                    for (int i = 0; i < val.length; i++)
                        totalLength += val[i].length;
                }
            } else if (field.getGenericType().toString().equals(
                    "long")) {
                totalLength += 8;
            } else if (field.getGenericType().toString().equals(
                    "class java.lang.Integer")) {
                totalLength += 4;
            } else if (field.getGenericType().toString().equals(
                    "class java.lang.Double")) {
                totalLength += 8;
            } else if (field.getGenericType().toString().equals(
                    "class java.lang.Short")) {
                totalLength += 2;
            } else if (field.getGenericType().toString().equals(
                    "class java.lang.Long")) {
                totalLength += 8;
            } else if (field.getGenericType().toString().equals(
                    "class java.lang.Boolean")) {
                totalLength += 1;
            }
            // 如果类型是boolean 基本数据类型不一样 这里有点说名如果定义名是 isXXX的 那就全都是isXXX的
            // 反射找不到getter的具体名
            else if (field.getGenericType().toString().equals("boolean")) {
                totalLength += 1;
            } else if (field.getGenericType().toString().indexOf("com.rtzt.protocol.message.motorcade.MemberInfo") >= 0) {
//              简单处理，写死了
                totalLength += 29;
            }
            // 如果类型是Date
//            else if (field.getGenericType().toString().equals(
//                    "class java.util.Date")) {
//
//            }
            else {
            	log.error(TAG, new StringBuilder().append("### ").append(field.getGenericType().toString()).append(" 数据类型未处理").toString());
            }
        }

        return totalLength;
    }

    // 把一个字符串的第一个字母大写、效率是最高的、
    private static String getMethodName(String fildeName) throws Exception {
        byte[] items = fildeName.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }
}
