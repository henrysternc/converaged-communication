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
public class LinkStatus {
    private Integer id;

    private Integer switchMeshStatus;

    private Integer switch4gStatus;

    private Integer switchLteRStatus;

    private Integer switchSatStatus;

    private Integer linkMeshStatus;

    private Integer link4gStatus;

    private Integer linkLteRStatus;

    private Integer linkSatStatus;

    private Date gmtCreate;
}