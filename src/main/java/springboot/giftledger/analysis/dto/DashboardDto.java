package springboot.giftledger.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private Long totalGive;
    private Long totalTake;
    private Long balance;
    private double recoveryRate;
    private double yearChangePercent;
    private Long yearChangeAmount;

}
