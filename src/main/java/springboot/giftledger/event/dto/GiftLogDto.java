package springboot.giftledger.event.dto;

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
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.entity.Event;
import springboot.giftledger.enums.ActionType;
import springboot.giftledger.enums.PayMethod;
import springboot.giftledger.enums.Relation;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GiftLogDto {
	
	private Long giftId;
	
	private Long eventId;
	
	private long amount;
	
	private ActionType actionType;
	
	private PayMethod payMethod;
	
	private String memo;

}
