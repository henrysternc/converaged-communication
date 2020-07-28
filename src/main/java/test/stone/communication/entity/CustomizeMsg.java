package test.stone.communication.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CustomizeMsg {
    private Integer id;

    private String deviceCode;

    private String msgContent;

    private Date gmtCreate;
}