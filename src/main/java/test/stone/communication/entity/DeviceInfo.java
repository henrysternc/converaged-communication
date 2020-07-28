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
public class DeviceInfo {
    private Integer id;

    private String deviceCode;

    private String deviceIp;

    private Integer devicePort;

    private Date gmtCreate;

    private String deviceDesc;
}