package springboot.giftledger.acquaintance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AcquaintanceDto {
	
    private Long acquaintanceId;
    private Long memberId;
    private String name;
    private String relation;
    private String groupName;
    private String phone;

    
}
