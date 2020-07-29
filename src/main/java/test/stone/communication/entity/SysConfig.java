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
public class SysConfig {

    private Integer id;

    private String name;

    private String value;

    private Date gmtCreate;
}