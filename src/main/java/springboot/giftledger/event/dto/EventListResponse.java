package springboot.giftledger.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventListResponse {
	
	private EventDto event;
	private String ownerName;
	private String relation;
	private String memo;

}
