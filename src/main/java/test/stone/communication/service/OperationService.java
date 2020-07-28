package test.stone.communication.service;

import com.alibaba.fastjson.JSONObject;
import test.stone.communication.defines.ResponseMsg;

public interface OperationService {

    ResponseMsg init();

    ResponseMsg complete();

    ResponseMsg getDetails(Integer id);

    ResponseMsg getList(Integer pageNum, Integer pageSize);

    ResponseMsg changeSwitch(Integer linkType, Integer status);

    ResponseMsg sendMsg(String msg);
}
