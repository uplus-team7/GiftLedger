package springboot.giftledger.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryDto {
    private Double totalRecovery;              // 전체 회수율 (%)
    private Long unrecovered;                  // 미회수 금액
    private List<UnrecoveredRelation> unrecoveredList;     // 미회수 지인 리스트
    private List<UnrecoveredRelation> longTermWarning;     // 장기 미회수 경고 (180일 이상)

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnrecoveredRelation {
        private String name;        // 지인 이름
        private Long amount;        // 미회수 금액 (준 금액 - 받은 금액)
        private String date;        // 마지막 GIVE 날짜
        private Long days;          // 경과 일수
    }
}