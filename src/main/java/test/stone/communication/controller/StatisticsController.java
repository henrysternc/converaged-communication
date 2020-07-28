package test.stone.communication.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import test.stone.communication.defines.ResponseMsg;
import test.stone.communication.service.OperationService;

@RestController
public class StatisticsController {

    @Autowired
    private OperationService operationService;

    /**
     * 查询本次详情
     * @return
     */
    @GetMapping("/getDetails/{id}")
    public ResponseMsg showDetails(@PathVariable("id")Integer id){

        return operationService.getDetails(id);
    }

    /**
     * 初始化
     * 清空redis
     *
     * @return
     */
    @GetMapping("/init")
    public ResponseMsg init(){
        return operationService.init();
    }

    /**
     * 完结
     * 最终的数据入库（StatisticsInfo）
     * @return
     */
    @GetMapping("/complete")
    public ResponseMsg complete(){
        return operationService.complete();
    }

    /**
     * 列表查询list
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/getList/{pageNum}/{pageSize}")
    public ResponseMsg getList(@PathVariable("pageNum") Integer pageNum,
                               @PathVariable("pageSize") Integer pageSize){
        return operationService.getList(pageNum, pageSize);
    }

    @PutMapping("/changeSwitch")
    public ResponseMsg changeSwitch(@RequestBody JSONObject jsonObject){
        return operationService.changeSwitch(jsonObject.getInteger("linkType"), jsonObject.getInteger("status"));
    }

    @PostMapping("/sendMsg")
    public ResponseMsg sendMsg(@RequestBody JSONObject jsonObject){
        return operationService.sendMsg(jsonObject.getString("msg"));
    }
}
