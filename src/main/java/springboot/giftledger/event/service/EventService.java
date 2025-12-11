package springboot.giftledger.event.service;

import org.springframework.data.domain.Pageable;

import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.event.dto.EventUpdateRequest;

public interface EventService {
	
	ResultDto updateEvent(long evnetId, EventUpdateRequest req, String userName);
	ResultDto eventList(String email, Pageable pageable);

}
