package test.stone.communication.entity;

import lombok.Data;

import java.util.Date;

@Data
public class ControlInfo {
    private Integer id;

    private Integer linkType;

    private Integer controlType;

    private Date gmtCreate;
}