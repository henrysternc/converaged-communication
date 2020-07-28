package test.stone.communication.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class StatisticInfo {
    private Integer id;

    private Date startTime;

    private Date endTime;

    private Integer lteTotalDelay;

    private Integer lteRTotalDelay;

    private Integer meshTotalDelay;

    private Integer satTotalDelay;

    private BigDecimal lteAvgDelay;

    private BigDecimal lteRAvgDelay;

    private BigDecimal meshAvgDelay;

    private BigDecimal satAvgDelay;
}