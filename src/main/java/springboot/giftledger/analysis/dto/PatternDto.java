package springboot.giftledger.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatternDto {
    private Map<Integer, Long> monthlyData;
    private Map<String, Long> weekdayData;
    private Map<String, EventTypeData> eventTypeData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventTypeData{
        private Integer count;
        private Long amount;
    }
}
