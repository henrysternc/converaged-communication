package test.stone.communication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class DelayDetail {

    private Integer id;

    private Integer linkType;

    private String deviceAddr;

    private Integer delayTime;

    private Date gmtCreate;
}