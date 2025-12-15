package springboot.giftledger.event.service;

import org.springframework.data.domain.Pageable;

import springboot.giftledger.common.dto.ResultDto;
import springboot.giftledger.event.dto.EventDetailsResultDto;
import springboot.giftledger.event.dto.EventRequestDto;
import springboot.giftledger.event.dto.EventResultDto;
import springboot.giftledger.event.dto.EventUpdateRequest;

public interface EventService {
    EventResultDto insertEvent(String email, EventRequestDto eventRequestDto);

    EventResultDto deleteEvent(String email, Long eventId);

    EventDetailsResultDto detailsEvent(String email, Long eventId);

    EventDetailsResultDto insertEventOnDetails(String email, Long eventId, EventRequestDto eventRequestDto);

}
