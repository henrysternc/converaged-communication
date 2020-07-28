package test.stone.communication.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class SwitchStatusVO {

    private Integer linkSwitchMeshStatus;

    private Integer linkSwitchLteRStatus;

    private Integer linkSwitch4gStatus;

    private Integer linkSwitchSatStatus;
}
