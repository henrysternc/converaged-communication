package test.stone.communication.message;

public interface MessageCode {

    byte MESH = 0x01; //Mesh

    byte LTE_R = 0x02; //LTRE-R

    byte LTE = 0x03; //4G

    byte SAT = 0x04; //SAT

    byte SWITCH_CONTROL = 0x17; //链路开关信息

    byte MSG_PUSH = 0x18; //传输数据推送

    byte LINK_STATUS = 0x19; //链路状态

    byte DEVICE_INFO = 0x1a; //设备列表

    byte DELAY_TIME = 0x1b; //链路延时信息推送
}
