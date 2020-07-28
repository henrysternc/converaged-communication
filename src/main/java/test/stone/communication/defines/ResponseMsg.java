package test.stone.communication.defines;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class ResponseMsg<T>  implements Serializable  {
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String message;

    /**
     * 响应信息
     */
    private T data;

    /**
     * 构造成功消息
     *
     * @param <T> 返回数据类型
     * @return 消息
     */
    public static <T> ResponseMsg<T> success() {
        ResponseMsg msg = new ResponseMsg<T>(200, "成功", null);
        return msg;
    }

    public static <T> ResponseMsg<T> success(String message) {
        ResponseMsg msg = new ResponseMsg<T>(200, message, null);
        return msg;
    }

    /**
     * 构造成功消息
     *
     * @param data 返回数据
     * @param <T>  返回数据类型
     * @return 消息
     */
    public static <T> ResponseMsg<T> success(T data) {
        ResponseMsg msg = new ResponseMsg<T>(200, null, data);
        return msg;
    }

    /**
     * 构造错误消息
     *
     * @param errorCode    异常状态码
     * @param errorMessage 异常描述信息
     * @return 消息
     */
    public static ResponseMsg<?> error(Integer errorCode, String errorMessage) {
        ResponseMsg msg = new ResponseMsg(500, errorMessage, null);
        return msg;
    }

    /**
     * to json.
     *
     * @return
     */
    public String toJson() {
        return this == null ? "" : JSON.toJSONString(this);
    }


    /**
     * 私有构造器
     *
     * @param code    状态码
     * @param message 状态描述
     * @param data    数据
     */
    private ResponseMsg(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 获取异常描述信息
     *
     * @return 异常描述信息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 获取返回数据
     *
     * @return 返回数据
     */
    public T getData() {
        return data;
    }
}
