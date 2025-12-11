package springboot.giftledger.acquaintance.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.giftledger.entity.Member;
import springboot.giftledger.enums.Relation;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AcquaintanceDto {
	
    private Long acquaintanceId;
    private String name;
    private Relation relation;
    private String groupName;
    private String phone;

    
    private Long memberId;
}
