package springboot.giftledger.event.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springboot.giftledger.acquaintance.dto.AcquaintanceDto;
import springboot.giftledger.enums.EventType;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventDto {
	
    private Long eventId;
    private EventType eventType;
    private String eventName;
    private LocalDateTime eventDate;
    private String location;
    private Boolean isOwner;
	
	

}
