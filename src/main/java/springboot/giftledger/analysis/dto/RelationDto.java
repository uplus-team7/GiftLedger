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
public class RelationDto {
    private List<TopRelation> topRelations;
    private List<RelationRecovery> relationRecovery;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopRelation{
        private String name;
        private Long totalAmount;
        private Integer eventCount;
        private Long avgAmount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelationRecovery{
        private String name;
        private Long give;
        private Long take;
        private Double rate;
    }
}
