package springboot.giftledger.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import springboot.giftledger.enums.Relation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcquaintanceDto {

    private Long acquaintanceId;
    private Long memberId;
    private String name;
    private Relation relation;
    private String groupName;
    private String phone;
}
